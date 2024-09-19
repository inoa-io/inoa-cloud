import { Injectable } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { ThingCreationDialogComponent, ThingCreationDialogData } from "../dialogs/thing-creation-dialog/thing-creation-dialog.component";
import { ConfigEditDialogComponent, ConfigEditDialogData } from "../dialogs/config-edit-dialog/config-edit-dialog.component";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ThingTypesService } from "@inoa/api";
import { RenameSatelliteDialogComponent } from "../dialogs/rename-satellite-dialog/rename-satellite-dialog.component";

@Injectable({
    providedIn: "root"
})
export class DialogService {
    constructor(private intercomService: InternalCommunicationService, private thingTypesService: ThingTypesService, public dialog: MatDialog) {}

    openCreateThingDialog(): MatDialogRef<ThingCreationDialogComponent> | null {
        if (!this.intercomService.selectedGateway) {
            return null;
        }

        const thingCreationDialogData: ThingCreationDialogData = {
            gateway: this.intercomService.selectedGateway,
            thing: undefined
        };

        const dialogRef = this.dialog.open(ThingCreationDialogComponent, {
            width: "600px",
            data: thingCreationDialogData
        });

        return dialogRef;
    }

    openConfigEditDialog(): MatDialogRef<ConfigEditDialogComponent> | null {
        if (!this.intercomService.selectedGateway) {
            return null;
        }

        const configEditDialogData: ConfigEditDialogData = {
            // wifi config
            wifiSid: "",
            wifiPassword: "",

            // eth config
            ethEnabled: false,

            // buffering config
            bufferingEnabled: false,

            // telnet config
            telnetEnabled: false,

            // ntp config
            ntpHost: "",

            // registry config
            registryUrl: "",

            // mqtt config
            mqttUrl: "",
            mqttPort: 1883,

            // hawkbit config
            hawkbitHost: "",
            hawkbitBasePath: "",

            // interfaces config 1
            interfacesUart1Baud: 9600,
            interfacesUart1DataBitMode: 3,
            interfacesUart1ParityMode: 0,
            interfacesUart1StopBitMode: 1,

            // interfaces config 2
            interfacesUart2Baud: 9600,
            interfacesUart2DataBitMode: 3,
            interfacesUart2ParityMode: 0,
            interfacesUart2StopBitMode: 1,

            // metering config
            meteringCycleDuration: 60000,

            // logging config
            loggingMqttLogging: false,
            loggingMqttConsole: false
        };

        const dialogRef = this.dialog.open(ConfigEditDialogComponent, {
            data: configEditDialogData
        });

        return dialogRef;
    }

    openRenameSatelliteDialog(oldName: string): MatDialogRef<RenameSatelliteDialogComponent> | null {
        const dialogRef = this.dialog.open(RenameSatelliteDialogComponent, {
            data: { name: oldName }
        });

        return dialogRef;
    }
}
