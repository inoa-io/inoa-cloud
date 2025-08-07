import { Injectable, OnDestroy } from "@angular/core";
import { Subject } from "rxjs";
import categoriesData from "../../assets/thing-categories/categories.json";
import { ThingTypeCategoryVO } from "@inoa/model";

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

    public getCategory(thingTypeId: ThingTypeCategoryVO): ThingCategory {
        return <ThingCategory>this.categories.get(thingTypeId);
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.complete();
    }
}
