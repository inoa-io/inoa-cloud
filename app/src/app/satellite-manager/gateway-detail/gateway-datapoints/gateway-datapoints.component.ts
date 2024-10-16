import { animate, state, style, transition, trigger } from "@angular/animations";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { ThingsService } from "@inoa/api";
import { GatewayVO, ThingCreateVO, ThingUpdateVO, ThingVO } from "@inoa/model";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RpcMqttService } from "src/app/services/rpc-mqtt-service";
import { UtilityService } from "src/app/services/utility-service";

export interface ConfigData {
    [key: string]: any;
}

@Component({
    selector: "gc-gateway-datapoints",
    templateUrl: "./gateway-datapoints.component.html",
    styleUrl: "./gateway-datapoints.component.css",
    animations: [
        trigger("detailExpand", [
            state("collapsed", style({ height: "0px", minHeight: "0" })),
            state("expanded", style({ height: "*" })),
            transition("expanded <=> collapsed", animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"))
        ])
    ]
})
export class GatewayDatapointsComponent implements OnInit {
    expandedElementDatabase: ThingVO | null = null;
    expandedElementSatellite: ThingVO | null = null;
    displayedColumnsThingsTableDatabase: string[] = ["name", "category", "thing_type", "enabled", "match", "actions", "sync"];
    displayedColumnsThingsTableSatellite: string[] = ["sync", "name", "enabled", "match", "actions"];

    dataSourceThingsDatabase = new MatTableDataSource<ThingVO>();
    dataSourceThingsSatellite = new MatTableDataSource<object>();

    constructor(
        private rpcMqttService: RpcMqttService,
        public intercomService: InternalCommunicationService,
        public utilityService: UtilityService,
        private thingsService: ThingsService,
        private dialogService: DialogService
    ) {}

    ngOnInit(): void {
        //sub to selected gateway changes
        this.intercomService.selectedGatewayChangedEventEmitter.subscribe(() => {
            this.refreshThingsListDatabase(this.intercomService.selectedGateway);
            this.refreshThingsListSatellite(this.intercomService.selectedGateway);
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

    cellClickThings(thing: ThingVO) {
        this.intercomService.selectedThing = thing;
    }

    refreshThingsListDatabase(satellite: GatewayVO | undefined) {
        //do nothing if there is no satellite selected
        if (!satellite) return;

        this.dataSourceThingsDatabase.data = [];
        this.intercomService.thingsList = [];
        this.intercomService.isLoadingDB = true;

        //get things from database
        this.thingsService.findThingsByGatewayId(satellite.gateway_id).subscribe((data) => {
            this.dataSourceThingsDatabase.data = data.content;
            this.intercomService.thingsList = data.content;
            this.intercomService.isLoadingDB = false;
        });
    }

    refreshThingsListSatellite(satellite: GatewayVO | undefined) {
        //do nothing if there is no satellite selected
        if (!satellite) return;

        this.dataSourceThingsSatellite.data = [];
        this.intercomService.isLoadingSat = true;

        //get things from satellite
        this.rpcMqttService.sendRpcCommand(satellite.gateway_id, { method: "dp.list" }).subscribe((data) => {
            this.storeSatelliteThingsData(satellite.gateway_id, data.response);
            this.intercomService.isLoadingSat = false;
        });
    }

    addThingToSatellite(thing: ThingVO): Promise<void> {
        return new Promise<void>((resolve) => {
            let methodName = "";

            // TODO: make this more generic (should be defined in schemas for example)
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
}
