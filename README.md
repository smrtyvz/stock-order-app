# Stock Order App Project
An application for brokage firms serving several APIs to operate stock orders for their customers
## Tools
* Spring Boot 3.3
* Spring Web for RESTful APIs
* H2 database
* Spring Data JPA for persistency
* Spring Security for basic auth
* Lombok
* Maven
* For Testing : MvcMock, Mockito and JUnit

## ER Diagram
![er](https://github.com/user-attachments/assets/34957a51-04b1-4a42-b023-633d542139da)

### Relationships
***One-To-Many***
Customer - Orders : A customer can have multiple orders but an order can belong to only one customer
Customer - Assets : A customer can have multiple assets but an asset can belong to only one customer

## Users & Authorization
There are two types of pre-defined user (user registry and login are not included, just added two users in h2) : "admin" and "agent". Please check the schema.sql file for passwords.

**admin**: Privileged to use all services.
**agent**: Prvileged to use all services apart from "matching order".

*Note: All endpoints in the project require authorization.*

## Endpoints

![endpoints](https://github.com/user-attachments/assets/a38e6bf7-aee1-48ad-9267-155de4606b64)

## Running application

### Running with embedded Tomcat
Please follow the steps:
* Clone the repository to your local
* Import project from Existing Sources with Maven
* In terminal, Run "mvn clean package" in the root of project.
* In terminal, Run "java -jar target/stock-order-app-0.0.1-SNAPSHOT.jar" 

### Running as a Docker image
Please follow the steps:
* Clone the repository to your local.
* In terminal, run command "docker build --tag=stock-order-app:latest ."
* In terminal, run command "docker run -p8080:8080 stock-order-app:latest"

In both way, you can access the API on http://localhost:8080/

