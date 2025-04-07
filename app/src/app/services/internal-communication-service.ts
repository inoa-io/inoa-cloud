import { EventEmitter, Injectable } from "@angular/core";
import { GatewayVO, TenantVO, ThingTypeVO, ThingVO } from "@inoa/model";
import { NetworkInterface } from "../measurement-collector/measurement-collector.component";

@Injectable({
	providedIn: "root"
})
export class InternalCommunicationService {
	//paginator parameters
	pageNumber = 0;
	pageSize = 10;
	pageSizes = [10, 25, 50, 100];

	// selected things
	selectedGateway: GatewayVO | undefined;
	selectedGatewayId: string | null = null;
	connectionType: NetworkInterface | undefined;
	selectedThing: ThingVO | undefined;

	// datasets
	gateways: GatewayVO[] = [];
	thingTypes: ThingTypeVO[] = [];
	tenantList: TenantVO[] = [];
	thingsList: ThingVO[] = [];

	// data
	userEmail = "admin@example.org";
	hardwareVersion = 0;

	// flags
	httpDataLoading = false;
	expertMode = false;
	isLoadingDB = false;
	isLoadingSat = false;
	isLoadingSysInfo = false;
	isLoadingConfig = false;

	viewModes = [
		{ name: "Table", active: true, icon: "table", tabIndex: 0 },
		{ name: "Map", active: false, icon: "map", tabIndex: 1 },
	];

	//should be raised after display permissions have been received
	selectedGatewayChangedEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
	raiseSelectedGatewayChangedEvent() {
		this.selectedGatewayChangedEventEmitter.emit();
	}
}
