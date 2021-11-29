# CNPM

## Modules

* [tenant-api-management](/tenant-api-management): rest api for tenant management
* [tenant-api-messaging](/tenant-api-messaging): async api for tenant updates
* [tenant-service](/tenant-service): service implementing management/messaging api

## Validate OpenAPI before Github Action

```sh
docker run --rm -v $PWD:/lint redocly/openapi-cli lint /lint/tenant-api-messaging/src/main/resources/openapi/spec.yaml --skip-rule no-unused-components --skip-rule no-empty-servers
docker run --rm -v $PWD:/lint redocly/openapi-cli lint /lint/tenant-api-management/src/main/resources/openapi/spec.yaml
```
