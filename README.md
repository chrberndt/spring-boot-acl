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
