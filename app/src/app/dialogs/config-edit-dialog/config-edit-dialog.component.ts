import { Component, HostListener, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { ConfigData } from "src/app/satellite-manager/gateway-detail/gateway-configuration/gateway-configuration.component";

export interface ConfigEditDialogData {
    configNames: string[];
    configData: ConfigData;
}

@Component({
    selector: "gc-config-edit-dialog",
    templateUrl: "./config-edit-dialog.component.html",
    styleUrls: ["./config-edit-dialog.component.css"]
})
export class ConfigEditDialogComponent {
	@HostListener("document:keydown", ["$event"])
		
	// save on enter press
    handleKeyboardEvent(event: KeyboardEvent) {
        if (event.key === "Enter") {
			this.save();
        }
    }

    configLabels: Map<string, string> = new Map([
        ["wifiSid", "Wifi SSID"],
        ["wifiPassword", "Wifi Password"],
        ["ntpHost", "NTP Host"],
        ["registryUrl", "Registry Endpoint"],
        ["mqttUrl", "MQTT Endpoint"],
        ["mqttPort", "MQTT Port"],
        ["hawkbitHost", "Update Server"],
		["hawkbitBasePath", "Hawkbit Base Path"],
		["meteringCycleDuration", "Metering Interval (ms)"]
    ]);

	constructor(public dialogRef: MatDialogRef<ConfigEditDialogData>, @Inject(MAT_DIALOG_DATA) public data: ConfigEditDialogData) { }
	
	shouldWarn() {
		if (this.data.configNames.includes("wifiPassword")) return true;
		if (this.data.configNames.includes("wifiSid")) return true;
		if (this.data.configNames.includes("mqttUrl")) return true;
		if (this.data.configNames.includes("mqttPort")) return true;
		
		return false;
	}

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        this.dialogRef.close(this.data);
    }
}
