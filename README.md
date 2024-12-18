# spring-boot-acl

Explore and demonstrate how to use Spring Boot's Domain Object Security (ACLs)

## Prerequisites

`docker` and `docker compose` (>= v2.31.0) installed and running in root-less mode

## Running

In order to run the application, currently you have to uncomment the following lines in `ACLContext.java`

```java
jdbcMutableAclService.setClassIdentityQuery("SELECT @@IDENTITY");
jdbcMutableAclService.setSidIdentityQuery("SELECT @@IDENTITY");
```

```bash
./gradlew bootRun
```

## Testing

```bash
./gradlew test
```
You can review the application with test data by running

```bash
./gradlew bootTestRun
```

The (in memory) database with prepopulated test data can be inspected at

http://localhost:8080/h2-console

Make sure to use `jdbc:h2:mem:test` as JDBC URL

For the remaining properties leave the defaults which are `sa` as User Name and an empty password.