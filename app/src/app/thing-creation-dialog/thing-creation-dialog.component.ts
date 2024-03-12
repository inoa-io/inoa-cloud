import {Component, Inject, OnDestroy} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ThingTypesService, ThingsService} from "@inoa/api";
import {GatewayVO, ThingVO, ThingTypeVO} from "@inoa/model";
import {FormGroup} from "@angular/forms";
import {FormlyFieldConfig, FormlyFormOptions} from "@ngx-formly/core";
import {FormlyJsonschema} from "@ngx-formly/core/json-schema";
import {Subject, takeUntil, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {ThingCategoryService, ThingCategory} from "../thing-category.service";

/*enum ConnectionStatus {
  Created,
  Connected,
  Disabled
}*/

const staticThingTypes: ThingTypeVO[] =
[
  { id: "dzg_dvh4013", name: "DZG DVH 4013", created: "", updated: "" },
  { id: "dzg_mdvh4006", name: "DZG MDVH 4006", created: "", updated: "" },
  { id: "shelly_plug_s", name: "Shelly Plug S", created: "", updated: "" },
  { id: "shelly_plus_pm2", name: "Shelly Plus PM2", created: "", updated: "" },
];

@Component({
  selector: "gc-thing-creation-dialog",
  templateUrl: "./thing-creation-dialog.component.html",
  styleUrls: ["./thing-creation-dialog.component.css"]
})
export class ThingCreationDialogComponent implements OnDestroy
{
  private destroy$: Subject<any> = new Subject<any>();

  public thingTypes: ThingTypeVO[] = [];

  form!: FormGroup;
  model: any;
  options!: FormlyFormOptions;
  fields!: FormlyFieldConfig[];
  selectedThingType!: ThingTypeVO;
  category!: ThingCategory;

  constructor(
    public dialogRef: MatDialogRef<ThingCreationDialogComponent>,
    private formlyJsonschema: FormlyJsonschema,
    private http: HttpClient,
    private thingCategoryService: ThingCategoryService,
    private thingsService: ThingsService,
    private thingsTypesService: ThingTypesService,
    @Inject(MAT_DIALOG_DATA) public data: ThingCreationDialogData)
  {
    this.form = new FormGroup({});

    // Ensure that data.thingTypes is defined before accessing its elements
    if (this.data && this.data.thingTypes && this.data.thingTypes.length > 0) {
      // Load the first type into the form
      this.loadThingTypes(this.data.thingTypes[0]);
      this.selectedThingType = this.data.thingTypes[0];
      this.thingTypes = this.data.thingTypes;
    }
    else { console.error("ThingTypes data is undefined or null"); }

    //load first type into the form
    // this.loadThingTypes(data.thingTypes[0]); //TODO: switch to real data, not static
    this.loadThingTypes(staticThingTypes[0]);
    this.selectedThingType = staticThingTypes[0];

    // this.thingTypes = data.thingTypes; //TODO: switch to real data, not static
    this.thingTypes = staticThingTypes;
  }

  loadThingTypes(type: ThingTypeVO)
  {
    this.http.get<any>(`assets/thing-types/${type.id}.json`)
        .pipe(
            tap(({ schema, category }) =>
            {
              this.selectedThingType = type;
              this.form = new FormGroup({});
              this.options = {};
              this.fields = [this.formlyJsonschema.toFieldConfig(schema)];
              this.category = this.thingCategoryService.getCategory(category);
              this.model = {}; //model;
            }),
            takeUntil(this.destroy$),
        )
        .subscribe();
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

  getThingImage(type: ThingTypeVO) { console.log("Type is: " + type); return this.thingCategoryService.getCategory("unknown").image; }

  onSubmit(model: {email: string}) { console.log("Submitted for: " + model.email); }

  public ngOnDestroy()
  {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  protected readonly staticThingTypes = staticThingTypes;
}

export interface ThingCreationDialogData
{
  gateway: GatewayVO;
  thing: ThingVO | undefined;
  thingTypes: ThingTypeVO[];
}
