# customerflow-api

## Overview
CustomerFlow is a CRM software designed to manage customer relationships, business opportunities, and the organization of commercial processes. Developed in Java, Spring Boot and Oracle DB.

## Features
- Users management
- Contacts management
- Companies management
- Deals management

## Class diagram

![CustomerFlow class diagram](./images/class-diagram.png)


<details>
  <summary>Diagram code in PlantUML (planttext.com)</summary>

```
@startuml

class User {
    +id: UUID
    +name: String
    +email: String
    +password: String
}

class Contact {
    +id: UUID
    +firstname: String
    +lastname: String
    +email: String
    +jobTitle: String
    +phoneNumber: String
    +leadStatus: String
}

class Company {
    +id: UUID
    +name: String
    +domainName: String
    +industry: String
    +type: String
    +city: String
    +state: String
    +postalCode: String
    +numberOfEmployees: String
    +annualRevenue: String
    +timeZone: String
    +description: String
    +linkedinPage: String
}

class Deal {
    +id: UUID
    +name: String
    +amount: Decimal
    +description: String
    +closeDate: Date
    +type: String
    +priority: String
}

class Pipeline {
    +id: UUID
    +name: String
}

class DealStage {
    +id: UUID
    +name: String
    +orderIndex: Integer
}

User "1" --* "many" Company : owns
User "1" --* "many" Contact : owns
User "1" --* "many" Deal : owns
User "1" --* "many" Pipeline : owns
Company "many" *--* "many" Contact: associated with
Company "many" *--* "many" Deal : associated with
Contact "many" *--* "many" Deal : associated with

Pipeline "1" --* "many" DealStage : has
Pipeline "1" --* "many" Deal : contains
DealStage "1" --* "many" Deal : assigned to

@enduml
```

</details>

## Used Technologies

- Backend:
  - Java 17
  - Spring Web
  - Spring Data JPA
  - Oracle Driver
  - Spring Security
  - Lombok
  - Validation
  - ModelMapper
  - SpringDoc (OpenAPI and Swagger UI)
- Database: 
  - Oracle Database Free

## Setup (Initial Configuration)

Before running the application, follow these steps:

1. **Generate the environment variables file**
    ```zsh
   # Linux/macOS
    cp env_example .env
   
   # Windows (PowerShell)
   Copy-Item env_example -Destination .env
    ```
   
2. **Generate and assign the JWT key**
   - Use the `JwtSecretKeyMakerTest` class to generate the key.
   - Add the key to the `.env`  file under the `JWT_SECRET_KEY` variable.

## Running the Application

### Development Environment

1. Start the Oracle database:
    ```zsh
    docker compose up -d
    ```
2. Set up environment variables
   - Option 1 (IDE - recommended): In IntelliJ IDEA, go to `Run > Edit Configurations`, and select the `.env` to automatically load the environment variables.
   - Option 2 (Terminal - Manual):
       ```zsh
       set -a
       source .env
       set +a
       ```
5. Run the application from the IDE or terminal:
    ```zsh
    mvn spring-boot:run
    ```

### Testing Environment

Start the API along with the database (automatically loads the `.env` file):
```zsh
docker compose -f docker-compose-testing.yml up -d  
```

## Environment variables

| Variable           | Description                                                             | Required | Default value | Example / Possible Values                             |
|--------------------|-------------------------------------------------------------------------|----------|---------------|-------------------------------------------------------|
| DB_HOST            | Oracle Database Host                                                    | Yes      | -             | `localhost`                                           |
| DB_PORT            | Oracle Database Port                                                    | Yes      | -             | `1521`                                                |
| DB_PDB             | Oracle Pluggable Database name                                          | Yes      | -             | `customerflowpdb`                                     |
| DB_USERNAME        | Oracle Database Username                                                | Yes      | -             | `CUSTOMERFLOW_DEV`                                    |
| DB_PASSWORD        | Oracle Database Password                                                | Yes      | -             | `changeme`                                            |
| JWT_SECRET_KEY     | JWT Secret Key. Generate using `JwtSecretKeyMakerTest` class            | Yes      | -             | `B9KcRasDYfFpDKYavK70qUYukxKGJXLfTXaGrQuoGdc`         |
| JWT_EXPIRATION     | JWT expiration in milliseconds.                                         | Yes      | -             | `86400000`                                            |
| JPA_DDL_AUTO       | Controls how Hibernate handles database schema generation and updates.  | No | `none`        | `none`, `update`, `create`, `create-drop`, `validate` | 
| JPA_SHOW_SQL       | Enable Hibernate to display generated SQL queries in the console.       | No | `false`       | `true`, `false`                                       | 
| JPA_FORMAT_SQL     | Formats SQL queries in logs for better readability.                     | No | `false` | `true`, `false`                                       |
| API_DOCS_ENABLED   | Enables API documentation generation.                                   | No | `false` | `true`, `false`                                       |
| SWAGGER_UI_ENABLED | Enables Swagger UI for API visualization.                               | No | `false` | `true`, `false`                                       | 
| INITIAL_ADMIN_EMAIL | Email for the initial admin user, created when no users exist at startup. | No | - | `admin@admin.com`                                     | 
| INITIAL_ADMIN_PASSWORD | Password for the initial admin user, created when no users exist at startup. | No | - | `changeme`                                            |
