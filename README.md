# MVP Match Vending Machine Backend API Application

Welcome to the Vending Machine Backend API application. The tech stack of the application is Java 17 with Spring Boot
3.0.2 and Postgres 15.2 as relational database.

## Pre-requisites

For running the application in the local environment, there should be the following tools installed:

- JDK17+
- Docker

## Getting started

To run the application in the local environment, there should be dependencies listed in the `docker-compose.yml`
started. To start the `docker-compose.yml` file, run the following command:

```shell
docker-compose -f src/docker/docker-compose.yml up -d
```

After the docker containers are spin up, the following command should be executed in order to start the application:

```shell
./mvnw spring-boot:run -Dspring.datasource.username=<DATABASE_USERNAME> -Dspring.datasource.password=<DATABASE_PASSWORD>
```

where DATABASE_USERNAME and DATABASE_PASSWORD are properties which should be replaced with values from
the `docker-compose.yml` file.

## Postman API collection

To try out the backend API there is a Postman collection, located in
the `docs/mvpmatch-vending-machine-api.postman_collection`. Import it into the Postman, and try out different endpoints.
