FROM ubuntu

RUN apt-get update && apt-get install default-jdk -y

RUN apt-get update && apt-get install cppcheck -y

WORKDIR /opt/app
ENV HOME=/opt/app
ENV TOKEN=b3563fa1b5f3a9b3b621c81d28aee2de12e8226f
RUN apt install wget -y && wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F6.30.0/pmd-bin-6.30.0.zip && apt-get install unzip -y && unzip pmd-bin-6.30.0.zip -d $HOME

COPY target/Theia-BackEnd-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
