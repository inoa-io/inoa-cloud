import { Injectable } from "@angular/core";
import { ThingCategory, ThingCategoryService } from "./thing-category.service";

@Injectable({
    providedIn: "root"
})
export class UtilityService {
    constructor(private thingCategoryService: ThingCategoryService) {}

    getThingImage(thingTypeId: string | undefined) {
        if (thingTypeId == undefined) return "assets/energy_meter.png";

        const category = this.getThingCategory(thingTypeId);

        return category.image;
    }

    getThingCategory(thingTypeId: string | undefined): ThingCategory {
        if (thingTypeId == undefined) return { key: "unknown", image: "", title: "Some Thing" };

        return this.thingCategoryService.getCategory(thingTypeId);
    }
}
