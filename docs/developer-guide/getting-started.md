# Getting Started

## Development

### Prerequisites

Please install the following components to your system. Otherwise, you will face errors during the setup process!

* Git >= 2.25
* Java >= 17.0.8
* Maven >= 3.6.3
* Docker >= 24.0.5
* Python >= 3.8.5 (for documentation only)

### Setup developer environment

* Clone the project from `git@github.com:inoa-io/inoa-cloud.git`
* Use an IDE of your choice, recommended is ItelliJ
* Import the project as Maven project in your IDE
* Be sure all annotation processors are active in your IDE

### Local Build

```bash
mvn clean install -DskipTests
```

### Local Start

```bash
mvn k3s:rm -Dk3s.includeCache=false && mvn pre-integration-test -Dk3s.failIfExists=false -pl ./inoa-test/
```

Browse to [http://help.127.0.0.1.nip.io:8080/](http://help.127.0.0.1.nip.io:8080/).

To bind a local Satellite Gateway, go to the [Debug Console](https://inoa-io.github.io/inoa-os-esp32/user-guide/debug-console/) and type `setup-mqtt mqtt://YOUR_COMPUTERS_IP_IN_YOUR_NETWORK:1883`.

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
