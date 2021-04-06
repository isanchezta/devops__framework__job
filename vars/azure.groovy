/**
 * Inicia la conexión con instancia de service principal de Azure Cloud
 * 
 * Ejemplo de uso
 * 
 * azure.login(credential)
 * 
 * @param credential     -> Nombre de la credencial de Service Principal almacenada en Jenkins
 */
def login(credential = env.AZURE_SERVICEPRINCIPAL_CREDENTIAL){
    withCredentials([azureServicePrincipal(credential)]) {
        sh 'az login --service-principal --username=$AZURE_CLIENT_ID --password=$AZURE_CLIENT_SECRET --tenant=$AZURE_TENANT_ID'
    }
}

/**
 * Finaliza la conexión existente
 * 
 * Ejemplo de uso
 * 
 * azure.logout()
 * 
 */
def logout(credential = env.AZURE_SERVICEPRINCIPAL_CREDENTIAL){
    withCredentials([azureServicePrincipal(credential)]) {
        sh 'az logout --username=$AZURE_CLIENT_ID'
    }    
}

// ######################################## WEBAPP ######################################## //

/**
 * Parametriza acciona relacionadas con el command line de az webapp
 * 
 * Ejemplo de uso
 * 
 * azure.webappAction(action, resourceGroup, webappName)
 * 
 * @param action           -> Acción a realizar (stop, start, ls)
 * @param resourceGroup    -> Resource Group al que pertenece la WebApp
 * @param webappName	   -> Nombre de la WebApp
 * @param parameters       -> Parámetros adicionales opcionales
 */
def webappAction(action, resourceGroup, webappName, parameters = "" ){
    res_group = resourceGroup != "" ? "--resource-group ${resourceGroup}" : ""
    web_name = webappName != "" ? "--name ${webappName}" : ""
    
    sh "az webapp ${action} ${res_group} ${web_name} ${parameters}"
}

/**
 * Inicia una webapp asociada a un resource group
 * 
 * Ejemplo de uso
 * 
 * azure.webappStart(resourceGroup, webappName)
 * 
 * @param resourceGroup    -> Resource Group al que pertenece la WebApp
 * @param webappName	   -> Nombre de la WebApp
 * @param slot	           -> Nombre de slot
 * @param targetSlot	   -> Nombre de target-slot
 */
def webappStart(resourceGroup, webappName, slot = "", targetSlot = ""){
    source_slot = slot != "" ? "--slot=${slot}" : ""
    target_slot = targetSlot != "" ? "--target-slot=${targetSlot}" : ""

    webappAction("start", resourceGroup, webappName, "${source_slot} ${target_slot}")
}

/**
 * Para una webapp asociada a un resource group
 * 
 * Ejemplo de uso
 * 
 * azure.webappStart(resourceGroup, webappName)
 * 
 * @param resourceGroup    -> Resource Group al que pertenece la WebApp
 * @param webappName	   -> Nombre de la WebApp
 * @param slot	           -> Nombre de slot
 */
def webappStop(resourceGroup, webappName, slot = ""){
    source_slot = slot != "" ? "--slot=${slot}" : ""

    webappAction("stop", resourceGroup, webappName, source_slot)
}

/**
 * Configura una webapp asociada a un resource group con una imagen. Esta puede pertenecer a un registry privado
 * 
 * Ejemplo de uso
 * 
 * azure.webappConfigContainer(resourceGroup, webappName, registryUser, registryPass)
 * 
 * @param resourceGroup    -> Resource Group al que pertenece la WebApp
 * @param webappName	   -> Nombre de la WebApp
 * @param registryUser     -> Nombre de usuario de acceso al registry
 * @param registryPass	   -> Contraseña de acceso al registry
 */
def webappConfigContainer(resourceGroup, webappName, imageName = "articloudpro-${env.ARTIFACTORY_REGISTRY}.jfrog.io/${env.PRJ.toLowerCase()}:${new frmwork.Utils().gitDescribe().split("-")[0]}", credential){
    withCredentials([usernamePassword(credentialsId: credential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        webappAction("config container set", resourceGroup, webappName, "--docker-custom-image-name=${imageName} --docker-registry-server-password=$PASSWORD --docker-registry-server-user=$USERNAME")
    }
}

/**
 * Configura una webapp asociada a un resource group con una imagen. Esta puede pertenecer a un registry privado
 * 
 * Ejemplo de uso
 * 
 * azure.webappConfigAppSettings(resourceGroup, webappName, parameters)
 * 
 * @param resourceGroup    -> Resource Group al que pertenece la WebApp
 * @param webappName	   -> Nombre de la WebApp
 * @param parameters       -> Nombre de usuario de acceso al registry
 */
def webappConfigAppSettings(resourceGroup, webappName, parameters=""){
    res_group = resourceGroup != "" ? "--resource-group ${resourceGroup}" : ""
    web_name = webappName != "" ? "--name ${webappName}" : ""
    
    sh ( returnStdout: false, script: """
        set +x
        az webapp config appsettings set ${res_group} ${web_name} ${parameters}
        set -x
        """)
}

// ######################################## AKS ######################################## //

/**
 * Parametriza acciona relacionadas con el command line de az aks
 * 
 * Ejemplo de uso
 * 
 * azure.aksAction(action, resourceGroup, aksName)
 * 
 * @param action           -> Acción a realizar (stop, start, ls)
 * @param resourceGroup    -> Resource Group al que pertenece el cluster de aks
 * @param webappName	   -> Nombre del aks
 * @param parameters       -> Parámetros adicionales opcionales
 */
def aksAction(action, resourceGroup, aksName, parameters = "" ){
    res_group = resourceGroup != "" ? "--resource-group ${resourceGroup}" : ""
    web_name = webappName != "" ? "--name ${webappName}" : ""
    
    sh "az aks ${action} ${res_group} ${web_name} ${parameters}"
}

/**
 * Parametriza acciona relacionadas con el command line de az aks
 * 
 * Ejemplo de uso
 * 
 * azure.aksGetCredentials(resourceGroup, aksName, "--admin")
 * 
 * @param resourceGroup    -> Resource Group al que pertenece el cluster de aks
 * @param aksName	       -> Nombre del aks
 * @param admin            -> Sets --admin param
 */
def aksGetCredentials(resourceGroup, aksName, parameters = ""){
    res_group = resourceGroup != "" ? "--resource-group ${resourceGroup}" : ""
    aks_name = aksName != "" ? "--name ${aksName}" : ""
    
    sh "az aks get-credentials ${res_group} ${aks_name} -f ./.kubeconfig ${parameters}"
   	env.KUBECONFIG="${pwd()}/.kubeconfig"
}
