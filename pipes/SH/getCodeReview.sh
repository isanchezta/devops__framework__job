# SCRIPT PARA Validar si se ejecuta Sonar
#!/bin/bash
Folder_JF="$(System.DefaultWorkingDirectory)/pipes/Jenkinsfile"
Valor="True"
tag=$(git describe --abbrev=0 --tags)

if [[ -f "$Folder_JF" ]]; then
    condition=$(grep "CODE_REVIEW=true" "$Folder_JF")

    if [ "$condition" ]; then
        echo "##vso[task.setvariable variable=ShouldRun_Sonar]$Valor"
        echo "Valor: $Valor"
    fi
fi

echo "Directorio: $Folder_JF"
echo "Condicion:""$(echo -e "${condition}" | sed -e 's/^[[:space:]]*//')"
echo "Tag: $tag"
echo "Version: $(Build.BuildNumber)"
echo "Branch: $(Build.SourceBranchName)"