# Gateway Configuration

## Context

One of the main use cases of INOA is the remote configuration of IoT devices, especially our INOA Satellites.
This concept will explain:

* What we configure?
* How we configure?
* How we manage the configuration?

## Satellite Configuration

```yaml
wifi:
  sid: grayc
  password: <PASSWORD>
ethernet:
  enabled: false
offlineBuffering:
  enabled: false
telnetConsole:
  enabled: false
mqtt:
  host: mqtt://inoa.192.168.20.10.nip.io
  port: 1883
ntp:
  host: pool.ntp.org
registry:
  url: http://inoa.192.168.20.10.nip.io:8080/gateway
ota:
  host: http://hawkbit.192.168.20.10.nip.io:8080
  basePath: /update/inoa/controller/v1
metering:
  cycleDuration: 60000
logging:
  mqttLogging: false
  mqttConsole: false
interfaces:
  uart1:
    baud: 9600
    dataBitMode: 3
    parityMode: 0
    stopBitMode: 1
  uart2:
    baud: 9600
    dataBitMode: 3
    parityMode: 0
    stopBitMode: 1

```

## Configuration Hierarchy

In the Configuration Service we do have 4 layers of Configuration to make configuration as easy as possible.

1. **Instance Layer** - Configurations are set into the deployed INOA instance. They are global defaults for the environment this instance is running on.
2. **Tenant Layer** - Configurations are stored into the database in the table `tenant_configuration`. These values override the instance layer on a per-tenant level.
3. **Group Layer** - Configurations are stored in the table `group_configuration`. These values override the instance and the tenant layer per logical group of Satellites.
4. **Gateway LAmer** - Configurations at this layer are stored in `gateway_configuration` table. These are the concrete values stored per Satellite and override all the values in the layers above.
