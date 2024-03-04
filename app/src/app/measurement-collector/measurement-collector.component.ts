import { Component, OnInit } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, ValidatorFn, Validators } from "@angular/forms";
import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { GatewaysService, ThingTypesService, ThingsService } from "@inoa/api";
import { GatewayVO, ThingTypeVO, ThingVO } from "@inoa/model";
import { MatDialog} from "@angular/material/dialog";
import { ThingCreationDialogComponent, ThingCreationDialogData } from "../thing-creation-dialog/thing-creation-dialog.component";
import { StepperSelectionEvent } from "@angular/cdk/stepper";
import { ThingCategory, ThingCategoryService } from "../thing-category.service";

function autocompleteObjectValidator(): ValidatorFn
{
  return (control: AbstractControl): { [key: string]: any } | null =>
  {
    if (typeof control.value === "string")
    {
      if (control.value === "") { return null; }

      return {"invalidAutocompleteObject": {value: control.value}};
    }

    return null;
  }
}

function requiredIfValidator(predicate: () => any): ValidatorFn
{
  return (formControl =>
  {
    if (!formControl.parent) { return null; }
    if (predicate()) { return Validators.required(formControl); }

    return null;
  })
}

export type ConnectionStatus = "created" | "connected" | "measuring" | "offline" | "deactivated";
export const ConnectionStatus =
{
  Created: "created" as ConnectionStatus,
  Connected: "connected" as ConnectionStatus,
  Measuring: "measuring" as ConnectionStatus,
  Offline: "offline" as ConnectionStatus,
  Deactivated: "deactivated" as ConnectionStatus
};
export type NetworkInterface = "wifi" | "lan";

export const NetworkInterface =
{
  Wifi: "wifi" as NetworkInterface,
  Lan: "lan" as NetworkInterface
};

@Component({
  selector: "gc-measurement-collector",
  templateUrl: "./measurement-collector.component.html",
  styleUrls: ["./measurement-collector.component.css"]
})
export class MeasurementCollectorComponent implements OnInit
{
  connectionStatus: typeof ConnectionStatus = ConnectionStatus;

  constructor(private _formBuilder: FormBuilder,
              private gatewaysService: GatewaysService,
              private thingsService: ThingsService,
              private thingTypesService: ThingTypesService,
              private thingCategoryService: ThingCategoryService,
              public dialog: MatDialog,) {
  }

  public gateways: GatewayVO[] = [];
  public thingTypes: ThingTypeVO[] = [];
  public thingsList: ThingVO[] = [];

  public filteredGateways: Observable<GatewayVO[]> | undefined;

  public satelliteControl = new FormControl("", { validators: [Validators.required, autocompleteObjectValidator()] });
  public connectionControl = new FormControl("", { validators: [requiredIfValidator(() => this.selectedGateway && !this.isOnline(this.selectedGateway))] });
  public thingControl = new FormControl("", { validators: [Validators.required] });

  public selectedGateway: GatewayVO | undefined;
  public connectionType: NetworkInterface | undefined;
  public selectedThing: ThingVO | undefined;

  isAnimationRunning = false;
  isInfoCardOpen = false;

  onSelectionChange(event: StepperSelectionEvent)
  {
    if (event.previouslySelectedIndex != null && event.selectedIndex != null)
    {
      this.isAnimationRunning = true;

      // Adjust the timeout value based on the actual animation duration
      setTimeout(() => { this.isAnimationRunning = false; }, 550);
    }
  }

  showInfoCard() { this.isInfoCardOpen = true; }
  hideInfoCard() { this.isInfoCardOpen = false; }

  satelliteFormGroup = this._formBuilder.group(
  {
    satellite: this.satelliteControl,
    connection: this.connectionControl
  });

  measurementFormGroup = this._formBuilder.group({ thingControl: this.thingControl });

  public validation_msgs =
  {
    "satelliteControl":
    [
      {
        type: "invalidAutocompleteObject",
        message: "Entered serial of satellite is unknown. Click one of the autocomplete options."
      },
      { type: "required", message: "Satellite is required to collect data." }
    ]
  }

  ngOnInit()
  {
    this.gatewaysService.findGateways().subscribe(data => { this.gateways = (data.content as any); });

    this.filteredGateways = this.satelliteControl.valueChanges
    .pipe(
      startWith(""),
      map(satellite => satellite ? this._filterGateways(satellite) : this.gateways.slice())
    );

    this.thingTypesService.findThingTypes().subscribe( (data: { content: ThingTypeVO[]; }) => { this.thingTypes = data.content; });
  }

  private _filterGateways(satellite: any): GatewayVO[]
  {
    let filterValue: string;

    if (typeof satellite === "string") { filterValue = satellite.toLowerCase(); }
    else { filterValue = satellite.gateway_id.toLowerCase(); }

    if (filterValue === "") { return this.gateways.slice() }

    const filteredSatellites: GatewayVO[]  = this.gateways.filter(option => option.gateway_id.toLowerCase().includes(filterValue));

    if (filteredSatellites.length == 1 && filteredSatellites[0] === satellite)
    {
      this.selectedGateway = filteredSatellites[0];
      this.selectedThing = undefined;
      this.connectionControl.setValue(null);
    }
    else { this.selectedGateway = undefined; }

    return filteredSatellites;
  }

  public displaySatelliteFn(gateway?: GatewayVO): string { return gateway ? gateway.gateway_id : "" }

