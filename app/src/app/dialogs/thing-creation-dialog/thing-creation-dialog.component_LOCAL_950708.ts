import { Component, Inject, OnDestroy } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { GatewayVO, ThingTypeVO, ThingCreateVO, MeasurandVO } from "@inoa/api";
import { FormBuilder, FormGroup, Validators, FormArray } from "@angular/forms";
import { Subject } from "rxjs";
import { ThingCategoryService, ThingCategory } from "../../services/thing-category.service";
import { UtilityService } from "../../services/utility-service";
import { InternalCommunicationService } from "../../services/internal-communication-service";

@Component({
    selector: "gc-thing-creation-dialog",
    templateUrl: "./thing-creation-dialog.component.html",
    styleUrls: ["./thing-creation-dialog.component.css"]
    selector: "gc-thing-creation-dialog",
    templateUrl: "./thing-creation-dialog.component.html",
    styleUrls: ["./thing-creation-dialog.component.css"]
})
export class ThingCreationDialogComponent implements OnDestroy {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private destroy$: Subject<any> = new Subject<any>();
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private destroy$: Subject<any> = new Subject<any>();

    public thingTypes: ThingTypeVO[] = [];
    public thingTypes: ThingTypeVO[] = [];

    form!: FormGroup;
    selectedThingType: ThingTypeVO | undefined;
    category!: ThingCategory;

    // extract from json_schema for display in dialog
    thingTitle = "";
    thingDescription: string | undefined = "";

    constructor(
        public dialogRef: MatDialogRef<ThingCreationDialogComponent>,
        private thingCategoryService: ThingCategoryService,
        public intercomService: InternalCommunicationService,
        public utilityService: UtilityService,
        private fb: FormBuilder,
        @Inject(MAT_DIALOG_DATA) public data: ThingCreationDialogData
    ) {
        this.form = this.fb.group({
            name: ["", Validators.required],
            configurations: this.fb.group({}),
            measurands: this.fb.array([])
        });

        // Ensure that thingTypes is defined before accessing its elements
        if (this.intercomService.thingTypes && this.intercomService.thingTypes.length > 0) {
            // Load the first type into the form
            this.loadThingType(this.intercomService.thingTypes[0]);
            this.selectedThingType = this.intercomService.thingTypes[0];
        } else {
            console.error("ThingTypes data is undefined or null");
        }
    }

    loadThingType(type: ThingTypeVO) {
        // Extract title and description to display them separately (these are always present, so that's ok)
        if (type) {
            this.thingTitle = type.name;
            this.thingDescription = type.description;
        }

        this.selectedThingType = type;
        this.category = type.category ? this.thingCategoryService.getCategory(type.identifier) : { key: "error", image: "", title: "none" };

        const configurationsGroup = this.fb.group({});
        if (type.configurations) {
            type.configurations.forEach((config) => {
                const validators = [];
                if (config.validation_regex) {
                    validators.push(Validators.pattern(config.validation_regex));
                }
                configurationsGroup.addControl(config.name, this.fb.control("", validators));
            });
        }

        const measurandsArray = this.fb.array(type.measurands ? type.measurands.map(() => this.fb.control(true)) : []);

        this.form = this.fb.group({
            name: [type.name, Validators.required],
            configurations: configurationsGroup,
            measurands: measurandsArray
        });

        const thing: ThingCreateVO = {
            name: type.name,
            gateway_id: this.data.gateway.gateway_id,
            thing_type_id: type.identifier,
            configurations: {},
            measurands: []
        };
        const thing: ThingCreateVO = {
            name: type.name,
            gateway_id: this.data.gateway.gateway_id,
            thing_type_id: type.identifier
        };

        this.data.thing = thing;
    }

    get measurands(): FormArray {
        return this.form.get("measurands") as FormArray;
    }

    onCancelClick(): void {
        this.dialogRef.close();
    }

    onCreateClick() {
        if (this.form.valid && this.selectedThingType && this.data.thing) {
            this.data.thing.name = this.form.get("name")?.value;

            const configurations: { [key: string]: string } = {};
            if (this.selectedThingType.configurations) {
                this.selectedThingType.configurations.forEach((config) => {
                    const value = this.form.get("configurations")?.get(config.name)?.value;
                    if (value) {
                        configurations[config.name] = value;
                    }
                });
            }
            this.data.thing.configurations = configurations;

            const measurands: MeasurandVO[] = [];
            if (this.selectedThingType.measurands) {
                this.measurands.controls.forEach((control, index) => {
                    if (control.value && this.selectedThingType && this.selectedThingType.measurands) {
                        const measurandType = this.selectedThingType.measurands[index];
                        measurands.push({
                            measurand_type: measurandType.obis_id,
                            enabled: true,
                            interval: 30000,
                            timeout: 1000
                        });
                    }
                });
            }
            this.data.thing.measurands = measurands;
        }
    }

    public ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.complete();
    }
}

export interface ThingCreationDialogData {
    gateway: GatewayVO;
    thing: ThingCreateVO | undefined;
}
