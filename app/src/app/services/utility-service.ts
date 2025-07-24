import { Injectable } from "@angular/core";
import { ThingCategory, ThingCategoryService } from "./thing-category.service";
import { InternalCommunicationService } from "./internal-communication-service";
import { ThingTypeCategoryVO } from "@inoa/model";

@Injectable({
    providedIn: "root"
})
export class UtilityService {
    constructor(private thingCategoryService: ThingCategoryService, private intercomService: InternalCommunicationService) {}

    getThingImage(thingTypeId: ThingTypeCategoryVO | undefined) {
        if (thingTypeId == undefined) return "assets/energy_meter.png";

        const category = this.getThingCategory(thingTypeId);

        return category.image;
    }

    getThingCategory(thingTypeId: string | undefined): ThingCategory {
        if (thingTypeId == undefined) return { key: "unknown", image: "", title: "Some Thing" };

        const thingType = this.getThingType(thingTypeId);
        if (thingType == undefined) return { key: "unknown", image: "", title: "Some Thing" };

        return this.thingCategoryService.getCategory(thingType);
    }

    getThingType(thingTypeId: string | undefined): ThingTypeCategoryVO | undefined {
        if (thingTypeId == undefined) return ThingTypeCategoryVO.None;

        return this.intercomService.thingTypes.find((type) => type.identifier === thingTypeId)?.category;
    }
}
