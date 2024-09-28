import { OnInit, Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { RpcMqttService } from "../../services/rpc-mqtt-service";
import { InternalCommunicationService } from "../../services/internal-communication-service";

export interface ConfigEditDialogData {
    // wifi config
    wifiSid: string;
    wifiPassword: string;

    // eth config
    ethEnabled: boolean;

    // buffering config
    bufferingEnabled: boolean;

    // telnet config
    telnetEnabled: boolean;

    // ntp config
    ntpHost: string;

    // registry config
    registryUrl: string;

    // mqtt config
    mqttUrl: string;
    mqttPort: number;

    // hawkbit config
    hawkbitHost: string;
    hawkbitBasePath: string;

    // interfaces config 1
    interfacesUart1Baud: number;
    interfacesUart1DataBitMode: number;
    interfacesUart1ParityMode: number;
    interfacesUart1StopBitMode: number;

    // interfaces config 2
    interfacesUart2Baud: number;
    interfacesUart2DataBitMode: number;
    interfacesUart2ParityMode: number;
    interfacesUart2StopBitMode: number;

    // metering config
    meteringCycleDuration: number;

    // logging config
    loggingMqttLogging: boolean;
    loggingMqttConsole: boolean;

    [key: string]: string | number | boolean;
}

interface ConfigItem {
    key: string;
    label: string;
    type: "input" | "toggle";
    inputType?: string;
}

interface ConfigGroup {
    name: string;
    items: ConfigItem[];
}

@Component({
    selector: "gc-config-edit-dialog",
    templateUrl: "./config-edit-dialog.component.html",
    styleUrls: ["./config-edit-dialog.component.css"]
})
export class ConfigEditDialogComponent implements OnInit {
    configGroups: ConfigGroup[] = [
        {
            name: "WiFi",
            items: [
                { key: "wifiSid", label: "WiFi SID", type: "input" },
                { key: "wifiPassword", label: "WiFi Password", type: "input", inputType: "password" }
            ]
        },
        {
            name: "Ethernet",
            items: [{ key: "ethEnabled", label: "Ethernet Enabled", type: "toggle" }]
        },
        {
            name: "Buffering",
            items: [{ key: "bufferingEnabled", label: "Buffering Enabled", type: "toggle" }]
        },
        {
            name: "Telnet",
            items: [{ key: "telnetEnabled", label: "Telnet Enabled", type: "toggle" }]
        },
        {
            name: "NTP",
            items: [{ key: "ntpHost", label: "NTP Host", type: "input" }]
        },
        {
            name: "Registry",
            items: [{ key: "registryUrl", label: "Registry URL", type: "input" }]
        },
        {
            name: "MQTT",
            items: [
                { key: "mqttUrl", label: "MQTT URL", type: "input" },
                { key: "mqttPort", label: "MQTT Port", type: "input", inputType: "number" }
            ]
        },
        {
            name: "Hawkbit",
            items: [
                { key: "hawkbitHost", label: "Hawkbit Host", type: "input" },
                { key: "hawkbitBasePath", label: "Hawkbit Base Path", type: "input" }
            ]
        },
        {
            name: "Interface 1",
            items: [
                { key: "interfacesUart1Baud", label: "UART 1 Baud", type: "input", inputType: "number" },
                { key: "interfacesUart1DataBitMode", label: "UART 1 Data Bit Mode", type: "input", inputType: "number" },
                { key: "interfacesUart1ParityMode", label: "UART 1 Parity Mode", type: "input", inputType: "number" },
                { key: "interfacesUart1StopBitMode", label: "UART 1 Stop Bit Mode", type: "input", inputType: "number" }
            ]
        },
        {
            name: "Interface 2",
            items: [
                { key: "interfacesUart2Baud", label: "UART 2 Baud", type: "input", inputType: "number" },
                { key: "interfacesUart2DataBitMode", label: "UART 2 Data Bit Mode", type: "input", inputType: "number" },
                { key: "interfacesUart2ParityMode", label: "UART 2 Parity Mode", type: "input", inputType: "number" },
                { key: "interfacesUart2StopBitMode", label: "UART 2 Stop Bit Mode", type: "input", inputType: "number" }
            ]
        },
        {
            name: "Metering",
            items: [{ key: "meteringCycleDuration", label: "Metering Cycle Duration", type: "input", inputType: "number" }]
        },
        {
            name: "Logging",
            items: [
                { key: "loggingMqttLogging", label: "MQTT Logging", type: "toggle" },
                { key: "loggingMqttConsole", label: "MQTT Console", type: "toggle" }
            ]
        }
    ];

    selectedGroup: ConfigGroup;

	constructor(
        public dialogRef: MatDialogRef<ConfigEditDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: ConfigEditDialogData,
		private rpcService: RpcMqttService,
		private intercomService: InternalCommunicationService
	) {
        this.selectedGroup = this.configGroups[0];
    }

    ngOnInit(): void {
		console.log("Trying to read config via RPC");
		
		this.rpcService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: "config.read" }).subscribe((rpcResponse) => {
			this.data = this.parseRpcConfigResponse(rpcResponse.response);
        });
	}
	
	parseRpcConfigResponse(rpcResponse: string): ConfigEditDialogData {
		const jsonData = JSON.parse(rpcResponse);

		const data: ConfigEditDialogData = {
			wifiSid: jsonData.wifi.sid,
			wifiPassword: jsonData.wifi.password,
			ethEnabled: jsonData.eth.enabled,
			bufferingEnabled: jsonData.buffering.enabled,
			telnetEnabled: jsonData.telnet.enabled,
			meteringCycleDuration: jsonData.metering.cycleDuration,
			loggingMqttLogging: jsonData.logging.mqttLogging,
			loggingMqttConsole: jsonData.logging.mqttConsole,
			ntpHost: jsonData.ntp.host,
			registryUrl: jsonData.registry.url,
			mqttUrl: jsonData.mqtt.url,
			mqttPort: jsonData.mqtt.port,
			hawkbitHost: jsonData.hawkbit.host,
			hawkbitBasePath: jsonData.hawkbit.basepath,
			interfacesUart1Baud: jsonData.interfaces.uart1.baud,
			interfacesUart1DataBitMode: jsonData.interfaces.uart1.dataBitMode,
			interfacesUart1ParityMode: jsonData.interfaces.uart1.parityMode,
			interfacesUart1StopBitMode: jsonData.interfaces.uart1.stopBitMode,
			interfacesUart2Baud: jsonData.interfaces.uart2.baud,
			interfacesUart2ParityMode: jsonData.interfaces.uart2.parityMode,
			interfacesUart2DataBitMode: jsonData.interfaces.uart2.dataBitMode,
			interfacesUart2StopBitMode: jsonData.interfaces.uart2.stopBitMode,
		}

		return data;
	}

    selectGroup(group: ConfigGroup): void {
        this.selectedGroup = group;
    }

    close(): void {
        this.dialogRef.close();
	}
	
	save(): void {
		this.dialogRef.close(this.data);
	}
}
