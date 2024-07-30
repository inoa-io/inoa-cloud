import { Component, OnInit } from "@angular/core";
import {AbstractControl, FormBuilder, FormControl, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { GatewayVO, GatewaysService, ThingTypesService, ThingsService } from "@inoa/api";
import { StepperSelectionEvent } from "@angular/cdk/stepper";
import { ThingCategory, ThingCategoryService } from "../thing-category.service";
import { InternalCommunicationService } from "../internal-communication-service";
import { DialogService } from "../dialog-service";
import { ThingCreationDialogComponent } from "../thing-creation-dialog/thing-creation-dialog.component";

function autocompleteObjectValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (typeof control.value === "string") {
      if (control.value === "") { return null; }
      return { "invalidAutocompleteObject": { value: control.value } };
    }
    return null;
  }
}

function requiredIfValidator(predicate: () => boolean): ValidatorFn {
  return (formControl) => {
    if (!formControl.parent) { return null; }
    if (predicate()) { return Validators.required(formControl); }
    return null;
  };
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

  constructor(
    private formBuilder: FormBuilder,
    private gatewaysService: GatewaysService,
    private thingsService: ThingsService,
    private thingTypesService: ThingTypesService,
    private thingCategoryService: ThingCategoryService,
    private dialogService: DialogService,
    public intercomService: InternalCommunicationService,
  ) { }

  public filteredGateways: Observable<GatewayVO[]> | undefined;

  public satelliteControl = new FormControl("", { validators: [Validators.required, autocompleteObjectValidator()] });
  public connectionControl = new FormControl("", { validators: [requiredIfValidator(() => this.intercomService.selectedGateway !== undefined && !this.isOnline(this.intercomService.selectedGateway))] });
  public thingControl = new FormControl("", { validators: [Validators.required] });

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

  satelliteFormGroup = this.formBuilder.group(
  {
    satellite: this.satelliteControl,
    connection: this.connectionControl
  });

  measurementFormGroup = this.formBuilder.group({ thingControl: this.thingControl });

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
    this.gatewaysService.findGateways().subscribe(data => { this.intercomService.gateways = data.content; });

    this.filteredGateways = this.satelliteControl.valueChanges.pipe (
      startWith(""),
      map(satellite => satellite ? this.filterGateways(satellite) : this.intercomService.gateways.slice())
    );

    this.thingTypesService.findThingTypes().subscribe( (data) => { this.intercomService.thingTypes = data.content; });
  }

  filterGateways(satellite: string | GatewayVO): GatewayVO[]
  {
    let filterValue: string;

    if (typeof (satellite) === "string") filterValue = satellite.toLowerCase();
    else filterValue = satellite.gateway_id.toLowerCase();

    if (filterValue === "") { return this.intercomService.gateways.slice() }

    const filteredSatellites: GatewayVO[]  = this.intercomService.gateways.filter(option => option.gateway_id.toLowerCase().includes(filterValue));

    if (filteredSatellites.length == 1 && filteredSatellites[0] === satellite)
    {
      this.intercomService.selectedGateway = filteredSatellites[0];
      this.intercomService.selectedThing = undefined;
      this.connectionControl.setValue(null);
    }
    else { this.intercomService.selectedGateway = undefined; }

    return filteredSatellites;
  }

  openCreateThingDialog()
  {
    this.dialogService.openCreateThingDialog()?.afterClosed().subscribe(data =>
    {
      if (data != undefined) {
        this.thingsService.createThing(data).subscribe(() => { this.refreshThingsList(); });
      }
    });
  }

  refreshThingsList()
	{
		// Get connected things for Gateway.
		if (this.intercomService.selectedGateway?.gateway_id)
		{
			this.thingsService.findThingsByGatewayId(this.intercomService.selectedGateway.gateway_id).subscribe(next =>
				{
					this.intercomService.thingsList = next.content;

					// Select first thing in list
					if(this.intercomService.thingsList.length > 0) this.intercomService.selectedThing = this.intercomService.thingsList[0];
				});
		}
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
    console.log(gatewayVO);
    return true;
    // TODO Get out the interfaces availabale at the connected gateway.
    // return gatewayVO.network_interfaces?.includes(NetworkInterface.Wifi);
  }

  supportsLAN(gatewayVO: GatewayVO)
  {
    console.log(gatewayVO);
    return true;
    // TODO Get out the interfaces availabale at the connected gateway.
    // return gatewayVO.network_interfaces?.includes(NetworkInterface.Lan);
  }

  getGatewayStrokeColor() { return this.intercomService.selectedGateway ? "#2196f3" : "#999"; }

  getSatelliteFillColor() { return this.intercomService.selectedGateway ? "#bbdefb" : "#ccc"; }

  getSatelliteTitle(): string { return this.intercomService.selectedGateway ? this.intercomService.selectedGateway.gateway_id : "Choose a satellite"; }

  getFlowTitleCloudSatellite(): string
  {
    if (!this.intercomService.selectedGateway) return this.getSatelliteTitle();

    return this.isOnline(this.intercomService.selectedGateway) ? "Satellite connected to cloud" : "Connect your satellite [" + this.intercomService.selectedGateway.gateway_id + "] to INOA Cloud";
  }

  getFlowColorCloudSatellite() { return this.intercomService.selectedGateway && this.isOnline(this.intercomService.selectedGateway) ? "#2196f3" : "#999"; }

  getFlowColorEnergyMeter() { return this.intercomService.selectedGateway && this.isOnline(this.intercomService.selectedGateway) ? "#2196f3" : "#999"; }

  getMainFlowDisplay(): string
  {
    if (!this.intercomService.selectedGateway || !this.isMeasuring(this.intercomService.selectedGateway)) return "none";

    return "inline";
  }

  getFlowColorSatelliteThing(category: ThingCategory)
  {
    console.log(category);
    /* TODO Find category of thing
    return this.selectedThing && this.selectedThing.category == category ? "#2196f3" : "#999";

     */
    return "#999";
  }
  getFlowDisplaySatelliteThing(category: ThingCategory)
  {
    console.log(category);
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
    if (this.intercomService.selectedThing && this.intercomService.selectedThing.thing_type_id && category == this.getThingCategory(this.intercomService.selectedThing.thing_type_id))
    {
      return this.intercomService.selectedThing && this.intercomService.selectedThing.name ? this.intercomService.selectedThing.name : "No name";
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
    console.log(category);
    if (!this.intercomService.selectedGateway) return false;
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
    if (this.intercomService.selectedGateway) { return "none"; }

    return "inline";
  }

  getFocusStepOneTwoDisplay(): string
  {
    if (!this.intercomService.selectedGateway || this.isOnline(this.intercomService.selectedGateway)) { return "none"; }

    return "inline";
  }

  resetAll()
  {
    this.intercomService.selectedGateway = undefined;
    this.intercomService.selectedThing = undefined;
  }

  thingSelected() { return this.intercomService.selectedThing; }

  getThingImage(thingTypeId: string | undefined)
  {
    if (thingTypeId == undefined) return "assets/energy_meter.png";

    const category = this.getThingCategory(thingTypeId);

    return category.image;
  }

  getThingCategory(thingTypeId: string): ThingCategory
  {
    //TODO: should probably use thingTypeId later
    console.log(thingTypeId);
    return this.thingCategoryService.getCategory("energy_meter");
  }

  getSatelliteStrokeColor() { return this.satelliteFormGroup.valid ? "#2196f3" : "#999"; }
}

