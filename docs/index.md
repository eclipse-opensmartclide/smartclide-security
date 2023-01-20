


<!--  
   Copyright (C) 2021-2022 CERTH  
     
   This program and the accompanying materials are made  
   available under the terms of the Eclipse Public License 2.0  
   which is available at https://www.eclipse.org/legal/epl-2.0/  
     
   SPDX-License-Identifier: EPL-2.0  
-->  


#### Installation and Invocation of the Security-related Static Analysis Subcomponent (SSAS)


Installation:

To install the SSAS and the Sonarqube platfrom, which the security component is base on, we will use Docker and specifically docker compose method to install the two individual containers using only one file.

First we need to to clone the project from the repository

`$` `git clone  https://iti-gitlab.iti.gr/smartclide/security.git`




Then we have build the maven project to produce the necessary .jar file. Open a terminal inside the project folder and run



`$` `mvn clean install`




Then run

`$` `docker compose up`

The docker command will build and run the 2 containers using their Dockerfiles.





Inside the project there is a specific structure of files needed for the installation, apart from the main project files:

### Files and Folders included for the installation





| Name    | Description |
| ------------- |:-------------|
|  docker-compose.yml        |  Docker compose file           |
| Dockerfile    |  ssas platform Dockerfile   |
| CppRules    |   Folder that contains .xml files with all the rules of the C++ analysis  |
| Rulesets    |   Folder that contains .xml files with the rules that are going to analyze  |
| sonarqube    |   Folder that contains the Dockerfile that is needed to built  SSAS platform container  |
|




docker-compose.yml is the one that we are going to run and creates sonarqube and ssas containers .

Inside we declare the containers and their parameters,the networks and the volumes needed.

The parameters that need to be defined are presented in the table below




**Container parameters**


|Parameter Name    | Description |
| ------------- |:-------------|
| container_name        |  the name  we give to the container         |
| build    | the directory where the Dockerfile needed for the build is located.   |
| image    |  the name we give to the docker image.   |
| ports    |  mapping of the ports from the container to the host   |
| networks    |   which docker network the container will be connected to  |
| volumes    |  volumes that are going to be used for the container  | 




Then in the “networks” section we declare and create the network that the 2 containers will use to communicate with each other.

And lastly we declare and create the docker volumes that will be used.

You can see the docker-compose.yml file below:




**docker-compose.yml**




``` 

version: "3.2"  
services:  
 sonarqube:  
    container_name: sonarqube  
    build: sonarqube  
    image: smartclide2022/sonar  
    ports:  
      - "9000:9000"  
  networks:  
      - custom-bridge2  
    volumes:  
      - sonarqube_logs:/opt/sonarqube/logs  
      - sonarqube_data:/opt/sonarqube/data  
      - sonarqube_extensions:/opt/sonarqube/extensions  
   
 ssas:  
    container_name: ssas  
    build: .  
    image: smartclide2022/ssas  
    ports:  
      - "8080:8080"  
  networks:  
      - custom-bridge2  
    
networks:  
 custom-bridge2:  
    external: false         
  
volumes:  
  sonarqube_logs:  
  sonarqube_data:  
  sonarqube_extensions:
```   

Below it is attached the Dockerfile that installs the SSAS platform container.

First we install the OS needed for container and the necessary tools that we will use like maven and cpp check and also Node Js that the container downloads and declares its environmental path.

Then we declare the name of token that the container will use to access Sonarqube, and store it as an environmental variable.

After that we download the PMD tool needed for the analysis of Maven projects and the sonar scanner that will be used for Python and Javascript projects.

We also copy the neccesary folders “CppRules” and “Rulesets” which contain .xml files for the rules that we will be used for the analysis of the C++ projects.

Lastly we copy the main .jar file that contains the Java Spring Boot application that we built for the API of the SSAS platform and we expose the port 8080 to reach the application.

### Dockerfile.yml that installs SSAS platform container


