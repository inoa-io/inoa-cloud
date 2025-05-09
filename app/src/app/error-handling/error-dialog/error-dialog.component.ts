import { Component, Inject } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
	selector: "gc-error-dialog",
	templateUrl: "./error-dialog.component.html",
	styleUrls: ["./error-dialog.component.css"]
})
export class ErrorDialogComponent {
	constructor(
		@Inject(MAT_DIALOG_DATA)
		public data: { message: string; status?: number; isInfo?: boolean }
	) {}
}
