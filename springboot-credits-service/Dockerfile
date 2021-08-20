FROM openjdk:18
VOLUME /tmp
ADD ./target/springboot-credits-service-0.0.1-SNAPSHOT.jar credits-service.jar
ENTRYPOINT ["java","-jar","/credits-service.jar"]