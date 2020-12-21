#Overview

System is dived in to two main modules "task runner api" and "task runner worker"

Please check c4 diagram to take an overview about the architecture (https://structurizr.com/share/61530) 

#Build the system

System build is based on maven build, so you can use regular maven commands to build the system

>> cd task-runner-api
>> mvn clean install
>> cd task-runner-worker
>> mvn clean install

#Startup local environment

The system is based on the following:
1) Mysql database: database is optional, and you can start the system without database, and it will use RAM database
2) Kafka: kafka is use for messaging, and it should start for the system to operate correctly
3) spring boot: the system is build on spring boot

Note:

you can use docker file on "local" path to start kafka and my sql

>> cd local
>> docker-compose up

After starting up Kafka and MySql (optional) you can run regular spring boot commands to start up the system

>> cd task-runner-api
>> mvn spring-boot:run
>> cd task-runner-worker
>> mvn spring-boot:run

#Api Documentation

Swagger is the main api documentation, To access Swagger:

http://localhost:8081/swagger-ui/index.html#/

#Future plans

- System is not set to handle security (authentication, authorization)
- Right now system is executing simple commands and this part need to be improved
- System should support upload files to remote location, so it can be used during execution
- System should support running commands in remote machines as well
- System Log and monitor is not implemented yet
