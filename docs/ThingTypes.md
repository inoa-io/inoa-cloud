# Thing Types and how to configure them

Here you can find the latest and greatest Information about how to configure your things.

## URN Format

- The format is always: urn:\<thingType>:\<thingId>:\<sensor>
  <br>
- <b>\<thingType></b> can currently be one of the following options:
  - s0
  - shellyplug-s
  - shellyplusplugs
  - shellyht
  - dvmodbusir
  - dvh4013
  - mdvh4006
    <br>
- <b>\<thingId></b> can be chosen freely. We usually use the MAC of the thing.
  <br>
- <b>\<sensor></b> is ThingType specific.
  - For S0 type the only option is "gas" right now.
  - For Shelly plugs the only option is "status" right now.

## Shelly Plug S

RPC-Command:

```shell
dp.add.http.get
```

Parameters:

```json
{
    "id": "urn:shellyplug-s:XXXXXXXXXXXX:status",
    "name": "Name of your Shelly Plug S",
    "enabled": true,
    "uri": "http://192.168.1.XXX/status"
}
```

- For the parameter uri fill in the last part of the ip of your shelly plug

## Shelly Plus Plug S

RPC-Command:

```shell
dp.add.http.get
```

Parameters:

```json
{
    "id": "urn:shellyplusplugs:XXXXXXXXXXXX:status",
    "name": "Name of your Shelly Plus Plug S",
    "enabled": true,
    "uri": "http://192.168.1.XXX/rpc/Shelly.GetStatus"
}
```

- For the parameter uri fill in the last part of the ip of your shelly plug

## S0

RPC-Command:

```shell
dp.add.s0
```

Parameters:

```json
{
    "id": "urn:s0:XXXXXXXXXXXX:gas",
    "name": "Name of your S0 sensor",
    "enabled": true,
    "interface": 1
}
```

- The parameter interface represents the 2 channels S0 has access to, so it should be either 1 or 2

<br>
This document is still WIP, that's why not every thing is explained yet
