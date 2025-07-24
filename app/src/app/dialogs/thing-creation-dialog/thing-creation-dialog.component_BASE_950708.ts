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
	thingDescription: string | undefined = "";

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
		if (type) {
			this.thingTitle = type.name;
			this.thingDescription = type.description;
		}

		this.selectedThingType = type;
		this.form = new FormGroup({});
		this.options = {};

		this.category = type.category ? this.thingCategoryService.getCategory(type.identifier) : { key: "error", image: "", title: "none" };
		this.model = {};

		this.addOutlineAppearanceToFields(this.fields);

		const thing: ThingCreateVO = {
			name: type.name,
			gateway_id: this.data.gateway.gateway_id,
			thing_type_id: type.identifier
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
				this.data.thing.thing_type_id = this.selectedThingType.identifier;
				this.data.thing.name = this.form.get("name")?.value;

				// TODO

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
