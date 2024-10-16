import { Injectable } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { ThingCreationDialogComponent, ThingCreationDialogData } from "../dialogs/thing-creation-dialog/thing-creation-dialog.component";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ThingTypesService } from "@inoa/api";
import { RenameSatelliteDialogComponent } from "../dialogs/rename-satellite-dialog/rename-satellite-dialog.component";
import { ConfigEditDialogComponent, ConfigEditDialogData } from "../dialogs/config-edit-dialog/config-edit-dialog.component";

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
}
