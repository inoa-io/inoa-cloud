import { AfterViewInit, Component, ElementRef, ViewChild } from "@angular/core";
import { GatewaysService } from "@inoa/api";
import { GatewayUpdateVO, GatewayVO } from "@inoa/model";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { MapService } from "src/app/services/map-service";
import { PropertiesService } from '@inoa/api';
import * as L from "leaflet";

export interface SysInfo {
    [key: string]: any;
}

@Component({
    selector: "gc-gateway-overview",
    templateUrl: "./gateway-overview.component.html",
    styleUrl: "./gateway-overview.component.css"
})
export class GatewayOverviewComponent implements AfterViewInit {
    @ViewChild("mapRef") mapContainer!: ElementRef;
    map!: L.Map;
    resizeObserver?: ResizeObserver;

    sysData: SysInfo = {
        hardwareRevision: "",
        buildRevision: 0,
        buildDate: "",
        buildTime: ""
    };

    updateAvailable = false;

    constructor(
        public mapService: MapService,
        public intercomService: InternalCommunicationService,
        private dialogService: DialogService,
        private gatewaysService: GatewaysService,
        private propertiesService: PropertiesService
    ) {}

    ngAfterViewInit(): void {
        setTimeout(() => {
            this.readSysInfo();
            this.initMap();
            this.updateMap();

            //sub to selected gateway changes
            this.intercomService.selectedGatewayChangedEventEmitter.subscribe(() => {
                this.readSysInfo();

                setTimeout(() => {
                    this.updateMap();
                }, 0);
            });

            //sub to gateway location changes
            this.mapService.updateMapEventEmitter.subscribe(() => {
                setTimeout(() => {
                    this.updateMap();
                }, 0);
            });
        }, 0);
    }
    initMap() {
        // Initialize the map
        this.map = L.map(this.mapContainer.nativeElement, {
            zoomControl: false,
            dragging: false,
            scrollWheelZoom: false,
            attributionControl: false,
            doubleClickZoom: false
        });

        this.mapService.switchMapType(this.map, "normal");

        // Set up resize observer
        this.resizeObserver = new ResizeObserver(() => {
            this.map.invalidateSize();
        });
        this.resizeObserver.observe(this.mapContainer.nativeElement);
    }
    updateMap() {
        this.mapService.moveMapToSelectedGatewayLocation(this.map);
        this.map.invalidateSize();
    }

    readSysInfo() {
        if (!this.intercomService.selectedGateway) return;

        this.intercomService.isLoadingSysInfo = true;

      // TODO: Use: this.propertiesService.getProperties()
    }

    parseRpcConfigResponse(rpcResponse: string): SysInfo {
        const jsonData = JSON.parse(rpcResponse);

        const data: SysInfo = {
            hardwareRevision: jsonData.hardwareRevision,
            buildRevision: jsonData.buildRevision,
            buildDate: jsonData.buildDate,
            buildTime: jsonData.buildTime
        };

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

    locationEditorClick(gateway: GatewayVO | undefined, event: Event) {
        event.stopPropagation();

        if (!gateway) return;

        this.dialogService
            .openLocationEditorDialog(gateway.location ? gateway.location : null)
            ?.afterClosed()
            .subscribe((data) => {
                if (!data) return;
                this.updateSatelliteLocation(gateway, data.location);
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

    updateSatelliteLocation(gateway: GatewayVO, location: any) {
        const updateData: GatewayUpdateVO = {
            location: location
        };

        this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
            gateway.location = returnData.location;

            //raise event to inform subbed components of location change
            this.mapService.raiseUpdateMapEvent();
        });
    }
}
