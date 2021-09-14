# Integration Tests

## How

This integration test is based on [docker-compose](src/test/compose/docker-compose.yaml). The compose setup is started each test run via [testcontainers](https://www.testcontainers.org/modules/docker_compose/).

```shell
mvn verify -pl test
```
Alternativly you can follow the [developer documentation](../docs/development.md) for getting a local instance up and running.

### Changes in Keycloak Realm

In case of changes for keycloak realm config, you only have to re-run this step instead of the whole packaging:
```shell
docker-compose -f test/target/compose/docker-compose.yaml --no-cache build keycloak
```

### Debugging

Add environment variable to your service you want to debug in your **docker-compose.yaml**

```
service:
  environment:
  - JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n
```

Expose port 8000 to a port of your choice to avoid port conflicts, e.g.:

```
burner-selector-service:
  ports:
  - 50054:8000
```

Copy your ressources and run **docker-compose**.

Create a Remote-Debug-Session in your IDE with the project open you want to debug.