```FROM ubuntu:18.04    
 RUN apt-get update && apt-get install default-jdk -y    
 RUN apt-get update && apt-get install cppcheck -y    
 RUN apt install maven -y    
 RUN apt-get install curl -y    
 ENV PATH="/opt/node-v14.17.1-linux-x64/bin:${PATH}" RUN curl https://nodejs.org/dist/v14.17.1/node-v14.17.1-linux-x64.tar.gz |tar xzf - -C /opt/    
 WORKDIR /opt/app ENV HOME=/opt/app    
 RUN apt install wget -y    
 RUN wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F6.30.0/pmd-bin-6.30.0.zip -P /opt/app RUN apt-get install unzip -y    
 RUN chmod -R 777 /opt/app RUN chmod -R 700 /opt/app/pmd-bin-6.30.0.zip    
 RUN unzip pmd-bin-6.30.0.zip -d /opt/app/    
 RUN chmod -R 700 /opt/app/pmd-bin-6.30.0/ RUN wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.7.0.2747-linux.zip -P /opt/app RUN unzip sonar-scanner-cli-4.7.0.2747-linux.zip -d /opt/app    
    
 ADD CppRules /opt/resources/CppRules ADD Rulesets /opt/resources/Rulesets #/ADD sonar-scanner-cli-4.7.0.2747-linux /opt/app #/ADD pmd-bin-6.30.0 /opt/app    
    
    
 #/bin/bash    
 ENV PATH="/opt/app/sonar-scanner-4.7.0.2747-linux/bin:${PATH}" RUN pwd COPY target/Theia-BackEnd-0.0.1-SNAPSHOT.jar app.jar ENTRYPOINT ["java", "-jar", "app.jar"]    
 EXPOSE 8080    
    
```   



Below we attach also the Dockerfile that installs Sonarqube platform container.

First it installs Sonarqube using the image for the repository and then copies the CXX plugin needed to run C++analysis. You may need to update the version of the CXX plugin if there is a new release and copy it’s url in the Dockerfile




**Dockerfile that installs Sonarqube platform container.**
``` FROM sonarqube:9.6.1-community    
 COPY CXXplugin/sonar-cxx-plugin-2.1.0.311.jar  /opt/sonarqube/extensions/plugins/ 
 ```
### Usage of the SSAS Platform through REST API

In this section, we present how the SSAS platform can be utilized for analazying the security level of a given software application. In particular, we present the REST API of the SSAS platform, by giving information about the endpoint, the type of request, and the mandatory and optional parameters that have to be provided. Indicative examples are also provided to help the reader understand how the SSAS service can be invoked, what information it returns after a successful analysis, and how the results of the analysis can be interpreted.

After setting up the SSAS microservice (along with the required SonarQube instance) by following the instructions provided in Section 4.6.1, the SSAS service is accessible through the following endpoint:

```<local_IP>:<defined_port>/smartclide/analyze```

In the above endpoint, the <local_ip> placeholder should be replaced with the IP of the machine on which the SSAS microservice (i.e., the Docker Container) has been deployed, while the <defined_port> placeholder should be replaced with the port that was assigned to the SSAS Docker Container during the installation. This port is by default the port 8080; however, the users can use any port they wish, by properly defining it in the docker run command that builds the SSAS container

It should be noted that SSAS has a single endpoint for performing the analysis of any software project that is written in one of its supported languages, namely Java, Python, JavaScript, C, and C++. In order to perform the analysis, the user needs to submit an HTTP POST request by providing a set of parameters in its body. These parameters are described in Table  below

**The parameters of the HTTP Request that should be submitted for analysing a specific software project using SSAS**

|Parameter Name    | Description |
| ------------- |:-------------:|
|  url           |  The url of the project’s repository to be analysed. This url must be a url from an online repository like GitHub, GitLab, and Bitbucket.      |
| language     |  Indicating the implementation language of the project. The possible values are Java, Python, JavaScript, and Cpp.    |



It gives a brief description of the parameters that are necessary in order to analyse a software project with SSAS. However, since some of them are quite complicated to understand, we provide a set of detailed examples, in order to further facilitate the understanding of the service. In particular, we showcase how the service could be used for analysing open-source software applications, and we provide the exact parameters that need to be applied. This will allow the reader to execute those requests in order to understand how the analysis work, as well as to prepare custom requests for analysing their own projects, by properly modifying the parameters of these examples. The user also needs to provide JSON description of the model to the Body of the request, a description of which is provided in the box below:

A JSON containing the security categories, which are, in fact the properties of the security model that is used by the Security Measures Computation module (see D2.2), which are derived from external static analysis tools (namely PMD and CppCheck). For each one of these security categories/properties, similarly to the properties parameter/JSON described above, their thresholds that are used for the calculations of the Measures Computation module need to be provided. In particular, for each one of the security categories, an array with three values, which correspond to the lower, mid, and upper threshold of the corresponding category should be provided. This JSON also contains the Characteristics of the security model along with their weights that are required for the computation of the high-level measures, and the overall Security Index of the project.

