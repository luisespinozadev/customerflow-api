# customerflow-api

## Overview

## Spring Dependencies
- Spring Web
- Lombok
- Spring Data JPA
- Oracle Driver
- Validation
- ModelMapper

## Requirements
- Java 17
- Maven
- Docker and Docker Compose
- SQL client (optional)

## Running in Development Environment

1. Run Oracle DB server
    ```zsh
    docker compose up -d
    ```
2. Start app from IDE:
    ```zsh
    mvn spring-boot:run
    ```

## Running in Testing Environment

The following command will buill an image and run a container of the API with the database server.
```zsh
docker compose -f docker-compose-testing.yml up -d  
```