  isOnline(satellite: GatewayVO): boolean { return <boolean>satellite.status?.mqtt?.connected; }

  isMeasuring(gatewayVO: GatewayVO): boolean
  {
    if (!gatewayVO) return false;

    // TODO Figure out status for measuring data
    return this.isOnline(gatewayVO);
  }

  getLinkColor(gatewayVO: GatewayVO) { return this.isOnline(gatewayVO) ? "primary" : "accent"; }

  getLinkIcon(satellite: GatewayVO) { return this.isOnline(satellite) ? "link" : "link_off"; }

  supportsWifi(gatewayVO: GatewayVO)
  {
    return true;
    // TODO Get out the interfaces availabale at the connected gateway.
    // return gatewayVO.network_interfaces?.includes(NetworkInterface.Wifi);
  }

  supportsLAN(gatewayVO: GatewayVO)
  {
    return true;
    // TODO Get out the interfaces availabale at the connected gateway.
    // return gatewayVO.network_interfaces?.includes(NetworkInterface.Lan);
  }

  getGatewayStrokeColor() { return this.selectedGateway ? "#2196f3" : "#999"; }

  getSatelliteFillColor() { return this.selectedGateway ? "#bbdefb" : "#ccc"; }

  getSatelliteTitle(): string { return this.selectedGateway ? this.selectedGateway.gateway_id : "Choose a satellite"; }

  getFlowTitleCloudSatellite(): string
  {
    if (!this.selectedGateway) return this.getSatelliteTitle();

    return this.isOnline(this.selectedGateway) ? "Satellite connected to cloud" : "Connect your satellite [" + this.selectedGateway.gateway_id + "] to INOA Cloud";
  }

  getFlowColorCloudSatellite() { return this.selectedGateway && this.isOnline(this.selectedGateway) ? "#2196f3" : "#999"; }

  getFlowColorEnergyMeter() { return this.selectedGateway && this.isOnline(this.selectedGateway) ? "#2196f3" : "#999"; }

  getMainFlowDisplay(): string
  {
    if (!this.selectedGateway || !this.isMeasuring(this.selectedGateway)) return "none";

    return "inline";
  }

  getFlowColorSatelliteThing(category: ThingCategory)
  {
    /* TODO Find category of thing
    return this.selectedThing && this.selectedThing.category == category ? "#2196f3" : "#999";

     */
    return "#999";
  }
  getFlowDisplaySatelliteThing(category: ThingCategory)
  {
    /* TODO Find Category and thing status.
    if(this.selectedThing
        && this.selectedThing.category == category
        && this.selectedThing.status == ConnectionStatus.Measuring) {
      return "inline";
    }
     */
    return "none";
  }

  getThingTitle(category: ThingCategory)
  {
    if (this.selectedThing && this.selectedThing.thing_type_id && category == this.getThingCategory(this.selectedThing.thing_type_id))
    {
      return this.selectedThing && this.selectedThing.name ? this.selectedThing.name : "No name";
    }

    return category.title;
  }
  getThingOpacity(category: ThingCategory): string
  {
    // TODO Get category of thing out.
    // if (this.selectedThing && this.selectedThing.category == category) {
    //   return "1";
    // }
    if (this.isThingCategoryConnected(category)) { return "0.5"; }
    return "0";
  }

  isThingCategoryConnected(category: ThingCategory): boolean
  {
    if (!this.selectedGateway) return false;
    /*
     ** TODO Get connected Things from Gateway.
    for(let thing of this.selectedGateway.connected_things) {
      if (thing.category == category) return true;
    }
     */
    return false;
  }

  getFocusStepOneDisplay(): string
  {
    if (this.selectedGateway) { return "none"; }

    return "inline";
  }

  getFocusStepOneTwoDisplay(): string
  {
    if (!this.selectedGateway || this.isOnline(this.selectedGateway)) { return "none"; }

    return "inline";
  }

  resetAll()
  {
    this.selectedGateway = undefined;
    this.selectedThing = undefined;
  }

  refreshThingsList()
  {
    // Get connected things for Gateway.
    if (this.selectedGateway?.gateway_id)
    {
      this.thingsService.findThingsByGatewayId(this.selectedGateway.gateway_id).subscribe(next =>
        {
          this.thingsList = next.content;

          // Select first thing in list
          if(this.thingsList.length > 0) this.selectedThing = this.thingsList[0];
        });
    }
  }

  thingSelected() { return this.selectedThing; }

  createThingDialog()
  {
    if (!this.selectedGateway) { return; }

    const dialogData: ThingCreationDialogData =
    {
      gateway: this.selectedGateway,
      thing: this.selectedThing,
      thingTypes: this.thingTypes
    }

    const dialogRef = this.dialog.open(ThingCreationDialogComponent,
    {
      width: "600px",
      data: dialogData,
    });

    dialogRef.afterClosed().subscribe(result =>
    {
      this.thingsService.createThing(result).subscribe(() =>
      {
        this.refreshThingsList();
      });
    });
  }

  getThingImage(thingTypeId: string | undefined)
  {
    if (thingTypeId == undefined) return "assets/energy_meter.png";

    const category = this.getThingCategory(thingTypeId);

    return category.image;
  }

  getThingCategory(thingTypeId: string): ThingCategory
  {
    //TODO: should probably use thingTypeId later
    return this.thingCategoryService.getCategory("energy_meter");
  }

  getSatelliteStrokeColor() { return this.satelliteFormGroup.valid ? "#2196f3" : "#999"; }
}

