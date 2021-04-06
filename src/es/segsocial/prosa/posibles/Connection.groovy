package es.segsocial.prosa

class Connection {
	String body="";
    String message="";
    String cookie="";
    String url="";
    String userid="";
    String pw="";
    Integer statusCode;
    boolean failure = false;

	def parseResponse(HttpURLConnection connection){
        this.statusCode = connection.responseCode;
        this.message = connection.responseMessage;
        this.failure = false;

        if(statusCode == 200 || statusCode == 201){
            this.body = connection.content.text;//this would fail the pipeline if there was a 400
        }else{
            this.failure = true;
            this.body = connection.getErrorStream().text;
        }

		if (cookie.length() == 0) {
			String headerName=null;

			for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++) {
				if (headerName.equals("Set-Cookie")) {
					String c = connection.getHeaderField(i);
					cookie += c + "; ";
				}
			}
		}
    }

	def consultaVersiones(String requestUrl, String verb){

        URL url = new URL(requestUrl);
        HttpURLConnection connection = url.openConnection();

        connection.setRequestMethod(verb);
        connection.setRequestProperty("Content-Type", "application/json");
        if (cookie.length() > 0)
          connection.setRequestProperty("Cookie", cookie);
        connection.doOutput = true;

        //post the request
        connection.connect();

        //parse the response
        parseResponse(connection);

        if(failure){
            error("\n$verb to URL: $requestUrl\n    JSON: $json\n    HTTP Status: $statusCode\n    Message: $message\n    Response Body: $body");
            failure = true
        }

    }
}
