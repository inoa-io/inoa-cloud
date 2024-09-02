import { Injectable, OnDestroy } from "@angular/core";
import { Subject } from "rxjs";
import categoriesData from "../assets/thing-categories/categories.json";

export interface ThingCategory {
    key: string;
    image: string;
    title: string;
}

@Injectable({
    providedIn: "root"
})
export class ThingCategoryService implements OnDestroy {
    private destroy$: Subject<boolean> = new Subject<boolean>();
    private categories: Map<string, ThingCategory> = new Map();

    constructor() {
        // load thing categories from imported json file
        categoriesData.forEach((category) => {
            if (category && typeof category === "object" && "key" in category) {
                this.categories.set(category.key, category as ThingCategory);
            }
        });
    }

    public getCategory(thingTypeId: string): ThingCategory {
        let key = "unknown";

        switch (thingTypeId) {
            case "dvmodbusir":
                key = "energy_meter";
                break;
            case "s0":
                key = "sensor";
                break;
            case "shellyplug-s":
                key = "smart_plug";
                break;
            case "shellyplusplugs":
                key = "smart_plug";
                break;
            case "shplg-pm2":
                key = "smart_plug";
                break;
            case "shellyht":
                key = "sensor";
                break;
            case "dvh4013":
                key = "energy_meter";
                break;
            case "mdvh4006":
                key = "energy_meter";
                break;

            default:
                console.log("Unknown thing type: " + thingTypeId);
                break;
        }

        return <ThingCategory>this.categories.get(key);
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.complete();
    }
}
