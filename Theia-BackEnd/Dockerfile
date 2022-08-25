FROM ubuntu:18.04


RUN apt-get update && apt-get install default-jdk -y

RUN apt-get update && apt-get install cppcheck -y

RUN apt install maven -y

RUN apt-get install curl -y

ENV PATH="/opt/node-v14.17.1-linux-x64/bin:${PATH}"
RUN curl https://nodejs.org/dist/v14.17.1/node-v14.17.1-linux-x64.tar.gz |tar xzf - -C /opt/

WORKDIR /opt/app
ENV HOME=/opt/app
#/ENV TOKEN=b3563fa1b5f3a9b3b621c81d28aee2de12e8226f

RUN apt install wget -y

RUN wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F6.30.0/pmd-bin-6.30.0.zip -P /opt/app
RUN apt-get install unzip -y

RUN chmod -R 777 /opt/app
RUN chmod -R 700 /opt/app/pmd-bin-6.30.0.zip

RUN unzip pmd-bin-6.30.0.zip -d /opt/app/

RUN chmod -R 700 /opt/app/pmd-bin-6.30.0/
RUN wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.7.0.2747-linux.zip -P /opt/app
RUN unzip sonar-scanner-cli-4.7.0.2747-linux.zip -d /opt/app


ADD CppRules /opt/resources/CppRules
ADD Rulesets /opt/resources/Rulesets
#/ADD sonar-scanner-cli-4.7.0.2747-linux /opt/app
#/ADD pmd-bin-6.30.0 /opt/app



#/bin/bash

ENV PATH="/opt/app/sonar-scanner-4.7.0.2747-linux/bin:${PATH}"
RUN pwd
COPY target/Theia-BackEnd-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080