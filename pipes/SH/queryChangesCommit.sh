#!/bin/bash
echo "BEGIN CAMBIOS DE PROYECTO"
NOW=$(date +"%Y%m%d%H%M%S")
git --version

git diff --name-only HEAD~1..HEAD
git diff --name-only HEAD~1..HEAD> cambios.txt
awk '{split($0,a,"/"); print a[2]}' cambios.txt > directorios.txt
awk '!seen[$0]++' directorios.txt > $BUILD_ARTIFACTSTAGINGDIRECTORY/proyectos_$NOW.txt
ls -al  $BUILD_ARTIFACTSTAGINGDIRECTORY
echo "PROYECTOS"
cat $BUILD_ARTIFACTSTAGINGDIRECTORY/proyectos_$NOW.txt
echo "FIN"