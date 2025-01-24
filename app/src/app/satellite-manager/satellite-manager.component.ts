import { Component, OnInit } from "@angular/core";
import { ThingTypesService } from "@inoa/api";
import { InternalCommunicationService } from "../services/internal-communication-service";

@Component({
	selector: "gc-satellite-manager",
	templateUrl: "./satellite-manager.component.html",
	styleUrls: ["./satellite-manager.component.css"]
})
export class SatelliteManagerComponent implements OnInit {
	constructor(public intercomService: InternalCommunicationService, private thingTypesService: ThingTypesService) {
		this.intercomService.selectedGateway = undefined;
	}

	ngOnInit() {
		console.log("Initializing Satellite Manager...");

		// get all thingTypes from the database
		this.thingTypesService.getThingTypes().subscribe((thingTypeList) => {
			this.intercomService.thingTypes = thingTypeList;
		});
	}
}
