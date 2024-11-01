# INOA Fleet Service

## Context

The Fleet service is build with help of [Micronaut](https://micronaut.io/) and offers three main use cases:

1. Managing the IoT Fleet
2. Be counter-part for IoT gateways "INOA Satellite"
3. Hosting the single page application [INOA Ground Control](../app/README.md)

## Developer Setup

It is part of the maven project and therefor directly build with `mvn package` in the root of this repository.

Additionally, you can simply launch it from your IDE, e.g. in InelliJ with the prepared launch configuration:

![Launch Fleet](../docs/assets/launch-fleet.png)

This will execute the Micronaut Application directly in a JVM at your machine.
Take care to source your [.env](../.env) file before launch to use the correct configurations.
