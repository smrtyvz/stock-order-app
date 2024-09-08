FROM openjdk:17-jdk-alpine
COPY target/stock-order-app-0.0.1-SNAPSHOT.jar stock-order-app-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/stock-order-app-0.0.1-SNAPSHOT.jar"]