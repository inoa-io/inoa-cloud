import { ChangeDetectorRef, Component } from "@angular/core";
import { GatewaysService } from "@inoa/api";
import { GatewayUpdateVO, GatewayVO, RpcCommandVO } from "@inoa/model";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RoutingService } from "src/app/services/routing-service";
import { RemoteService } from "@inoa/api"

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
		private changeDetector: ChangeDetectorRef,
		private routingService: RoutingService,
		private gatewaysService: GatewaysService,
		private remoteService: RemoteService
	) {}

	restartClick(gateway: GatewayVO | undefined, event: Event) {
		event.stopPropagation();

		if (!gateway) return;

		this.remoteService.reboot(gateway.gateway_id);

		gateway.status!.mqtt!.connected = false;
	}

	sendRPC() {
		if (this.intercomService.selectedGateway) {
			const rpcCommand: RpcCommandVO = JSON.parse(this.jsonCode);

			// TODO: This will not work anymore. We currently cannot send free-from commands. Only dedicated REST methods are allowed!
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
