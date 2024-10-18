import { Component, OnInit } from "@angular/core";
import { GatewayVO } from "@inoa/model";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RpcMqttService } from "src/app/services/rpc-mqtt-service";

export interface ConfigData {
	[key: string]: any;
}

@Component({
	selector: "gc-gateway-configuration",
	templateUrl: "./gateway-configuration.component.html",
	styleUrl: "./gateway-configuration.component.css"
})
export class GatewayConfigurationComponent implements OnInit {
	configData: ConfigData = {
		wifiSid: "",
		wifiPassword: "",
		ethEnabled: false,
		bufferingEnabled: false,
		telnetEnabled: false,
		ntpHost: "",
		registryUrl: "",
		mqttUrl: "",
		mqttPort: 0,
		hawkbitHost: "",
		hawkbitBasePath: "",
		interfacesUart1Baud: 0,
		interfacesUart1DataBitMode: 0,
		interfacesUart1ParityMode: 0,
		interfacesUart1StopBitMode: 0,
		interfacesUart2Baud: 0,
		interfacesUart2DataBitMode: 0,
		interfacesUart2ParityMode: 0,
		interfacesUart2StopBitMode: 0,
		meteringCycleDuration: 0,
		loggingMqttLogging: false,
		loggingMqttConsole: false
	};

	constructor(private rpcService: RpcMqttService, public intercomService: InternalCommunicationService, private dialogService: DialogService) {}

	ngOnInit(): void {
		//sub to selected gateway changes
		this.intercomService.selectedGatewayChangedEventEmitter.subscribe(() => {
			this.readConfig();
		});
	}

	readConfig() {
		if (!this.intercomService.selectedGateway) return;

		this.intercomService.isLoadingConfig = true;

		this.rpcService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: "config.read" }).subscribe((rpcResponse) => {
			this.configData = this.parseRpcConfigResponse(rpcResponse.response);

			this.intercomService.isLoadingConfig = false;
		});
	}

	parseRpcConfigResponse(rpcResponse: string): ConfigData {
		const jsonData = JSON.parse(rpcResponse);

		const data: ConfigData = {
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
			interfacesUart2StopBitMode: jsonData.interfaces.uart2.stopBitMode
		};

		return data;
	}

	updateConfigurationClicked() {
		if (!this.intercomService.selectedGateway) return;

		// write the config data to the gateway
		this.rpcService.sendRpcCommand(this.intercomService.selectedGateway.gateway_id, { method: "config.write", params: this.createRpcConfigRequest(this.configData) }).subscribe(() => {});
	}

	formatMillisecTimeToSec(millisecTime: number): string {
		return (millisecTime / 1000).toString() + " s";
	}

	configEditClick(configNames: string[]) {
		if (this.intercomService.selectedGateway === undefined) return;

		this.dialogService
			.openConfigEditDialog({ configData: this.configData, configNames: configNames })
			?.afterClosed()
			.subscribe((data) => {
				if (!data) return;

				this.configData = data.configData;
				this.updateConfigurationClicked();
			});
	}

	createRpcConfigRequest(config: ConfigData): object {
		const jsonData = {
			wifi: {
				sid: config["wifiSid"],
				password: config["wifiPassword"]
			},
			eth: {
				enabled: config["ethEnabled"]
			},
			buffering: {
				enabled: config["bufferingEnabled"]
			},
			telnet: {
				enabled: config["telnetEnabled"]
			},
			metering: {
				cycleDuration: config["meteringCycleDuration"]
			},
			logging: {
				mqttLogging: config["loggingMqttLogging"],
				mqttConsole: config["loggingMqttConsole"]
			},
			ntp: {
				host: config["ntpHost"]
			},
			registry: {
				url: config["registryUrl"]
			},
			mqtt: {
				url: config["mqttUrl"],
				port: config["mqttPort"]
			},
			hawkbit: {
				host: config["hawkbitHost"],
				basepath: config["hawkbitBasePath"]
			},
			interfaces: {
				uart1: {
					baud: config["interfacesUart1Baud"],
					dataBitMode: config["interfacesUart1DataBitMode"],
					parityMode: config["interfacesUart1ParityMode"],
					stopBitMode: config["interfacesUart1StopBitMode"]
				},
				uart2: {
					baud: config["interfacesUart2Baud"],
					dataBitMode: config["interfacesUart2DataBitMode"],
					parityMode: config["interfacesUart2ParityMode"],
					stopBitMode: config["interfacesUart2StopBitMode"]
				}
			}
		};

		return jsonData;
	}

	restartClick(gateway: GatewayVO | undefined) {
		if (!gateway) return;

		this.rpcService.sendRpcReboot(gateway.gateway_id);

		gateway.status!.mqtt!.connected = false;
	}
}
