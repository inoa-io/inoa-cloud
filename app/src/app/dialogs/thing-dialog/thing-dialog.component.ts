import { Component, Inject, OnDestroy } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { GatewayVO, ThingTypeVO, ThingCreateVO, MeasurandVO, ThingVO } from "@inoa/api";
import { FormBuilder, FormGroup, Validators, FormArray } from "@angular/forms";
import { Subject } from "rxjs";
import { ThingCategoryService, ThingCategory } from "../../services/thing-category.service";
import { UtilityService } from "../../services/utility-service";
import { InternalCommunicationService } from "../../services/internal-communication-service";

@Component({
    selector: "gc-thing-dialog",
    templateUrl: "./thing-dialog.component.html",
    styleUrls: ["./thing-dialog.component.css"]
})
export class ThingDialogComponent implements OnDestroy {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private destroy$: Subject<any> = new Subject<any>();

    public thingTypes: ThingTypeVO[] = [];

    form!: FormGroup;
    selectedThingType: ThingTypeVO | undefined;
    category!: ThingCategory;

    // extract from json_schema for display in dialog
    thingTitle = "";
    thingDescription: string | undefined = "";

    constructor(
        public dialogRef: MatDialogRef<ThingDialogComponent>,
        private thingCategoryService: ThingCategoryService,
        public intercomService: InternalCommunicationService,
        public utilityService: UtilityService,
        private fb: FormBuilder,
        @Inject(MAT_DIALOG_DATA) public data: ThingDialogData
    ) {
        this.form = this.fb.group({
            name: ["", Validators.required],
            configurations: this.fb.group({}),
            measurands: this.fb.array([])
        });

        if (this.data.thing) {
            const thingType = this.intercomService.thingTypes.find(tt => tt.identifier === this.data.thing?.thing_type_id);

            if (thingType) {
                this.loadThingType(thingType);
                this.selectedThingType = thingType;
                this.form.get("name")?.setValue(this.data.thing.name);

                if (this.data.thing.configurations) this.form.get("configurations")?.patchValue(this.data.thing.configurations);
                if (this.data.thing.measurands) {
                    this.measurands.controls.forEach((control, index) => {
                        const measurand = this.data.thing?.measurands?.find(m => this.selectedThingType?.measurands && m.measurand_type === this.selectedThingType.measurands[index].obis_id);
                        control.setValue(measurand?.enabled ?? true);
                    });
                }
            }
        } else {
            // Ensure that thingTypes is defined before accessing its elements
            if (this.intercomService.thingTypes && this.intercomService.thingTypes.length > 0) {
                // Load the first type into the form
                this.loadThingType(this.intercomService.thingTypes[0]);
                this.selectedThingType = this.intercomService.thingTypes[0];
            }
            else console.error("ThingTypes data is undefined or null");
        }
    }

    loadThingType(type: ThingTypeVO) {
        // Extract title and description to display them separately (these are always present, so that's ok)
        if (type) {
            this.thingTitle = type.name;
            this.thingDescription = type.description;
        }

        this.selectedThingType = type;
        this.category = type.category ? this.thingCategoryService.getCategory(type.category) : { key: "error", image: "", title: "none" };

        // create configurations with optional validators
        const configurationsGroup = this.fb.group({});

        if (type.configurations) {
            type.configurations.forEach((config) => {
                const validators = [];

                if (config.validation_regex) validators.push(Validators.pattern(config.validation_regex));
                configurationsGroup.addControl(config.name, this.fb.control("", validators));
            });
        }

        // create measurands with default values
        const measurandsArray = this.fb.array(type.measurands ? type.measurands.map(() => this.fb.control(true)) : []);

        // Initialize the form with the selected type's name, configurations, and measurands
        this.form = this.fb.group({
            name: [type.name, Validators.required],
            configurations: configurationsGroup,
            measurands: measurandsArray
        });

        // Initialize the thing object with the selected type
        if (!this.data.thing) {
            const thing: ThingCreateVO = {
                name: type.name,
                gateway_id: this.data.gateway.gateway_id,
                thing_type_id: type.identifier,
                configurations: {},
                measurands: []
            };
            this.data.thing = thing;
        }
        else this.data.thing.thing_type_id = type.identifier;
    }

    get measurands(): FormArray {
        return this.form.get("measurands") as FormArray;
    }

    onCancelClick(): void {
        this.dialogRef.close();
    }

    onOkClick() {
        if (this.form.valid && this.selectedThingType && this.data.thing) {
            this.data.thing.name = this.form.get("name")?.value;

            // Set configurations based on the form values
            const configurations: { [key: string]: string } = {};

            if (this.selectedThingType.configurations) {
                this.selectedThingType.configurations.forEach((config) => {
                    const value = this.form.get("configurations")?.get(config.name)?.value.toString();

                    if(value) configurations[config.name] = value;
                });
            }
            this.data.thing.configurations = configurations;

            // Set measurands based on the form values
            const measurands: MeasurandVO[] = [];

            if (this.selectedThingType.measurands) {
                this.measurands.controls.forEach((control, index) => {
                    if (this.selectedThingType && this.selectedThingType.measurands) {
                        const measurandType = this.selectedThingType.measurands[index];

                        measurands.push({
                            measurand_type: measurandType.obis_id,
                            enabled: control.value,
                            interval: 30000,
                            timeout: 1000
                        });
                    }
                });
            }
            this.data.thing.measurands = measurands;

            // Close the dialog and pass the data back
            this.dialogRef.close(this.data);
        }
    }

    public ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.complete();
    }
}

export interface ThingDialogData {
    gateway: GatewayVO;
    thing: ThingCreateVO | ThingVO | undefined;
}

