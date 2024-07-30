import {Component, Inject, OnDestroy} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GatewayVO, ThingTypeVO, ThingCreateVO} from "@inoa/api";
import {FormGroup} from "@angular/forms";
import {FormlyFieldConfig, FormlyFormOptions} from "@ngx-formly/core";
import {FormlyJsonschema} from "@ngx-formly/core/json-schema";
import {Subject} from "rxjs";
import {ThingCategoryService, ThingCategory} from "../thing-category.service";

@Component({
  selector: "gc-thing-creation-dialog",
  templateUrl: "./thing-creation-dialog.component.html",
  styleUrls: ["./thing-creation-dialog.component.css"]
})
export class ThingCreationDialogComponent implements OnDestroy
{
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

  constructor(
    public dialogRef: MatDialogRef<ThingCreationDialogComponent>,
    private formlyJsonschema: FormlyJsonschema,
    private thingCategoryService: ThingCategoryService,
    @Inject(MAT_DIALOG_DATA) public data: ThingCreationDialogData
  )
  {
    this.form = new FormGroup({});

    // Ensure that data.thingTypes is defined before accessing its elements
    if (this.data && this.data.thingTypes && this.data.thingTypes.length > 0)
    {
      // Load the first type into the form
      this.loadThingType(this.data.thingTypes[0]);
      this.selectedThingType = this.data.thingTypes[0];
      this.thingTypes = this.data.thingTypes;
    }
    else { console.error("ThingTypes data is undefined or null"); }
  }

  loadThingType(type: ThingTypeVO)
  {
    this.selectedThingType = type;
    this.form = new FormGroup({});
    this.options = {};
    this.fields = type.json_schema ? [this.formlyJsonschema.toFieldConfig(type.json_schema)] : [];
    this.category = type.category ? this.thingCategoryService.getCategory(type.category) : { key: "error", image: "", title: "none" };
    this.model = {};

    const thing: ThingCreateVO = {
      name: type.name,
      gateway_id: this.data.gateway.gateway_id,
      thing_type_id: type.id,
      config: {}
    }

    this.data.thing = thing;
  }

  onCancelClick(): void { this.dialogRef.close(); }

  onCreateClick()
  {
    if (this.form.valid && this.selectedThingType)
    {
      if(this.data.thing)
      {
        // this.data.thing.category = this.thingCategoryService.getCategory("energy_meter")
        // this.data.thing.status = ConnectionStatus.Created;
        this.data.thing.thing_type_id = this.selectedThingType.id;
      }
    }
  }

  getThingImage(type: ThingTypeVO) {
    if (type.category) {
      console.log("Getting image for " + type);

      return this.thingCategoryService.getCategory(type.category).image;
    }
    
    return "";
  }

  onSubmit(model: { email: string })
  {
    console.log("Submitted for: " + model.email);
  }

  public ngOnDestroy()
  {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
}

export interface ThingCreationDialogData
{
  gateway: GatewayVO;
  thing: ThingCreateVO | undefined;
  thingTypes: ThingTypeVO[];
}
