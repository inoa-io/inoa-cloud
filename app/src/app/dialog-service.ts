import { Injectable } from "@angular/core";
import { InternalCommunicationService } from "./internal-communication-service";
import { ThingCreationDialogComponent, ThingCreationDialogData } from "./thing-creation-dialog/thing-creation-dialog.component";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ThingTypesService } from "@inoa/api";

@Injectable({
    providedIn: "root"
})
export class DialogService {
    constructor(private intercomService: InternalCommunicationService, private thingTypeService: ThingTypesService, public dialog: MatDialog) {
        this.thingTypeService.findThingTypes().subscribe((thingTypeList) => {
            this.intercomService.thingTypes = thingTypeList.content;
        });
    }

    openCreateThingDialog(): MatDialogRef<ThingCreationDialogComponent> | null {
        if (!this.intercomService.selectedGateway) {
            return null;
        }

        const dialogData: ThingCreationDialogData = {
            gateway: this.intercomService.selectedGateway,
            thing: undefined,
            thingTypes: this.intercomService.thingTypes
        };

        const dialogRef = this.dialog.open(ThingCreationDialogComponent, {
            width: "600px",
            data: dialogData
        });

        return dialogRef;
    }
}
