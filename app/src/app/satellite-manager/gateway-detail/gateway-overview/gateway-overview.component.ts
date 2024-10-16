import { Component, OnInit } from "@angular/core";
import { GatewaysService } from "@inoa/api";
import { GatewayUpdateVO, GatewayVO } from "@inoa/model";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RpcMqttService } from "src/app/services/rpc-mqtt-service";

export interface SysInfo {
    [key: string]: any;
}

@Component({
    selector: "gc-gateway-overview",
    templateUrl: "./gateway-overview.component.html",
    styleUrl: "./gateway-overview.component.css"
})
export class GatewayOverviewComponent implements OnInit {

    sysData: SysInfo = {
        hardwareRevision: "",
        buildRevision: 0,
        buildDate: "",
        buildTime: ""
    };

    updateAvailable = false;

    constructor(
        private rpcService: RpcMqttService,
        public intercomService: InternalCommunicationService,
        private dialogService: DialogService,
        private gatewaysService: GatewaysService
    ) {}

    ngOnInit(): void {
        //sub to selected gateway changes
        this.intercomService.selectedGatewayChangedEventEmitter.subscribe(() => {
            this.readSysInfo();
        });
    }

    readSysInfo() {
        if (!this.intercomService.selectedGateway) return;

        this.intercomService.isLoadingSysInfo = true;
		
		this.rpcService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: "sys.version" }).subscribe((rpcResponse) => {
			this.sysData = this.parseRpcConfigResponse(rpcResponse.response);

            this.intercomService.isLoadingSysInfo = false;
        });
    }
    
    parseRpcConfigResponse(rpcResponse: string): SysInfo {
		const jsonData = JSON.parse(rpcResponse);

		const data: SysInfo = {
			hardwareRevision: jsonData.hardwareRevision,
			buildRevision: jsonData.buildRevision,
			buildDate: jsonData.buildDate,
			buildTime: jsonData.buildTime
        }
        
        // set the hardware version to be checked in other components
        this.intercomService.hardwareVersion = jsonData.hardwareRevision;

		return data;
    }

    renameSatelliteClick(gateway: GatewayVO | undefined, event: Event) {
        event.stopPropagation();

        if (!gateway) return;

        this.dialogService
            .openRenameSatelliteDialog(gateway.name ? gateway.name : "")
            ?.afterClosed()
            .subscribe((data) => {
                if (!data) return;
                this.updateSatelliteName(gateway, data.name);
            });
    }

    updateSatelliteName(gateway: GatewayVO, name: string) {
        const updateData: GatewayUpdateVO = {
            name: name
        };

        this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
            gateway.name = returnData.name;
        });
    }
}
