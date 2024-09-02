import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { GatewaysService, GatewayUpdateVO, GatewayVO, RpcCommandVO, ThingsService, ThingTypesService, ThingVO } from "@inoa/api";
import { InternalCommunicationService } from "../internal-communication-service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { RpcMqttService } from "../rpc-mqtt-service";
import { RpcExchange, RpcHistoryService } from "../rpc-history-panel/rpc-history-service";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { interval, Subscription, switchMap } from "rxjs";
import { DialogService } from "../dialog-service";
import { UtilityService } from "../utility-service";
import { SelectionModel } from "@angular/cdk/collections";

@Component({
    selector: "gc-satellite-manager",
    templateUrl: "./satellite-manager.component.html",
    styleUrls: ["./satellite-manager.component.css"],
    animations: [
        trigger("detailExpand", [
            state("collapsed", style({ height: "0px", minHeight: "0" })),
            state("expanded", style({ height: "*" })),
            transition("expanded <=> collapsed", animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"))
        ])
    ]
})
export class SatelliteManagerComponent implements AfterViewInit, OnInit, OnDestroy {
    @ViewChild("sortRef") sort!: MatSort;
    @ViewChild("paginatorRef") paginator!: MatPaginator;
    @ViewChild("tableRef") table!: MatTable<GatewayVO>;

    expandedElement: RpcExchange | null = null;

    displayedColumnsRpcHistory: string[] = ["method", "id", "status"];
    displayedColumnsGatewayTable: string[] = ["select", "gateway_id", "name", "enabled", "status", "actions"];
    displayedColumnsGatewayTableMini: string[] = ["gateway_id"];
    displayedColumnsThingsTable: string[] = ["gateway_id", "category", "name", "actions"];

    dataSourceGateways = new MatTableDataSource<GatewayVO>();
    selectionGateways = new SelectionModel<GatewayVO>(true, []);
    dataSourceThings = new MatTableDataSource<ThingVO>();

    selectedTabIndex = 0;

    jsonCode = "{ \"method\": \"rpc.list\" }";

    monacoOptions = {
        theme: "vs-dark",
        language: "json",
        automaticLayout: true,
        fontSize: 18,
        scrollBeyondLastLine: false,
        minimap: { enabled: false }
    };

    measurements = this.formBuilder.group({
        water: false,
        heat: false,
        power: true
    });

    private autoTableRefresher!: Subscription;
    autoRefreshIntervals = ["Off", "1s", "5s", "10s", "30s"];
    autoRefreshInterval = "5s";

    constructor(
        private formBuilder: FormBuilder,
        private gatewaysService: GatewaysService,
        private thingsService: ThingsService,
        private rpcMqttService: RpcMqttService,
        public rpcHistoryService: RpcHistoryService,
        public intercomService: InternalCommunicationService,
        private thingTypesService: ThingTypesService,
        private changeDetector: ChangeDetectorRef,
        private dialogService: DialogService,
        public utilityService: UtilityService
    ) {
        this.intercomService.selectedGateway = undefined;
    }

    ngOnInit() {
        this.startAutoRefresh();

        this.thingTypesService.findThingTypes().subscribe((data) => {
            this.intercomService.thingTypes = data.content;
        });
    }

    /** Whether the number of selected elements matches the total number of rows. */
    isAllSelected() {
        const numSelected = this.selectionGateways.selected.length;
        const numRows = this.dataSourceGateways.data.length;
        return numSelected === numRows;
    }

    /** Selects all rows if they are not all selected; otherwise clear selection. */
    toggleAllRows() {
        if (this.isAllSelected()) {
            this.selectionGateways.clear();
            return;
        }

        this.selectionGateways.select(...this.dataSourceGateways.data);
    }

    /** The label for the checkbox on the passed row */
    checkboxLabel(row?: GatewayVO): string {
        if (!row) {
            return `${this.isAllSelected() ? "deselect" : "select"} all`;
        }
        return `${this.selectionGateways.isSelected(row) ? "deselect" : "select"} row ${row.gateway_id}`;
    }

    startAutoRefresh() {
        // Cancel existing auto-refresh subscription if it exists
        if (this.autoTableRefresher) {
            this.autoTableRefresher.unsubscribe();
        }

        // Always make an initial API call
        this.gatewaysService.findGateways().subscribe((data) => {
            this.dataSourceGateways.data = data.content;
        });

        // Parse autoRefreshInterval
        const autoRefreshInterval = this.autoRefreshInterval === "Off" ? 0 : parseInt(this.autoRefreshInterval.substring(0, this.autoRefreshInterval.length - 1)) * 1000;

        // If autoRefreshInterval is  0, don't set up interval
        if (autoRefreshInterval === 0) return;

        // otherwise do set up an interval
        this.autoTableRefresher = interval(autoRefreshInterval)
            .pipe(switchMap(() => this.gatewaysService.findGateways()))
            .subscribe((data) => {
                this.dataSourceGateways.data = data.content;

                const selectedGateway = data.content.find((gateway) => gateway.gateway_id === this.intercomService.selectedGateway?.gateway_id);

                if (selectedGateway) {
                    this.refreshThingsList(selectedGateway);
                }
            });
    }

    onIntervalChange() {
        this.startAutoRefresh();
    }

    ngOnDestroy() {
        this.autoTableRefresher.unsubscribe();
    }

    ngAfterViewInit() {
        this.dataSourceGateways.paginator = this.paginator;
        this.dataSourceGateways.sort = this.sort;
    }

    gatewayRowClicked(gateway: GatewayVO) {
        if (this.selectedTabIndex === 0) this.selectedTabIndex = 1;

        this.refreshThingsList(gateway);
    }

    openConsoleClick(index: number, event: Event) {
        event.stopPropagation();

        this.intercomService.selectedGateway = this.dataSourceGateways.data[index];
        this.refreshThingsList(this.intercomService.selectedGateway);
        this.selectedTabIndex = 2;
    }

    removeThingClick(thing: ThingVO, event: Event) {
        event.stopPropagation();

        console.log("DING " + JSON.stringify(thing));
        this.thingsService.deleteThing(thing.id).subscribe(() => {
            this.refreshThingsList(this.intercomService.selectedGateway);
        });
    }

    restartClick(gateway: GatewayVO, event: Event) {
        event.stopPropagation();
        this.rpcMqttService.sendRpcReboot(gateway.gateway_id);

        gateway.status!.mqtt!.connected = false;
        this.refreshThingsList(gateway);
    }

    winkClick(gateway: GatewayVO, event: Event) {
        event.stopPropagation();
        this.rpcMqttService.sendRpcWink(gateway.gateway_id);
    }

    toggleEnabledClick(gateway: GatewayVO, event: Event, enable: boolean) {
        event.stopPropagation();

        const updateData: GatewayUpdateVO = { enabled: enable };

        this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
            gateway.enabled = returnData.enabled;

            console.log("DINGDONG: " + JSON.stringify(returnData));
            this.refreshThingsList(gateway);
        });
    }

    isRowSelected(rowData: GatewayVO) {
        if (!this.intercomService.selectedGateway) return false;

        if (this.intercomService.selectedGateway.created !== rowData.created) return false;
        if (this.intercomService.selectedGateway.updated !== rowData.updated) return false;
        if (this.intercomService.selectedGateway.name !== rowData.name) return false;
        if (this.intercomService.selectedGateway.gateway_id !== rowData.gateway_id) return false;
        if (this.intercomService.selectedGateway.status?.mqtt?.connected !== rowData.status?.mqtt?.connected) return false;
        if (this.intercomService.selectedGateway.status?.mqtt?.timestamp !== rowData.status?.mqtt?.timestamp) return false;

        return true;
    }

    sendRPC() {
        if (this.intercomService.selectedGateway) {
            const rpcCommand: RpcCommandVO = JSON.parse(this.jsonCode);

            this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway.gateway_id, rpcCommand);
            this.changeDetector.detectChanges();
        }
    }

    cellClickThings(thing: ThingVO) {
        this.intercomService.selectedThing = thing;
    }

    closeMeasurementView() {
        this.intercomService.selectedThing = undefined;
    }

    openCreateThingDialog() {
        this.dialogService
            .openCreateThingDialog()
            ?.afterClosed()
            .subscribe((data) => {
                if (data != undefined) {
                    this.thingsService.createThing(data).subscribe(() => {
                        this.refreshThingsList(this.intercomService.selectedGateway);
                    });
                }
            });
    }

    refreshThingsList(satellite: GatewayVO | undefined) {
        this.intercomService.selectedGateway = satellite;

        //do nothing if there is no satellite selected
        if (!satellite) return;

        this.thingsService.findThingsByGatewayId(satellite.gateway_id).subscribe((data) => {
            this.dataSourceThings.data = data.content;
            this.intercomService.thingsList = data.content;
        });
    }
}
