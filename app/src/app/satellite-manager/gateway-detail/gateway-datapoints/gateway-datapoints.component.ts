import { animate, state, style, transition, trigger } from "@angular/animations";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { ThingsService } from "@inoa/api";
import { RemoteService } from "@inoa/api";
import { GatewayVO, MeasurandTypeVO, MeasurandVO, ThingCreateVO, ThingUpdateVO, ThingVO } from "@inoa/model";
import { ThingDialogData } from "src/app/dialogs/thing-dialog/thing-dialog.component";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
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
            state("collapsed, void", style({ height: "0px", minHeight: "0px" })),
            state("expanded", style({ height: "*" })),
            transition("expanded <=> collapsed", animate("75ms cubic-bezier(0.4, 0, 0.2, 1)"))
        ])
    ]
})
export class GatewayDatapointsComponent implements OnInit {
    expandedElementDatabase: ThingVO | null = null;
    expandedElementSatellite: ThingVO | null = null;
    displayedColumnsThingsTableDatabase: string[] = ["name", "measurands", "actions"];
    displayedColumnsThingsTableSatellite: string[] = ["name", "actions"];

    dataSourceThingsDatabase = new MatTableDataSource<ThingVO>();
    dataSourceThingsSatellite = new MatTableDataSource<object>();

    constructor(
        public intercomService: InternalCommunicationService,
        public utilityService: UtilityService,
        private thingsService: ThingsService,
        private remotingService: RemoteService,
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

        // TODO: Use: this.remotingService.getThingsFromGateway()
    }

    findIdMatchInDatabase(thingData: any): ThingVO | undefined {
        return this.dataSourceThingsDatabase.data.find((thing) => {
            return thing.id === thingData.id;
        });
    }

    findIdMatchOnSatellite(thingData: ThingVO): any | undefined {
        return this.dataSourceThingsSatellite.data.find((satelliteThing: any) => {
            return thingData.id === satelliteThing.id;
        });
    }

    findMatchInDatabaseByAllProperties(thingData: any): ThingVO | undefined {
        // TODO this is just here so the linter does not complain until the todo is fixed
        if (thingData) return undefined;
        return undefined;
    }

    findMatchOnSatelliteByAllProperties(thingData: ThingVO): any | undefined {
        // TODO this is just here so the linter does not complain until the todo is fixed
        if (thingData) return undefined;
        return undefined;
    }

    editThingDatabaseClick(thing: ThingVO, event: Event) {
        event.stopPropagation();

        this.dialogService.openCreateThingDialog(thing)?.afterClosed().subscribe((data: ThingDialogData) => {
            if (data && data.thing) {
                this.thingsService.updateThing(thing.id, data.thing).subscribe(() => this.refreshThingsListDatabase(this.intercomService.selectedGateway));
            }
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

        // TODO: Use: this.gatewaysService.syncThingsToGateway()
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
                configurations: thing,
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
            configurations: thing,
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
        // TODO: Use: this.gatewaysService.syncThingsToGateway()
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

    toggleDb(row: ThingVO) {
        this.expandedElementDatabase = this.expandedElementDatabase === row ? null : row;
    }

    toggleSat(row: ThingVO) {
        this.expandedElementSatellite = this.expandedElementSatellite === row ? null : row;
    }

    refreshThingsListDatabase(satellite: GatewayVO | undefined) {
        //do nothing if there is no satellite selected
        if (!satellite) return;

        this.intercomService.isLoadingDB = true;

        //get things from database
        this.thingsService.findThingsByGatewayId(satellite.gateway_id).subscribe((data) => {
            this.dataSourceThingsDatabase.data = [];
            this.intercomService.thingsList = [];
            this.dataSourceThingsDatabase.data = data;
            this.intercomService.thingsList = data;
            this.intercomService.isLoadingDB = false;
            this.expandedElementDatabase = null;
        });
    }

    refreshThingsListSatellite(satellite: GatewayVO | undefined) {
        //do nothing if there is no satellite selected
        if (!satellite) return;

        this.intercomService.isLoadingSat = true;
        this.expandedElementSatellite = null;

        // get things from satellite
        // TODO: Use: this.gatewaysService.getThingsFromGateway()
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

            if (thing.configurations && methodName !== "") {
              // TODO: Use: this.gatewaysService.syncThingsToGateway()
            } else {
                resolve();
            }
        });
    }

    syncThings() {
        console.log("Trying to synch to " + this.intercomService.selectedGateway!.gateway_id + " now...");

        this.remotingService.syncThingsToGateway(this.intercomService.selectedGateway!.gateway_id).subscribe((returnData) => {
            console.log(JSON.stringify(returnData, null, 4));
        });
    }

    openCreateThingDialog() {
        this.dialogService.openCreateThingDialog(undefined)?.afterClosed().subscribe((data: ThingDialogData) => {
            if (data && data.thing) {
                this.thingsService.createThing(data.thing).subscribe(() => this.refreshThingsListDatabase(this.intercomService.selectedGateway));
            }
        });
    }

    getEnabledMeasurands(measurands: MeasurandVO[]): MeasurandVO[] {
        if (!measurands) {
            return [];
        }

        const enabledMeasurands = measurands.filter((m) => m.enabled);

        if (enabledMeasurands.length === 0) {
            return [];
        }

        return enabledMeasurands;
    }

    getMeasurandName(measurandType: string): string {
        const thingType = this.intercomService.thingTypes.find((type) => type.measurands?.some((m: MeasurandTypeVO) => m.obis_id === measurandType));
        if (!thingType) return measurandType;

        const measurand = thingType.measurands?.find((m: MeasurandTypeVO) => m.obis_id === measurandType);
        if (!measurand) return measurandType;

        return measurand.name;
    }
}
