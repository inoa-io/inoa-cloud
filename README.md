# INOA IoT Platform

INOA is a lightweight IoT integration platform that
will help you to easily start with your IoT project and offer you the needed infrastructure to develop, deploy, maintain
and scale your solutions. With INOA we want to enable users to stay in control of their IoT gateways and measured and/or
controlled devices.

## Developer Setup

### Pre-Requisites

#### Hardware

* >= 32GB RAM

#### Development Tools

* [Git](https://git-scm.com/) >= 2.25
* [Java Development Kit](https://openjdk.org/install/) >= 17.0.8 & < 21
* [Maven](https://maven.apache.org/) >= 3.6.3
* [Docker](https://www.docker.com/) >= 24.0.5
* [k3s](https://k3s.io/)
* [Python](https://www.python.org/) >= 3.8.5 (for documentation only)
* [Telepresence](https://www.getambassador.io/docs/telepresence/latest/install) >= v2.19.6

#### Repository Access

* GitHub Container Registry - use `docker login ghcr.io` to initially authenticate against GitHub container registry
  * `ghcr.io/grayc-de`
  * `ghcr.io/inoa-io`

### Start Local Instance

1. Build INOA Cloud services and start the whole environment via k3s:

    ```shell
    mvn k3s:rm
    mvn clean install
    mvn clean pre-integration-test -Dk3s.failIfExists=false -pl ./test/
    ```

1. Check the now running services via [http://help.127.0.0.1.nip.io:8080/](http://help.127.0.0.1.nip.io:8080/).
1. Start a local instance of INOA Ground Control for UI development:

    ```shell
    cd app
    yarn install
    ng serve
    ```

### Connect INOA Satellite

To bind a local Satellite Gateway, go to the [Debug Console](https://inoa-io.github.io/inoa-os-esp32/user-guide/debug-console/) and type `setup-mqtt mqtt://YOUR_COMPUTERS_IP_IN_YOUR_NETWORK 1883`.

## INOA Modules

### INOA Fleet

INOA Fleet is the swiss army knife to manage your IoT devices and gateways in a simple way. With help of some great open
source tools, it is the cockpit for a fleet of IoT devices.

### INOA Measurement

INOA Measurements is the place where all the measured data is going in. It collects, stores, enhances measurements that
are collected by IoT devices and provide it with help of APIs and UIs.

### INOA Ground Control

INOA Ground Control is the UI to manage the IoT Fleet with INOA.

### INOA OS

INOA OS is our operating system for INOA connected IoT devices and gateways.

Please find the documentation [here](https://inoa-io.github.io/inoa-os-esp32/).

### INOA Satellite

The INOA-Satellite is a small IoT gateway made for gathering various of different measurements and forwarding them to Inoa
Measurement.

Please find the documentation [here](https://inoa-io.github.io/satellite/).
