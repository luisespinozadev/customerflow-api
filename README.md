# customerflow-api


## Dependencies
- Spring Web
- Lombok
- Spring Data JPA
- Oracle Driver
- Validation
- ModelMapper

## Setup Instructions

**1. Setup DB**
```zsh
docker pull container-registry.oracle.com/database/free:23.7.0.0-lite-amd64

docker volume create customerflow-oracle-data

docker run -d --name customerflow-oracle-db \
-p 1521:1521 \
-e ORACLE_PWD=changeme \
-e ORACLE_CHARACTERSET=AL32UTF8 \
-e ORACLE_PDB=customerflowpdb \
-v customerflow-oracle-data:/opt/oracle/oradata \
container-registry.oracle.com/database/free:23.7.0.0-lite-amd64
```

Connect to DB with credentials:
- Host: localhost or IP 
- Port: 1521 
- Database: customerflowpdb
- User: system
- Password: changeme

Create new schema:
```sql
CREATE USER CUSTOMERFLOW_DEV IDENTIFIED BY "changeme";
GRANT CONNECT, RESOURCE TO CUSTOMERFLOW_DEV;
GRANT CREATE SESSION TO CUSTOMERFLOW_DEV;
GRANT UNLIMITED TABLESPACE TO CUSTOMERFLOW_DEV;
```


