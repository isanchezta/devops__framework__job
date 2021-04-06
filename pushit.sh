#!/bin/bash
mvn -f AccionaFwDevOps/pom.xml validate 
git commit * -m ".,"
git push

