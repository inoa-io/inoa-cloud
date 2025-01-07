import { ChangeDetectorRef, Component } from "@angular/core";
import { GatewaysService } from "@inoa/api";
import { GatewayUpdateVO, GatewayVO, RpcCommandVO } from "@inoa/model";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RoutingService } from "src/app/services/routing-service";
import { RpcMqttService } from "src/app/services/rpc-mqtt-service";

@Component({
	selector: "gc-gateway-detail",
	templateUrl: "./gateway-detail.component.html",
	styleUrl: "./gateway-detail.component.css"
})
export class GatewayDetailComponent {
	jsonCode = "{ \"method\": \"rpc.list\" }";

	alert = false;

	monacoOptions = {
		theme: "vs-dark",
		language: "json",
		automaticLayout: true,
		fontSize: 18,
		scrollBeyondLastLine: false,
		minimap: { enabled: false }
	};

	constructor(
		public intercomService: InternalCommunicationService,
		private rpcMqttService: RpcMqttService,
		private changeDetector: ChangeDetectorRef,
		private routingService: RoutingService,
		private gatewaysService: GatewaysService
	) {}

	restartClick(gateway: GatewayVO | undefined, event: Event) {
		event.stopPropagation();

		if (!gateway) return;

		this.rpcMqttService.sendRpcReboot(gateway.gateway_id);

		gateway.status!.mqtt!.connected = false;
	}

	sendRPC() {
		if (this.intercomService.selectedGateway) {
			const rpcCommand: RpcCommandVO = JSON.parse(this.jsonCode);

			this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway.gateway_id, rpcCommand).subscribe();
			this.changeDetector.detectChanges();
		}
	}

	deselectGateway() {
		this.intercomService.selectedGateway = undefined;
		this.routingService.navigateName("satellite-manager");
	}

	toggleEnabledClick(gateway: GatewayVO, enable: boolean) {
		const updateData: GatewayUpdateVO = { enabled: enable };

		this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
			gateway.enabled = returnData.enabled;
		});
	}
}
