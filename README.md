# BE

This project was generated using [Spring Boot](https://spring.io/projects/spring-boot) version 4.0.4 with Java 25.

## Tech Stack

- Java 25
- Spring Boot 4.0.4
- PostgreSQL
- Redis
- Lombok

## Prerequisites

- Java 25
- Maven

## Development server

To start a local development server, run:

```bash
./mvnw spring-boot:run
```

Once the server is running, the API will be available at `http://localhost:8080`.

## Configuration

Create `src/main/resources/db-dev.properties` based on the following template:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://<host>/<database>
spring.datasource.username=<username>
spring.datasource.password=<password>

# Redis
spring.data.redis.host=<host>
spring.data.redis.port=<port>
spring.data.redis.password=<password>

spring.jpa.hibernate.ddl-auto=update

# Mail
spring.mail.host=<host>
spring.mail.port=<port>
spring.mail.username=<username>
spring.mail.password=<password>
spring.mail.from=<email>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

server.address=0.0.0.0
spring.web.cors.allowed-origins=http://localhost:4200
```

## Building

To build the project, run:

```bash
./mvnw clean package -DskipTests
```

This will generate a WAR file in the `target/` directory.

## Running unit tests

To execute unit tests, run:

```bash
./mvnw test
```

## Additional Resources

For more information on using Spring Boot, visit the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/).