Also containing the names of the security categories that are supported by SonarQube. These security categories are the properties of the security model that are quantified by SonarQube. For each one of these security categories/properties, similarly to the properties parameter/JSON described above, their thresholds that are used for the calculations of the Measures Computation module need to be provided. In particular, for each one of the security categories, an array with three values, which correspond to the lower, mid, and upper threshold of the corresponding category should be provided.



**JSON body raw**

```{"CK":{"lcom":[0,0.10910936800871021,3.1849529780564267],"cbo":[0.017050298380221656,0.03692993475020107,0.5714285714285714],"wmc":[0.13793103448275863,0.04986595433654195,0.2765273311897106]},"PMD":{"ExceptionHandling":[0,0.22938518010164353,12.987012987012987],"Assignment":[0,0.11160028050045479,7.6923076923076929],"Logging":[0,0.05692917472098835,6.8493150684931509],"NullPointer":[0,0.32358608981534067,25.966183574879229],"ResourceHandling":[0,2.201831659093579,166.66666666666667],"MisusedFunctionality":[0,0.13732179935769163,4.784688995215311]},"Characteristics":{"Confidentiality":[0.005,0.005,0.005,0.1,0.1,0.1,0.01,0.01,0.01,0.1,0.1,0.005,0.2,0.15,0.1],"Integrity":[0.01,0.005,0.005,0.1,0.15,0.01,0.01,0.01,0.01,0.15,0.15,0.01,0.16,0.21,0.01],"Availability":[0.005,0.005,0.01,0.1,0.01,0.01,0.2,0.3,0.01,0.01,0.01,0.3,0.01,0.01,0.01]},"metricKeys":{"vulnerabilities":[0,0.09848484848484848,4]},"Sonarqube":{"sql-injection":[0,0.013234192551328933,1.5479876160990714],"dos":[0,0.024419175132769336,2.2172949002217297],"weak-cryptography":[0,0.0015070136414874827,0.1989258006763477],"auth":[0,0.024207864640426639,3.0959752321981428],"insecure-conf":[0,0.7356100591012389,32.05128205128205]}}```

As can be seen by Table the JSON body is properly defined in order to encapsulate all the details of the security model that we proposed for Java applications .    
In particular, the JSON body contains all the Security Categories that were selected from the external static code analyser, namely PMD. Those properties are Exception Handling, Assignment, Logging, Null Pointer, Resource Handling, and Misused Functionality. For each one of these properties, there is an array with three numbers. Those three numbers correspond to the lower, mid, and upper threshold of each property    
For instance, the values of Assignment are 0, 0.11, 7.69, which are the same values reported . It should be noted, that the users can also compute some software metrics through the external CK tool. In this example, we state that the metrics lcom, cbo, and wmc, should be also computed. However, those values are not taken into account for the computation of the higher-level metrics as well as the overall Security Index of the project. Finally, in the same parameter, we also define the Characteristics of the model (which are the high-level measures that need to be computed). As can be seen, we have defined three Characteristics, namely Confidentiality, Availability, and Integrity. For each Characteristic there is an array containing the weights that need to be used for computing their overall score from the scores of the identified Properties. The selected Characteristics and weights of the given example are those of the Java model

The Sonarqube parameter contains all those Security Categories, which are retrieved from the SonarQube Platform. As can be seen, we have selected those properties that have been defined for the Java model in . Inside the arrays, we have also added their thresholds.

### Scan Java project from zip file

There is the option to scan a Java project that contains already its binary files.  
This option is available using the following endpoint

```<local_IP>:<defined_port>/smartclide/analyze_local```

In this case we need to pass our parameters as form-data inside the body.

