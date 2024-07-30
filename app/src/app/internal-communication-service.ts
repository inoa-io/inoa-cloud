import { Injectable } from "@angular/core";
import { GatewayVO, ThingTypeVO, ThingVO } from "@inoa/model";
import { NetworkInterface } from "./measurement-collector/measurement-collector.component";

@Injectable({
	providedIn: "root"
})
export class InternalCommunicationService
{
	//paginator parameters
	pageNumber = 0;
	pageSize = 10;
	pageSizes = [10, 25, 50, 100];

	// shows what routing page is selected in the sidebar
	pageSelect = "";

	selectedGateway: GatewayVO | undefined;
	connectionType: NetworkInterface | undefined;
	selectedThing: ThingVO | undefined;

	gateways: GatewayVO[] = [];
	thingTypes: ThingTypeVO[] = [];
	thingsList: ThingVO[] = [];
}
