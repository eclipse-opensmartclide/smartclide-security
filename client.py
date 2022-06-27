#*******************************************************************************
# Copyright (C) 2021-2022 CERTH
# 
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
#*******************************************************************************

import requests
import json
import git
from os import listdir
import os

output_dir = "/home/anasmarg/Desktop/C++"
r = requests.get('https://api.github.com/search/repositories?q=\"language:c++\"&order=desc&sort=stars&per_page=100')
list_url = []

for repo in r.json()['items']:
    list_url.append(repo['html_url'])

print(list_url)
del list_url[2]
del list_url[68]

for repo in list_url:
    git.Git(output_dir).clone(repo)
    print(repo)
    
print('Clone Completed')

# project_list = listdir(output_dir)
# output_dir = "/home/anasmarg/Desktop/C++/"
# projects_analyzed = []

# for project in project_list:
#     os.system('cppcheck --xml-version=2 ' + output_dir + project + '/ 2 > ' + output_dir + project + '/report.xml')
#     print('===================== CPPCHECK COMPLETED ==========================')

# for project in project_list:
#     os.system('docker run --rm --network=host -e SONAR_HOST_URL="http://localhost:9000" -e SONAR_LOGIN="b3563fa1b5f3a9b3b621c81d28aee2de12e8226f" -v "' + output_dir + project +':/usr/src/" sonarsource/sonar-scanner-cli -Dsonar.projectKey=' + project +' -Dsonar.cxx.file.suffixes=.cpp,.cxx,.cc,.c,.hxx,.hpp,.hh,.h -Dsonar.cxx.cppcheck.reportPaths =' + output_dir + project + '/report.xml')
#     print('=================== ' + project + ' ANALYSIS COMPLETED ============')
#     projects_analyzed.append(project)
    
# project = projects_analyzed[0]
