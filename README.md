# Inoa IoT Platform

Inoa is a lightweight IoT integration platform that
will help you to easily start with your IoT project and offer you the needed infrastructure to develop, deploy, maintain
and scale your solutions. With Inoa we want to enable users to stay in control of their IoT gateways and measured and/or
controlled devices.

## Developer Setup

### Pre-Requisites

1. [Docker](https://www.docker.com/)
2. [k3s](https://k3s.io/)
3. [Java Development Kit](https://openjdk.org/install/)
4. [Maven](https://maven.apache.org/)

### Start Local Instance

```shell
mvn k3s:rm
mvn clean install
mvn clean pre-integration-test -Dk3s.failIfExists=false -pl ./inoa-test/
```

Afterwards, you may browse to [http://help.127.0.0.1.nip.io:8080/](http://help.127.0.0.1.nip.io:8080/).

### Connect INOA Satellite

TODO

## INOA Modules

### INOA Fleet

INOA Fleet is the swiss army knife to manage your IoT devices and gateways in a simple way. With help of some great open
source tools, it is the cockpit for a fleet of IoT devices.

### INOA Measurement

INOA Measurements is the place where all the measured data is going in. It collects, stores, enhances measurments that
are collected by IoT devices and provide it with help of APIs and UIs.

### INOA Groundcontrol

TODO

### INOA OS

INOA OS is our operating system for Inoa connected IoT devices and gateways.

Please find the documentation [here](https://inoa-io.github.io/inoa-os-esp32/).

### INOA Satellite

The INOA-Satellite is small IoT gateway made for gathering various of different measurements and forwarding them to Inoa
Measurement.

Please find the documentation [here](https://inoa-io.github.io/satellite/).