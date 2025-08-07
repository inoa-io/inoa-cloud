import { Injectable } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { ThingDialogComponent, ThingDialogData } from "../dialogs/thing-dialog/thing-dialog.component";
import { MatDialog, MatDialogConfig, MatDialogRef } from "@angular/material/dialog";
import { RenameSatelliteDialogComponent } from "../dialogs/rename-satellite-dialog/rename-satellite-dialog.component";
import { ConfigEditDialogComponent, ConfigEditDialogData } from "../dialogs/config-edit-dialog/config-edit-dialog.component";
import { LocationEditorDialogComponent } from "../dialogs/location-editor-dialog/location-editor-dialog.component";
import { GatewayLocationDataVO } from "src/openapi/model/gatewayLocationData";
import { ThingVO } from "@inoa/model";

@Injectable({
    providedIn: "root"
})
export class DialogService {
    constructor(private intercomService: InternalCommunicationService, public dialog: MatDialog) {}

    openCreateThingDialog(thing: ThingVO | undefined): MatDialogRef<ThingDialogComponent> | null {
        if (!this.intercomService.selectedGateway) return null;

        const dialogConfig = new MatDialogConfig();

        dialogConfig.width = "700px";
        dialogConfig.data = {
            gateway: this.intercomService.selectedGateway,
            thing: thing
        } as ThingDialogData;

        const dialogRef = this.dialog.open(ThingDialogComponent, dialogConfig);

        return dialogRef;
    }

    openRenameSatelliteDialog(oldName: string): MatDialogRef<RenameSatelliteDialogComponent> | null {
        const dialogRef = this.dialog.open(RenameSatelliteDialogComponent, {
            data: { name: oldName }
        });

        return dialogRef;
    }

    openConfigEditDialog(data: ConfigEditDialogData): MatDialogRef<ConfigEditDialogComponent> | null {
        const dialogRef = this.dialog.open(ConfigEditDialogComponent, {
            data: data
        });

        return dialogRef;
    }

    openLocationEditorDialog(oldLocation: GatewayLocationDataVO | null): MatDialogRef<LocationEditorDialogComponent> | null {
        const dialogRef = this.dialog.open(LocationEditorDialogComponent, {
            height: "100%",
            width: "100%",
            maxHeight: "100%",
            maxWidth: "100%",
            data: { location: oldLocation }
        });

        return dialogRef;
    }
}
