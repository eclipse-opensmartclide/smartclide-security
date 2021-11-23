import requests
import json
import git
from os import listdir
import os



os.system('cppcheck --xml-version=2 /home/anasmarg/Desktop/ADOP 2 > /home/anasmarg/Desktop/ADOP/report.xml')
print('===================== CPPCHECK COMPLETED ==========================')

os.system('docker run --rm --network=host -e SONAR_HOST_URL="http://localhost:9000" -e SONAR_LOGIN="b3563fa1b5f3a9b3b621c81d28aee2de12e8226f" -v "/home/anasmarg/Desktop/ADOP:/usr/src/" sonarsource/sonar-scanner-cli -Dsonar.projectKey=Testing_CPP -Dsonar.cxx.file.suffixes=.cpp,.cxx,.cc,.c,.hxx,.hpp,.hh,.h -Dsonar.cxx.cppcheck.reportPaths =/home/anasmarg/Desktop/ADOP/report.xml')
print('=================== ANALYSIS COMPLETED ============')
    