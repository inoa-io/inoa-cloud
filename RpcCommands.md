### RPC Commands

One can perform remote procedure calls via MQTT or REST.
The REST endpoint can be used as: `curl Satellite-XXXXXX.local/rpc -X POST -d "{\"id\": 1, \"method\": \"rpc.list\"}"`
The XXXXXX section represents the last 6 characters of the gateway_id.

### RPC Table

| Command | Description | Parameters |
|-|-|-|
| rpc.list | List RPCs | N/A |
| sys.reboot | Reboot | N/A |
| sys.wink | Wink with Status LED | N/A |
| sys.heap | Current free heap | N/A |
| sys.free | Current free RAM | N/A |
| sys.version | Current version | N/A |
| sys.vpn.stop | Stop VPN | N/A |
| sys.vpn.start | Start VPN | Endpoint |
| status.read | Current status | N/A |
| config.write | Write config | Config Params |
| config.read | Read config | N/A |
| dp.write | Write data points | List of Datapoints |
| dp.list | Read data points | N/A |
| dp.add.s0 | Add an s0 datapoint | S0 Add Params |
| dp.add.http.get | Add an http datapoint | Http Add Params |
| dp.add.rs485 | Add an rs485 datapoint | RS485 Add Params |
| dp.clear | Delete all datapoints| N/A |
| dp.delete | Delete datapoint | Datapoint ID |
| dp.get | Get full datapoint info | Datapoint ID |
| metering.rs485.frame | Sends a RS485 request frame | UART Frame Params |

### RPC Param Examples

Http Add Params:

```
{
    "id": "urn:shplg-s:C8C9A3B8FC2A:status",
    "name": "Mary Shelly",
    "enabled": true,
    "urn": "http://192.168.1.7/status"
}
```

Datapoint ID:

```
{
    "id": "urn:shplg-s:C8C9A3B8FC2A:status"
}
```

UART Frame Params:

```
{
    "uart": 1, "frame": "090340000002D083"
}
```