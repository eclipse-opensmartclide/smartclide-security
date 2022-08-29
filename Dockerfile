FROM sonarqube:latest

RUN wget https://github.com/SonarOpenCommunity/sonar-cxx/releases/download/latest-snapshot/sonar-cxx-plugin-2.1.0.253.jar -P /opt/sonarqube/extensions/plugins/