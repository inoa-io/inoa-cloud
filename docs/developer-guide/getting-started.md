# Getting Started

## Development Environment

### Prerequisites

Please install the following components to your system. Otherwise, you will face errors during the setup process!

* Git >= 2.25
* Java >= 17.0.8
* Maven >= 3.6.3
* Docker >= 24.0.5
* Python >= 3.8.5 (for documentation only)

### Setup developer environment

* Clone the project from `git@github.com:inoa-io/inoa-cloud.git`
* Use an IDE of your choice, recommended is IntelliJJ
* Import the project as Maven project in your IDE
* Be sure all annotation processors are active in your IDE

### Local Build Roy Version

Adapt the ip to your local one first

```bash
mvn k3s:rm
mvn clean install -DskipTests
mvn pre-integration-test -Dk3s.failIfExists=false -Dk3s.ip=192.168.xxx.xxx -pl ./test/
```

### Linux Scripts

```bash
# Launch INOA Cloud Locally
./inoa-startup.sh

# Stop the local instance
./inoa-shutdown.sh
```

### Windows Scripts

t.b.d.

### Bind local Satellite

Configure your Satellite to connect to your local running INOA Cloud services via [Debug Console](https://inoa-io.github.io/inoa-os-esp32/user-guide/debug-console/):

```shell
# set with your INOA_IP
setup-registry http://inoa.<INOA_IP>.nip.io:8080/gateway
setup-mqtt mqtt://inoa.<INOA_IP>.nip.io 1883
```

### Coding rules

* We use checkstyle, eslint, yamllint

### Troubleshooting

#### Debugging

![Work in progress](../assets/images/workinprogress.png)

### Testing

#### Unit Testing

![Work in progress](../assets/images/workinprogress.png)

#### Integration Testing

![Work in progress](../assets/images/workinprogress.png)
