import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { GatewaysService, GatewayUpdateVO, GatewayVO, RpcCommandVO, ThingCreateVO, ThingsService, ThingTypesService, ThingUpdateVO, ThingVO } from "@inoa/api";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { RpcMqttService } from "../services/rpc-mqtt-service";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { interval, Subscription, switchMap } from "rxjs";
import { UtilityService } from "../services/utility-service";
import { SelectionModel } from "@angular/cdk/collections";
import { ConfigEditDialogData } from "../dialogs/config-edit-dialog/config-edit-dialog.component";
import { RpcHistoryService } from "../services/rpc-history-service";
import { DialogService } from "../services/dialog-service";

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

    expandedElementDatabase: ThingVO | null = null;
    expandedElementSatellite: ThingVO | null = null;
    displayedColumnsRpcHistory: string[] = ["method", "id", "status"];
    displayedColumnsGatewayTable: string[] = ["select", "gateway_id", "name", "enabled", "status", "actions"];
    displayedColumnsGatewayTableMini: string[] = ["gateway_id"];
    displayedColumnsThingsTableDatabase: string[] = ["name", "thing_config_id", "enabled", "match", "actions", "sync"];
    displayedColumnsThingsTableSatellite: string[] = ["sync", "thing_config_id", "enabled", "match", "actions"];

    dataSourceGateways = new MatTableDataSource<GatewayVO>();
    selectionGateways = new SelectionModel<GatewayVO>(true, []);
    dataSourceThingsDatabase = new MatTableDataSource<ThingVO>();
    dataSourceThingsSatellite = new MatTableDataSource<object>();

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
        private changeDetector: ChangeDetectorRef,
        private dialogService: DialogService,
        private thingTypesService: ThingTypesService,
        public utilityService: UtilityService
    ) {
        this.intercomService.selectedGateway = undefined;
    }

    ngOnInit() {
        console.log("Initializing Satellite Manager...");
        this.startAutoRefresh();

        this.thingTypesService.findThingTypes().subscribe((thingTypeList) => {
            this.intercomService.thingTypes = thingTypeList.content;
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

                // If the selected gateway is not in the list, set selectedGateway to undefined and refresh the things list
                if (!selectedGateway) {
                    this.intercomService.selectedGateway = undefined;
                    this.refreshThingsListDatabase(selectedGateway);
                    this.refreshThingsListSatellite(selectedGateway);
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

        this.refreshThingsListDatabase(gateway);
        this.refreshThingsListSatellite(gateway);
    }

    openConsoleClick(index: number, event: Event) {
        event.stopPropagation();

        this.intercomService.selectedGateway = this.dataSourceGateways.data[index];
        this.refreshThingsListDatabase(this.intercomService.selectedGateway);
        this.refreshThingsListSatellite(this.intercomService.selectedGateway);
        this.selectedTabIndex = 2;
    }

    removeThingDatabaseClick(thing: ThingVO, event: Event) {
        event.stopPropagation();

        this.thingsService.deleteThing(thing.id).subscribe(() => {
            this.refreshThingsListDatabase(this.intercomService.selectedGateway);
        });
    }

    removeThingSatelliteClick(thing: any, event: Event) {
        event.stopPropagation();

        const deleteParams = {
            id: thing.id
        };

        this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: "dp.delete", params: deleteParams }).subscribe();
        this.refreshThingsListSatellite(this.intercomService.selectedGateway);
    }

    syncThingSatelliteClick(thing: any, event: Event, updateLists: boolean, callback?: () => void) {
        event.stopPropagation();

        const match = this.findIdMatchInDatabase(thing);

        // if there is no id match in the database, create a new thing
        if (!match) {
            const syncThing: ThingCreateVO = {
                name: thing.name,
                thing_type_id: thing.id.split(":")[1],
                config: thing,
                gateway_id: this.intercomService.selectedGateway!.gateway_id
            };

            this.thingsService.createThing(syncThing).subscribe(() => {
                if (updateLists) {
                    this.refreshThingsListDatabase(this.intercomService.selectedGateway);
                    this.refreshThingsListSatellite(this.intercomService.selectedGateway);
                }
                if (callback) callback();
            });

            return;
        }

        // if there is an id match in the database, update the thing
        const syncThing: ThingUpdateVO = {
            name: thing.name,
            thing_type_id: thing.id.split(":")[1],
            config: thing,
            gateway_id: this.intercomService.selectedGateway!.gateway_id
        };

        this.thingsService.updateThing(match.id, syncThing).subscribe(() => {
            if (updateLists) {
                this.refreshThingsListDatabase(this.intercomService.selectedGateway);
                this.refreshThingsListSatellite(this.intercomService.selectedGateway);
            }
            if (callback) callback();
        });
    }

    syncAllThingsSatelliteClick(event: Event) {
        event.stopPropagation();

        // Create an array of promises (great band name)
        const syncPromises = this.dataSourceThingsSatellite.data.map((thing: any) => {
            return new Promise<void>((resolve) => {
                this.syncThingSatelliteClick(thing, event, false, resolve);
            });
        });

        // Wait for all sync operations to complete
        Promise.all(syncPromises).then(() => {
            this.refreshThingsListDatabase(this.intercomService.selectedGateway);
            this.refreshThingsListSatellite(this.intercomService.selectedGateway);
        });
    }

    syncThingDatabaseClick(thing: ThingVO, event: Event, updateLists: boolean, callback?: () => void) {
        event.stopPropagation();

        const match = this.findIdMatchOnSatellite(thing);

        // if there is no id match on the satellite, create a new thing
        if (!match) {
            this.addThingToSatellite(thing).then(() => {
                if (updateLists) {
                    this.refreshThingsListDatabase(this.intercomService.selectedGateway);
                    this.refreshThingsListSatellite(this.intercomService.selectedGateway);
                }
                if (callback) callback();
            });
            return;
        }

        // if there is an id match on the satellite, update the thing (updating for now means deleting and recreating)
        this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: "dp.delete", params: { id: thing.config!["id"] } }).subscribe(() => {
            this.addThingToSatellite(thing).then(() => {
                if (updateLists) {
                    this.refreshThingsListDatabase(this.intercomService.selectedGateway);
                    this.refreshThingsListSatellite(this.intercomService.selectedGateway);
                }
                if (callback) callback();
            });
        });
    }

    syncAllThingsDatabaseClick(event: Event) {
        event.stopPropagation();

        // Create an array of Promises (great band name)
        const syncPromises = this.dataSourceThingsDatabase.data.map((thing: ThingVO) => {
            return new Promise<void>((resolve) => {
                this.syncThingDatabaseClick(thing, event, false, resolve);
            });
        });

        // Wait for all sync operations to complete
        Promise.all(syncPromises).then(() => {
            // refresh the things lists
            this.refreshThingsListDatabase(this.intercomService.selectedGateway);
            this.refreshThingsListSatellite(this.intercomService.selectedGateway);
        });
    }

    restartClick(gateway: GatewayVO, event: Event) {
        event.stopPropagation();
        this.rpcMqttService.sendRpcReboot(gateway.gateway_id);

        gateway.status!.mqtt!.connected = false;
        this.refreshThingsListDatabase(gateway);
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

            this.refreshThingsListDatabase(gateway);
        });
    }

    editConfigClick(gateway: GatewayVO, event: Event) {
        this.dialogService
            .openConfigEditDialog()
            ?.afterClosed()
            .subscribe((data) => {
                // write the config data to the gateway
                this.rpcMqttService.sendRpcCommand(gateway.gateway_id, { method: "config.write", params: this.createRpcConfigRequest(data) }).subscribe(() => {
                    // reboot the gateway after config data has been written
                    this.restartClick(gateway, event);
                });
            });
    }

    renameSatelliteClick(gateway: GatewayVO, event: Event) {
        event.stopPropagation();

        this.dialogService
            .openRenameSatelliteDialog(gateway.name ? gateway.name : "")
            ?.afterClosed()
            .subscribe((data) => {
                this.updateSatelliteName(gateway, data.name);
            });
    }

    updateSatelliteName(gateway: GatewayVO, name: string) {
        const updateData: GatewayUpdateVO = {
            name: name
        };

        this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
            gateway.name = returnData.name;

            this.startAutoRefresh();
        });
    }

    createRpcConfigRequest(config: ConfigEditDialogData): object {
        const jsonData = {
            wifi: {
                sid: config.wifiSid,
                password: config.wifiPassword
            },
            eth: {
                enabled: config.ethEnabled
            },
            buffering: {
                enabled: config.bufferingEnabled
            },
            telnet: {
                enabled: config.telnetEnabled
            },
            metering: {
                cycleDuration: config.meteringCycleDuration
            },
            logging: {
                mqttLogging: config.loggingMqttLogging,
                mqttConsole: config.loggingMqttConsole
            },
            ntp: {
                host: config.ntpHost
            },
            registry: {
                url: config.registryUrl
            },
            mqtt: {
                url: config.mqttUrl,
                port: config.mqttPort
            },
            hawkbit: {
                host: config.hawkbitHost,
                basepath: config.hawkbitBasePath
            },
            interfaces: {
                uart1: {
                    baud: config.interfacesUart1Baud,
                    dataBitMode: config.interfacesUart1DataBitMode,
                    parityMode: config.interfacesUart1ParityMode,
                    stopBitMode: config.interfacesUart1StopBitMode
                },
                uart2: {
                    baud: config.interfacesUart2Baud,
                    dataBitMode: config.interfacesUart2DataBitMode,
                    parityMode: config.interfacesUart2ParityMode,
                    stopBitMode: config.interfacesUart2StopBitMode
                }
            }
        };

        return jsonData;
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

            this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway.gateway_id, rpcCommand).subscribe();
            this.changeDetector.detectChanges();
        }
    }

    cellClickThings(thing: ThingVO) {
        this.intercomService.selectedThing = thing;
    }

    openCreateThingDialog() {
        this.dialogService
            .openCreateThingDialog()
            ?.afterClosed()
            .subscribe((data) => {
                if (data) {
                    this.thingsService.createThing(data).subscribe((returnData) => {
                        this.refreshThingsListDatabase(this.intercomService.selectedGateway);

                        // add the thing to the satellite too
                        this.addThingToSatellite(returnData).then(() => {
                            this.refreshThingsListSatellite(this.intercomService.selectedGateway);
                        });
                    });
                }
            });
    }

    addThingToSatellite(thing: ThingVO): Promise<void> {
        return new Promise<void>((resolve) => {
            let methodName = "";

            switch (thing.thing_type_id) {
                case "s0":
                    methodName = "dp.add.s0";
                    break;
                case "shellyplug-s":
                    methodName = "dp.add.http.get";
                    break;
                case "shellyplusplugs":
                    methodName = "dp.add.http.get";
                    break;
            }

            if (thing.config && methodName !== "") {
                this.rpcMqttService.sendRpcCommand(this.intercomService.selectedGateway!.gateway_id, { method: methodName, params: thing.config }).subscribe(() => {
                    resolve();
                });
            } else {
                resolve();
            }
        });
    }

    refreshThingsListDatabase(satellite: GatewayVO | undefined) {
        this.intercomService.selectedGateway = satellite;

        //do nothing if there is no satellite selected
        if (!satellite) return;

        //get things from database
        this.thingsService.findThingsByGatewayId(satellite.gateway_id).subscribe((data) => {
            this.dataSourceThingsDatabase.data = data.content;
            this.intercomService.thingsList = data.content;
        });
    }

    refreshThingsListSatellite(satellite: GatewayVO | undefined) {
        this.intercomService.selectedGateway = satellite;

        //do nothing if there is no satellite selected
        if (!satellite) return;

        //get things from satellite
        this.rpcMqttService.sendRpcCommand(satellite.gateway_id, { method: "dp.list" }).subscribe((data) => {
            this.storeSatelliteThingsData(satellite.gateway_id, data.response);
        });
    }

    storeSatelliteThingsData(gateway_id: string, response: string) {
        const parsedResponse = JSON.parse(response);

        // clear old data
        this.dataSourceThingsSatellite.data = [];

        parsedResponse.forEach((urn: string) => {
            this.rpcMqttService.sendRpcCommand(gateway_id, { method: "dp.get", params: { id: urn } }).subscribe((data) => {
                // parse the response data
                const thingData = JSON.parse(data.response);

                // remove the 'type' property from thingData
                delete thingData.type;

                // add the thingData to the data source
                this.dataSourceThingsSatellite.data = [...this.dataSourceThingsSatellite.data, thingData];
            });
        });
    }

    findIdMatchInDatabase(thingData: any): ThingVO | undefined {
        return this.dataSourceThingsDatabase.data.find((thing) => {
            return thing.config?.["id"] === thingData.id;
        });
    }

    findIdMatchOnSatellite(thingData: ThingVO): any | undefined {
        return this.dataSourceThingsSatellite.data.find((satelliteThing: any) => {
            return thingData.config?.["id"] === satelliteThing.id;
        });
    }

    findMatchInDatabaseByAllProperties(thingData: any): ThingVO | undefined {
        return this.dataSourceThingsDatabase.data.find((thing) => {
            // Compare every property of thing.config and thingData
            return Object.keys(thing.config!).every((key) => {
                // Check if the property exists in both objects
                if (Object.prototype.hasOwnProperty.call(thing.config!, key) && Object.prototype.hasOwnProperty.call(thingData, key)) {
                    // Compare the values
                    return JSON.stringify(thing.config![key]) === JSON.stringify(thingData[key]);
                }

                return false;
            });
        });
    }

    findMatchOnSatelliteByAllProperties(thingData: ThingVO): any | undefined {
        return this.dataSourceThingsSatellite.data.find((satelliteThing: any) => {
            // Compare every property of thingData.config and satelliteThing
            return Object.keys(thingData.config!).every((key) => {
                // Check if the property exists in both config objects
                if (Object.prototype.hasOwnProperty.call(thingData.config!, key) && Object.prototype.hasOwnProperty.call(satelliteThing, key)) {
                    // Compare the values
                    return JSON.stringify(thingData.config![key]) === JSON.stringify(satelliteThing[key]);
                }

                return false;
            });
        });
    }
}
