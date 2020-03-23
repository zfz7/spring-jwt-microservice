FROM openjdk:latest
ADD build/libs/auth-0.0.1-SNAPSHOT.jar auth-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "auth-0.0.1-SNAPSHOT.jar"]