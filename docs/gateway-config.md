# Gateway Configuration

## Satellite 1.1

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