|          Key    |            Value  |Content-type    |    
|-----------------|---|---|   
|         zip                |       Select the zip file that contains the Java project | multipart-form-data      |    
|         sonarProperties           |             Json containing the properties for the analysis(same as above we used in JSON body raw    |application-json




After the analysis is complete, the service returns a JSON file with the results of the analysis. The response that is produced by SSAS is attached Below


### Response
```
{
    "CK": {
        "loc": 190159.0,
        "cbo": 0.08822616862730662,
        "lcom": 0.8416430460824889,
        "wmc": 0.1914345363616763
    },
    "PMD": {
        "Assignment": 0.6731209146030427,
        "Logging": 0.07888135718004408,
        "NullPointer": 3.0553379014403736,
        "MisusedFunctionality": 0.9202825004338475,
        "ResourceHandling": 0.40492430019089287,
        "ExceptionHandling": 1.4934870292755011
    },
    "Sonarqube": {
        "insecure-conf": 0.1191886234458921,
        "auth": 0.0,
        "weak-cryptography": 0.007449288965368256,
        "dos": 0.09684075654978733,
        "sql-injection": 0.0
    },
    "metrics": {
        "ncloc": 134241.0,
        "vulnerabilities": 4.0
    },
    "Property_Scores": {
        "Logging": 0.49838405953354453,
        "NullPointer": 0.44673410497481375,
        "MisusedFunctionality": 0.41576298277290863,
        "auth": 1.0,
        "lcom": 0.38092150139219616,
        "ExceptionHandling": 0.4504570180167413,
        "dos": 0.4834870757634748,
        "sql_injection": 1.0,
        "wmc": 0.1877090751177195,
        "weak_cryptography": 0.4849500764007037,
        "Assignment": 0.46296383677676317,
        "cbo": 0.4520146260840956,
        "insecure_conf": 0.9189865459483278,
        "ResourceHandling": 0.9080483063910556
    },
    "Characteristic_Scores": {
        "Availability": 0.6791253872286296,
        "Confidentiality": 0.6237102387826031,
        "Integrity": 0.672086307863198
    },
    "Security_index": {
        "Security_Index": 0.6583073112914769
    },
    "Hotspots": {
        "insecure-conf": [
            {
                "component": "struts:core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java",
                "project": "struts",
                "securityCategory": "insecure-conf",
                "vulnerabilityProbability": "LOW",
                "line": 398,
                "message": "Make sure creating this cookie without the \"secure\" flag is safe here.",
                "textRange": {
                    "startLine": 398,
                    "endLine": 398,
                    "startOffset": 32,
                    "endOffset": 38
                }
            },
            {
                "component": "struts:core/src/main/java/org/apache/struts2/result/plain/HttpCookies.java",
                "project": "struts",
                "securityCategory": "insecure-conf",
                "vulnerabilityProbability": "LOW",
                "line": 31,
                "message": "Make sure creating this cookie without the \"secure\" flag is safe here.",
                "textRange": {
                    "startLine": 31,
                    "endLine": 31,
                    "startOffset": 24,
                    "endOffset": 30
                }
            },
...
..
	  "PMD_issues": {
        "Assignment": [
            {
                "Problem": "1",
                "Package": "org.apache.struts2.showcase.source",
                "File": "/home/upload/struts/apps/showcase/src/main/java/org/apache/struts2/showcase/source/ViewSourceAction.java",
                "Priority": "3",
                "Line": "212",
                "Description": "Avoid assignments in operands",
                "Rule set": "Error Prone",
                "Rule": "AssignmentInOperand"
            },
            {
                "Problem": "1",
                "Package": "org.apache.struts2.showcase.source",
                "File": "/home/upload/struts/apps/showcase/target/classes/org/apache/struts2/showcase/source/ViewSourceAction.java",
                "Priority": "3",
                "Line": "212",
                "Description": "Avoid assignments in operands",
                "Rule set": "Error Prone",
                "Rule": "AssignmentInOperand"
            },
            {
                "Problem": "1",
                "Package": "org.apache.struts2.showcase.source",
                "File": "/home/upload/struts/apps/showcase/target/struts2-showcase/WEB-INF/classes/org/apache/struts2/showcase/source/ViewSourceAction.java",
                "Priority": "3",
                "Line": "212",
                "Description": "Avoid assignments in operands",
                "Rule set": "Error Prone",
                "Rule": "AssignmentInOperand"
            },
...
...

```   
As shown in the response, the first part of the JSON consists of the normalized values of the properties selected from each tool. Those values are the values that were used for computing the Property Scores of the defined properties, upon which the calculation of the higher-level measures, and, in turn, the security index is based. Afterwards, the “Property Scores” includes the property scores as calculated by the utility function and the thresholds per each property. Following that, “Characteristics Scores” included as calculated by the multiplication of the weight and the property score per each characteristic.The overall “Security Index” of the project analysed is included, containing the security score as it is calculated by the characteristic scores.    
Finally the detailed issues from the PMD analysis are shown in the case of Maven project, Hotspots(for Maven, Javascript and Python projects, and CPPcheck issues for CPP projects.



### Usage of the VA Model through REST API

In this section, we present how the VA model can be utilized for predicting the vulnerable software components of a given software application. In particular, we present the REST API of the VA model, by giving information about the endpoint, the type of request, and the mandatory and optional parameters that have to be provided. An indicative example is also provided to help the reader understand how the VA API can be invoked, what information is returned after a successful analysis, and how the results of the analysis can be interpreted.    
The VA service is accessible through the following endpoint:

```<local_IP>:<defined_port>/smartclide/VulnerabilityAssessment  ```

In the above endpoint, the <local_ip> placeholder should be replaced with the IP of the machine on which the VA microservice has been deployed (the same with SSAS), while the <defined_port> placeholder should be replaced with the port that was assigned to the Security Component during the installation. This port is by default the port 8080; however, the users can use any port they wish, by properly defining it in the docker run command that builds the container.    
It should be noted that the VA has a single endpoint for performing the analysis of any software project that is written in one of its supported languages, namely Java, Python, JavaScript, C, and C++. In order to perform the analysis, the user needs to submit an HTTP GET request by providing a set of parameters. These parameters are described in Table below:


| Parameter Name   | Description |
| -------------  |:-------------|
|  project         |   The url of the project’s repository to be analysed. This url must be a url from an online repository like GitHub, GitLab, and Bitbucket.                                                                           |
| lang    |     Indicating the implementation language of the project. The possible values are “java”, “python”, “javascript”, and “cpp”.                                             |
|user_name (optional)    |  The name of the user of the project’s Git repository.     |




The above table gives a brief description of the parameters that are necessary in order to analyse a software project with VA. In order to further facilitate the understanding of the service, we provide a detailed example. In particular, we showcase how the service could be used for analysing open-source software applications, and we provide the exact parameters that need to be applied. This will allow the reader to execute those requests in order to understand how the analysis work, as well as to prepare custom requests for analysing their own projects, by properly modifying the parameters of this example.


|Parameter Name    | Description |
| ------------- |:-------------|
|  project         |   https://github.com/apache/cordova-ios.git
| lang    |     javascript                                            |

After the analysis is complete, the service returns a JSON file with the results of the analysis. A fragment of the response that is produced by VA is attached below:

```
    "commit": "f804a42e171b2ec9288421e0ce63eb35bb0afe14",
    "date": "19/10/2022 17:11:36",
    "project_name": "cordova-ios_1666199484118",
    "results": [
        {
            "class_name": "create.spec.js",
            "confidence": "87 %",
            "is_vulnerable": 1,
            "package": "/apache_cordova-ios_1666199484118/tests/spec",
            "path": "VulnerabilityTestRepo/apache_cordova-ios_1666199484118/tests/spec/create.spec.js",
            "sigmoid": 0.8755856752
        },
        {
            "class_name": "BridgingHeader.spec.js",
            "confidence": "10 %",
            "is_vulnerable": 0,
            "package": "/apache_cordova-ios_1666199484118/tests/spec/unit",
            "path": "VulnerabilityTestRepo/apache_cordova-ios_1666199484118/tests/spec/unit/BridgingHeader.spec.js",
            "sigmoid": 0.1044444144
        },
        {
            "class_name": "PodsJson.spec.js",
            "confidence": "41 %",
            "is_vulnerable": 0,
            "package": "/apache_cordova-ios_1666199484118/tests/spec/unit",
            "path": "VulnerabilityTestRepo/apache_cordova-ios_1666199484118/tests/spec/unit/PodsJson.spec.js",
            "sigmoid": 0.4148975015
        },
        
        ...
        ...
		{ 
		 "class_name": "versions.spec.js",
		 "confidence": "16 %", 
		 "is_vulnerable": 0,
		 "package": "/apache_cordova-ios_1666199484118/tests/spec/unit", 
		 "path": 
		 "VulnerabilityTestRepo/apache_cordova-ios_1666199484118/tests/spec/unit/versions.spec.js",
		 "sigmoid": 0.161198765 }] 
		}
 ```

As can be seen from the above fragment, the JSON report comprises an array named “results”, which contains several JSON Objects. Each one of these JSON Objects contains relevant information for each source code file of the analyzed application. More specifically, it contains the following entries:

• class_name:  The name of the source code file to which this JSON Object refers.

• path: The exact path of the source code file to which this JSON Object refers.

• package: The package of the source code file to which this JSON Object refers.

• is_vulnerable: The vulnerability status of the corresponding source code file. It takes two possible values, i.e., 0 if the model decides that the source code file is potentially clean, and 1 if the model decides that the source code file is potentially vulnerable.

• sigmoid: The actual output of the neural network. It corresponds to the probability of the source code file to be vulnerable. This value is actually used by the model in order to define the vulnerability status of the corresponding source code file (i.e., the value of the “is_vulnerable” entry of the corresponding JSON Object).