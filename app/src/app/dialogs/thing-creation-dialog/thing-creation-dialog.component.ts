import { Component, Inject, OnDestroy } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { GatewayVO, ThingTypeVO, ThingCreateVO } from "@inoa/api";
import { FormGroup } from "@angular/forms";
import { FormlyFieldConfig, FormlyFormOptions } from "@ngx-formly/core";
import { FormlyJsonschema } from "@ngx-formly/core/json-schema";
import { Subject } from "rxjs";
import { ThingCategoryService, ThingCategory } from "../../services/thing-category.service";
import { UtilityService } from "../../services/utility-service";
import { InternalCommunicationService } from "../../services/internal-communication-service";

@Component({
    selector: "gc-thing-creation-dialog",
    templateUrl: "./thing-creation-dialog.component.html",
    styleUrls: ["./thing-creation-dialog.component.css"]
})
export class ThingCreationDialogComponent implements OnDestroy {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private destroy$: Subject<any> = new Subject<any>();

    public thingTypes: ThingTypeVO[] = [];

    form!: FormGroup;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    model: any;
    options!: FormlyFormOptions;
    fields!: FormlyFieldConfig[];
    selectedThingType!: ThingTypeVO;
    category!: ThingCategory;

    // extract from json_schema for display in dialog
    thingTitle = "";
    thingDescription = "";

    constructor(
        public dialogRef: MatDialogRef<ThingCreationDialogComponent>,
        private formlyJsonschema: FormlyJsonschema,
        private thingCategoryService: ThingCategoryService,
        public intercomService: InternalCommunicationService,
        public utilityService: UtilityService,
        @Inject(MAT_DIALOG_DATA) public data: ThingCreationDialogData
    ) {
        this.form = new FormGroup({});

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
        if (type.json_schema) {
            this.thingTitle = type.json_schema["title"] || "";
            this.thingDescription = type.json_schema["description"] || "";
        }

        this.selectedThingType = type;
        this.form = new FormGroup({});
        this.options = {};

        // Create a copy of the json_schema without title and description for fields
        const fieldSchema = type.json_schema ? { ...type.json_schema } : {};
        delete fieldSchema["title"];
        delete fieldSchema["description"];

        this.fields = fieldSchema ? [this.formlyJsonschema.toFieldConfig(fieldSchema)] : [];
        this.category = type.category ? this.thingCategoryService.getCategory(type.id) : { key: "error", image: "", title: "none" };
        this.model = {};

        this.addOutlineAppearanceToFields(this.fields);

        const thing: ThingCreateVO = {
            name: type.name,
            gateway_id: this.data.gateway.gateway_id,
            thing_type_id: type.id,
            config: {}
        };

        this.data.thing = thing;
    }

    private addOutlineAppearanceToFields(fields: FormlyFieldConfig[]) {
        fields.forEach((field) => {
            if (field.props) {
                field.props["appearance"] = "outline";
            } else {
                field.props = { appearance: "outline" };
            }

            if (field.fieldGroup) {
                this.addOutlineAppearanceToFields(field.fieldGroup);
            }
        });
    }

    onCancelClick(): void {
        this.dialogRef.close();
    }

    onCreateClick() {
        if (this.form.valid && this.selectedThingType) {
            if (this.data.thing) {
                this.data.thing.thing_type_id = this.selectedThingType.id;
                this.data.thing.name = this.form.get("name")?.value;

                let dataPointParams = undefined;

                switch (this.data.thing.thing_type_id) {
                    case "shellyplug-s":
                        dataPointParams = {
                            id: "urn:shellyplug-s:" + this.data.thing.name.replace(/\s/g, "").toLocaleLowerCase() + ":status",
                            name: this.data.thing.name,
                            enabled: true,
                            uri: "http://" + this.form.get("host")?.value + "/status"
                        };
                        break;
                    case "shellyplusplugs":
                        dataPointParams = {
                            id: "urn:shellyplusplugs:" + this.data.thing.name.replace(/\s/g, "").toLocaleLowerCase() + ":status",
                            name: this.data.thing.name,
                            enabled: true,
                            uri: "http://" + this.form.get("host")?.value + "/rpc/Shelly.GetStatus"
                        };
                        break;
                    case "s0":
                        dataPointParams = {
                            id: "urn:s0:" + this.data.thing.name.replace(/\s/g, "").toLocaleLowerCase() + ":gas",
                            interface: this.form.get("port")?.value,
                            name: this.data.thing.name,
                            enabled: true
                        };
                        break;
                }

                this.data.thing.config = dataPointParams;
            }
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
