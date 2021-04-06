# SCRIPT PARA USAR LAST TAG GIT como buildnumber
# este script actualiza la variable de azure repos buildnumber por lo tanto solo es util
# para lanzarlo desde los pipes de azure devops
# se tiene que crear la invocacion al script y seleccionar el script 
IFS="-"
version=$(git describe --abbrev=0 --tags)
set $version
newbuildnumber=$1
echo $newbuildnumber
echo "##vso[build.updatebuildnumber]$newbuildnumber"