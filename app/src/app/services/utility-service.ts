import { Injectable } from "@angular/core";
import { ThingCategory, ThingCategoryService } from "./thing-category.service";
import { InternalCommunicationService } from "./internal-communication-service";
import { ThingTypeCategoryVO } from "@inoa/model";

@Injectable({
    providedIn: "root"
})
export class UtilityService {
    constructor(private thingCategoryService: ThingCategoryService, private intercomService: InternalCommunicationService) {}

    getThingImageFromThingTypeCategoryVO(thingCategory: ThingTypeCategoryVO | undefined) {
        if (!thingCategory) return "assets/energy_meter.png";

        const category = this.getThingCategoryFromId(thingCategory);

        return category.image;
    }

    getThingImageFromThingCategory(thingCategory: ThingCategory | undefined) {
        if (!thingCategory) return "assets/energy_meter.png";

        return thingCategory.image;
    }

    getThingCategoryFromId(thingTypeId: string | undefined): ThingCategory {
        if (!thingTypeId) return { key: "unknown", image: "", title: "Some Thing" };

        const thingType = this.intercomService.thingTypes.find((type) => type.identifier === thingTypeId)?.category;
        if (!thingType) return { key: "unknown", image: "", title: "Some Thing" };

        return this.thingCategoryService.getCategory(thingType);
    }

    getThingTypeFromId(thingTypeId: string | undefined): ThingTypeCategoryVO | undefined {
        if (!thingTypeId) return ThingTypeCategoryVO.None;

        const type = this.intercomService.thingTypes.find((type) => type.identifier === thingTypeId);

        return type ? type.category : ThingTypeCategoryVO.None;
    }

    getThingTypeName(thingTypeId: string | undefined): string {
        if (thingTypeId == undefined) return "unknown";

        const type = this.intercomService.thingTypes.find((type) => type.identifier === thingTypeId);

        return type ? type.name : "unknown";
    }
}
