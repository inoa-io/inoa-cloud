import { Component, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

export interface RenameSatelliteDialogData {
	name: string;
}

@Component({
	selector: "gc-rename-satellite-dialog",
	templateUrl: "./rename-satellite-dialog.component.html",
	styleUrls: ["./rename-satellite-dialog.component.css"]
})
export class RenameSatelliteDialogComponent {
	constructor(public dialogRef: MatDialogRef<RenameSatelliteDialogData>, @Inject(MAT_DIALOG_DATA) public data: RenameSatelliteDialogData) {}

	close(): void {
		this.dialogRef.close();
	}

	save(): void {
		this.dialogRef.close(this.data);
	}
}
