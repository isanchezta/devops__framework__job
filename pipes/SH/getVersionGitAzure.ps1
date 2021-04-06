# SCRIPT PARA USAR LAST TAG GIT como buildnumber
# este script actualiza la variable de azure repos buildnumber por lo tanto solo es util
# para lanzarlo desde los pipes de azure devops
# se tiene que crear la invocacion al script y seleccionar el script 

# Establecemos el workspace de git como directorio sobre el que ejecutar comandos
Write-Host "$Env:BUILD_SOURCESDIRECTORY"
Set-Location -Path "$Env:BUILD_SOURCESDIRECTORY"

# Extraemos el ultimo tag, descartando el hito: (Ejemplo: 1.0.1)
$CurrentVersion = (git describe --tags --abbrev=0)
# Si devuelve un error sobre git, es que no está en el workspace correcto
$SlimVersion = $CurrentVersion.Split("-",2)[0]
Write-Host "Se establece la versión <$SlimVersion>"

# Asignamos a la variable por defecto que espera la pipeLine
Write-Output "##vso[build.updatebuildnumber]$SlimVersion"