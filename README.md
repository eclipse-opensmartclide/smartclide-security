# smartclide-security
The Security Component of the SmartCLIDE project consists of two subcomponents, namely the Security-related Static Analysis (SSA) subcomponent and the Vulnerability Assessment (VA) subcomponent. The Security Component is in charge of evaluating the internal security of subservices and the overall Workflow based on any code-level security issues and software vulnerabilities discovered in the source code. The objective of this SSA subcomponent is to detect possible security flaws in the web services' source code, as well as to offer high-level security ratings. Based on any code-level security concerns found in the source code, the Security-related Static Analysis (SSA) subcomponent will be responsible of assessing the internal security of subservices and the overall Workflow. In fact, the goal of this subcomponent is to detect potential security flaws in the source code of web services and to provide high-level security ratings. As regards the VA subcomponent, it is responsible of identifying software components with high possibility to be vulnerable. Actually, it predicts through a Machine Learning model, the vulnerable files of a project. 
Our method is built around a framework for performing security-related static analysis on a particular software application. This framework allows the execution of numerous static code analyzers for a specific software product, as well as the reporting the potential vulnerabilities found in its source code. The integration of various tools is critical for increasing the likelihood of discovering additional security flaws. Different technologies may identify various sorts of security problems and support various programming languages. The source code for each programming language is evaluated, and thorough reports containing mistakes and potential security vulnerabilities in the code are provided, depending on the tool and security standards examined. Using such tools, the developer may quickly detect security flaws in the software implementation, and also he/her will have a comprehensive analysis of his/her code and the mistakes that occur, receiving an "indicator" of how safe his/her product is. 
## Component Deployment
In this section, a description of how the Security Component can be used as an individual Microservice is provided. More specifically, we describe the whole process on how to set up the platform in order to run the component locally as well as the dependencies required to be configured in order to get the proper results.	
### Prerequisites
In order to analyze a project, an instance of the SonarQube platform needs to be running alongside with the SSA and VA services. The easiest way to do this is by running a Docker Container of SonarQube instance locally in your machine. There is an extensive guidance for running the Docker Container of the SonarQube in this [link](https://docs.sonarqube.org/latest/setup/install-server/). For your convenience, some indicative commands are provided. First of all, you need to pull the image of the SonarQube:
~~~
$>docker pull sonarqube
~~~

Subsequently, you need to create 3 volumes in order to not lose your data in case of an update:
~~~
$> docker volume create --name sonarqube_data
$> docker volume create --name sonarqube_logs
$> docker volume create --name sonarqube_extensions
~~~

Finally, you need to run your container with the command bellow:
~~~
$> docker run -d --name sonarqube \
    -p 9000:9000 \
    -v sonarqube_data:/opt/sonarqube/data \
    -v sonarqube_extensions:/opt/sonarqube/extensions \
    -v sonarqube_logs:/opt/sonarqube/logs \
    <image_name>
~~~

You can test that the instance is running properly by accessing your localhost:9000. You will be able to see the login screen of SonarQube:


![sonarqube_login](https://user-images.githubusercontent.com/39555236/152549766-8862b5c3-8e6f-497f-8212-8754cb48a955.PNG)

After your successful login, you will have to create an access token in order to properly execute analyze commands through the service. The access token can be generated through your profile -> Security tab as bellow:

![security_tab](https://user-images.githubusercontent.com/39555236/152550008-a1d7b0c2-d791-4bd3-b15e-022b586c9aca.PNG)

This token needs to be declared as an environmental variable in the SSAS container, for the service to be able to access the SonarQube Platform. From this point on, if all the above steps are successful, SonarQube will be running on your local machine. An example token is shown below:

~~~
b3563fa1b5f3a9b3b621c81d28aee2de12e8226f
~~~

### Execution of the Docker Container of SSAS
With the SSAS Docker Container already deployed on your local machine, it can be started, paused, and stopped using common Docker commands. The first step is to build the image through a Dockerfile:
~~~
FROM ubuntu

RUN apt-get update && apt-get install default-jdk -y
CMD ["export JAVA_HOME=`which java`"]

RUN apt-get update && apt-get install cppcheck -y
CMD ["cppcheck --version"]
	
WORKDIR /opt/app
ENV HOME=/opt/app
ENV TOKEN=b3563fa1b5f3a9b3b621c81d28aee2de12e8226f
RUN apt install wget -y && wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F6.40.0/pmd-bin-6.40.0.zip && apt-get install unzip -y && unzip pmd-bin-6.40.0.zip -d $HOME

COPY target/Theia-BackEnd-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
~~~

As can be seen, the token produced is declared in the Dockerfile as an environmental parameter. In the file above, you can see that we are adding two CLI tools, the PMD and the CPPCHECK. Both tools are required in order to properly execute the analysis of the projects given. As a next step, you need to build the image with the proper docker command. The docker command must be executed in the same folder as the Dockerfile:

~~~
docker build -t ssas .
~~~

After the execution of the above command, you will be able to see the ssas image in your docker images repo. In order to run the container, you need to type:
~~~
docker run -p 8080:8080 ssas
~~~

At this point, you will be able to analyze your projects at the port 8080 on your localhost.

### Invocation of the individual services (APIs)

The Security Platform allows the user to evaluate the internal security level of software applications written in Java and Python programming languages. This is achieved through a dedicated API exposed by the RESTful web server, which is, in fact, a simple HTTP POST request. Several inputs need to be provided as parameters to this request. After starting the Docker Container of the Security Platform backend, its web services are up and running. The platform is accessible through the following end point:

~~~
localhost:8080/smartclide/analyze
~~~

The parameters of the HTTP request are presented in the table below.

|   Parameter  |                           Description                           | Required |                                                                                                                                                                Valid Inputs                                                                                                                                                               |
|:------------:|:---------------------------------------------------------------:|:--------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| url      | The URL of the software project that should be analyzed.        |    Yes   | Any valid URL from a Git repository. |
| language | The programming language of the project to be analyzed.     |    Yes    | “Maven” for Java Maven projects, “Python” for python projects.                     |
| properties | A JSON containing the metrics alongside with the security characteristics to be measured. Each metric is accompanied by it’s thresholds and each characteristic by it’s weights. Weights and Threshold are required for our security model to run.       |    Yes    | Explained below.                                                                                                                                                                                                                                        |
| sonarqube | A similar JSON as properties, containing metrics exclusively for SonarQube Platform.     |    Yes    | Explained below.                     |

An example of “properties” parameter for the Java model.
~~~
{
   "CK":{
  	"lcom":[
     	0,
     	0.10910936800871021,
     	3.1849529780564265
  	],
  	"cbo":[
     	0.017050298380221655,
     	0.03692993475020107,
     	0.5714285714285714
  	],
  	"wmc":[
     	0.13793103448275862,
     	0.04986595433654195,
     	0.2765273311897106
  	]
   },
   "PMD":{
  	"ExceptionHandling":[
     	0,
     	0.22938518010164352,
     	12.987012987012987
  	],
  	"Assignment":[
     	0,
     	0.11160028050045478,
     	7.6923076923076925
  	],
  	"Logging":[
     	0,
     	0.05692917472098835,
     	6.8493150684931505
  	],
  	"NullPointer":[
     	0,
     	0.32358608981534065,
     	25.966183574879228
  	],
  	"ResourceHandling":[
     	0,
     	2.201831659093579,
     	166.66666666666666
  	],
  	"MisusedFunctionality":[
     	0,
     	0.13732179935769162,
     	4.784688995215311
  	]
   },
   "Characteristics":{
  	"Confidentiality":[
     	0.005,
     	0.005,
     	0.005,
     	0.1,
     	0.1,
     	0.1,
     	0.01,
     	0.01,
     	0.01,
     	0.1,
     	0.1,
     	0.005,
     	0.2,
     	0.15,
     	0.1
  	],
  	"Integrity":[
     	0.01,
     	0.005,
     	0.005,
     	0.1,
     	0.15,
     	0.01,
     	0.01,
     	0.01,
     	0.01,
     	0.15,
     	0.15,
     	0.01,
     	0.16,
     	0.21,
     	0.01
  	],
  	"Availability":[
     	0.005,
     	0.005,
     	0.01,
     	0.1,
     	0.01,
     	0.01,
     	0.2,
     	0.3,
     	0.01,
     	0.01,
     	0.01,
     	0.3,
     	0.01,
     	0.01,
     	0.01
  	]
   }
}
~~~

An example of “sonarqube” parameter for the Java model.
~~~
{
   "metricKeys":{
  	"vulnerabilities":[
     	0,
     	0.09848484848484848,
     	4
  	]
   },
   "vulnerabilities":{
  	"sql-injection":[
     	0,
     	0.013234192551328933,
     	1.5479876160990713
  	],
  	"dos":[
     	0,
     	0.024419175132769335,
     	2.2172949002217295
  	],
  	"weak-cryptography":[
     	0,
     	0.0015070136414874827,
     	0.1989258006763477
  	],
  	"auth":[
     	0,
     	0.024207864640426638,
     	3.0959752321981426
  	],
  	"insecure-conf":[
     	0,
     	0.7356100591012389,
     	32.05128205128205
  	]
   }
}
~~~

An example of “properties” parameter for the Python model.

~~~
{
   "Characteristics":{
  	"Confidentiality":[
     	0.05,
     	0.2,
     	0.1,
     	0.05,
     	0.3,
     	0.2,
     	0.1
  	],
  	"Integrity":[
     	0.05,
     	0.2,
     	0.2,
     	0.05,
     	0.1,
     	0.3,
     	0.1
  	],
  	"Availability":[
     	0.05,
     	0.15,
     	0.05,
     	0.4,
     	0.05,
     	0.1,
     	0.2
  	]
   }
}
~~~

An example of “sonarqube” parameter for the Python model.
~~~
{
   "metricKeys":{
  	"complexity":[
     	0.022727272727272728,
     	0.027435805175993106,
     	0.3082355281480008
  	],
  	"vulnerabilities":[
     	0,
     	0.09848484848484848,
     	4
  	]
   },
   "vulnerabilities":{
  	"sql-injection":[
     	0,
     	0.013234192551328933,
     	1.5479876160990713
  	],
  	"dos":[
     	0,
     	0.024419175132769335,
     	2.2172949002217295
  	],
  	"weak-cryptography":[
     	0,
     	0.0015070136414874827,
     	0.1989258006763477
  	],
  	"auth":[
     	0,
     	0.024207864640426638,
     	3.0959752321981426
  	],
  	"insecure-conf":[
     	0,
     	0.7356100591012389,
     	32.05128205128205
  	]
   }
}
~~~

### Use Case Scenario

As an example, we analyzed a new GitHub project by sending a POST request to the following address,

~~~
localhost:8080/smartclide/analyze
~~~

providing also the required parameters:

|   Parameter  |                           Value                           |                                                                                                                                                                                                                                                                                                                                
|:------------:|:---------------------------------------------------------------:|
| url      | https://github.com/apache/iotdb        
|language | Maven                     |
| inspection | {"CK":{"lcom":[0,0.10910936800871021,3.1849529780564265]},"PMD":{"ExceptionHandling":[0,0.187293,13.191],"Assignment":[0,0.334,6.832],"Logging":[0,0.031,7.592],"NullPointer":[0,0.415,31.87],"ResourceHandling":[0,7.8201,193.8],"MisusedFunctionality":[0,0.234,3.951]},"Characteristics":{"Confidentiality":[0,0.05,0.01,0.1,0.01,0.01,0.01,0,0.2,0.01,0.3,0.2,0.1],"Integrity":[0,0.15,0.2,0.015,0.015,0.03,0.005,0,0.1,0.07,0.1,0.3,0.015],"Availability":[0,0.15,0.02,0.01,0.1,0.15,0.02,0,0.01,0.5,0.01,0.01,0.02]}}
|sonarqube|{"metricKeys":{"vulnerabilities":[0,0.09848484848484848,4]},"vulnerabilities":{"sql-injection":[0,0.02,1.894],"dos":[0,0.032,2.576],"weak-cryptography":[0,0.0003,0.2464],"auth":[0,0.013,4.134],"insecure-conf":[0,0.76,36.667]}}


The output is a JSON report that can be found below:
~~~
{
    "Metrics": {
        "Assignment": 0.28533877494552623,
        "Logging": 0.09511292498184208,
        "NullPointer": 1.1975581918168297,
        "MisusedFunctionality": 0.5663542351191505,
        "ResourceHandling": 1.3531975236052987,
        "ExceptionHandling": 0.6614671601009927
        "insecure-conf": 0.394348048254871,
        "auth": 0.0888671658039146,
        "weak-cryptography": 0.07775877007842527,
        "vulnerabilities": 0.0,
        "dos": 0.011108395725489325,
        "sql-injection": 0.011108395725489325
    },
    "Properies": {
        "Logging": 0.4957602879922073,
        "NullPointer": 0.48756067092963234,
        "MisusedFunctionality": 0.45529267754652264,
        "auth": 0.4907950538942108,
        "lcom": 0.3557782383819141,
        "ExceptionHandling": 0.4817677313053504,
        "dos": 0.8264313167892293,
        "sql_injection": 0.722290106862767,
        "weak_cryptography": 0.34262744803245576,
        "Assignment": 0.5728461452911284,
        "insecure_conf": 0.7405604945691637,
        "ResourceHandling": 0.9134795256067506,
        "vulnerabilities": 1.0
    },
    "Characteristics ": {
        "Availability": 0.5379092619267439,
        "Confidentiality": 0.6737682431276345,
        "Integrity": 0.632938306168564
    },
    "Security_index": {
        "Security_Index": 0.6148719370743141
    }
}

~~~

Finally, the results of the Vulnerability Assessment are outputted in a JSON report as well:
~~~
{
    "message": "The request was fulfilled. ",
    "results": [
        {
            "file_path": "testRepo/apache_iotdb\\.mvn\\wrapper\\MavenWrapperDownloader.java",
            "vulnerability_flag": 0,
            "vulnerability_score": 0.4711637506
        },
        {
            "file_path": "testRepo/apache_iotdb\\cli\\src\\main\\java\\org\\apache\\iotdb\\cli\\AbstractCli.java",
            "vulnerability_flag": 0,
            "vulnerability_score": 0.45865204
        },
        {
            "file_path": "testRepo/apache_iotdb\\cli\\src\\main\\java\\org\\apache\\iotdb\\cli\\Cli.java",
            "vulnerability_flag": 0,
            "vulnerability_score": 0.2290591577
        },
…..
…..
…..
      {
            "file_path":"testRepo/apache_iotdb\\zeppelin-interpreter\\src\\main\\java\\org\\apache\\zeppelin\\iotdb\\IoTDBInterpreter.java",
            "vulnerability_flag": 1,
            "vulnerability_score": 0.9139516602
        }
  ],
    "status": 200
}

~~~

This JSON contains a list of all the source code files of the analyzed project along with a vulnerability flag (i.e. ‘0’ for not vulnerable files, ‘1’ for vulnerable file) and a vulnerability score indicative of how likely is a file is to be vulnerable.


