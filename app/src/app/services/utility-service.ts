import { Injectable } from "@angular/core";
import { ThingCategory, ThingCategoryService } from "./thing-category.service";
import { InternalCommunicationService } from "./internal-communication-service";

@Injectable({
	providedIn: "root"
})
export class UtilityService {
	constructor(private thingCategoryService: ThingCategoryService, private intercomService: InternalCommunicationService) {}

	getThingImage(thingTypeId: string | undefined) {
		if (thingTypeId == undefined) return "assets/energy_meter.png";

		const category = this.getThingCategory(thingTypeId);

		return category.image;
	}

	getThingCategory(thingTypeId: string | undefined): ThingCategory {
		if (thingTypeId == undefined) return { key: "unknown", image: "", title: "Some Thing" };

		return this.thingCategoryService.getCategory(thingTypeId);
	}

	getThingType(thingTypeId: string | undefined): string {
		if (thingTypeId == undefined) return "unknown";

		return this.intercomService.thingTypes.find((type) => type.id === thingTypeId)?.name || "unknown";
	}
}
