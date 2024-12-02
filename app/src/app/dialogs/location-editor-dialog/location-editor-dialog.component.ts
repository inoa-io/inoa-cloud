import { AfterViewInit, Component, ElementRef, Inject, ViewChild } from "@angular/core";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import * as L from "leaflet";
import "leaflet-control-geocoder";
import { debounceTime, distinctUntilChanged, filter } from "rxjs";
import { AddressSuggestion, MapService } from "src/app/services/map-service";
import { GatewayLocationDataVO } from "src/openapi/model/gatewayLocationData";

export interface LocationEditorDialogData {
	location: GatewayLocationDataVO
}

@Component({
	selector: "gc-location-editor-dialog",
	templateUrl: "./location-editor-dialog.component.html",
	styleUrls: ["./location-editor-dialog.component.css"]
})
export class LocationEditorDialogComponent implements AfterViewInit {
	@ViewChild("mapRef") mapContainer!: ElementRef;
	longitudeCurrent = 13.78457486629486;
	latitudeCurrent = 51.068173923572196;
	map!: L.Map;

	constructor(public mapService: MapService, public dialogRef: MatDialogRef<LocationEditorDialogData>, @Inject(MAT_DIALOG_DATA) public data: LocationEditorDialogData) {
		this.mapService.mapTypeFormControl.setValue(this.mapService.mapType);
		this.mapService.mapTypeFormControl.valueChanges.subscribe(() => {
			this.mapService.switchMapType(this.map);
		});

		// Set up address search with debounce
		this.mapService.addressSearchControl.valueChanges.pipe(
			debounceTime(300),
			distinctUntilChanged(),
			filter(value => typeof value === "string" && value.length > 2)
		  ).subscribe(value => {
			if (typeof value === "string") {
			  this.mapService.searchAddressSuggestions(value);
			}
		  });
	}

	ngAfterViewInit() {
		setTimeout(() => {
			this.initMap();
			this.updateMap();

			if (this.data.location) {
				const location = this.data.location;
	
				// if we have coordinates set location based on those
				if (location.longitude) this.longitudeCurrent = location.longitude;
				if (location.latitude) this.latitudeCurrent = location.latitude;
	
				// if we do not have coordinates, prefill the address searchbar
				if (!location.latitude || !location.longitude) {
					this.mapService.addressSearchControl.setValue(this.mapService.buildAddressString(location));
	
					//try to get coords from address
					this.searchAddress();
				}
				this.mapService.onCoordsChanged(this.map, this.latitudeCurrent, this.longitudeCurrent);
			}
		}, 0);
	}

	// gets geo coords from address string (hopefully)
	searchAddress() {
		const address = this.mapService.addressSearchControl.value;
		if (!address) return;
	
		const geocoder = new (L.Control as any).Geocoder.Nominatim();
		
		geocoder.geocode(address, (results: any[]) => {
			if (results && results.length > 0) {
				const { center } = results[0];
			
				// Update coordinates
				this.latitudeCurrent = center.lat;
				this.longitudeCurrent = center.lng;
			
				// Update marker and map
				this.mapService.onCoordsChanged(this.map, this.latitudeCurrent, this.longitudeCurrent);
			}
		});
	}

	initMap() {
		// Initialize the map
		this.map = L.map(this.mapContainer.nativeElement, {
			zoomControl: true,
			maxZoom: 18,
			minZoom: 3
		});
		
		// Set marker on click
		this.map.on("click", (event: L.LeafletMouseEvent) => {
			this.latitudeCurrent = event.latlng.lat;
			this.longitudeCurrent = event.latlng.lng;
			this.mapService.onCoordsChanged(this.map, this.latitudeCurrent, this.longitudeCurrent);
		});
	}

	updateMap() {
		// Move mapview to coords
		this.map.setView([this.latitudeCurrent, this.longitudeCurrent], 16);

		// Initial map load
		this.mapService.switchMapType(this.map);

		this.mapService.setMarkerPos(this.map, this.latitudeCurrent, this.longitudeCurrent);
	}

	// Handle selection from autocomplete
	onAddressSelected(event: MatAutocompleteSelectedEvent) {
		const selected = event.option.value as AddressSuggestion;
		
		// Update coordinates
		this.latitudeCurrent = selected.center.lat;
		this.longitudeCurrent = selected.center.lng;
		
		// Update marker and map
		this.mapService.onCoordsChanged(this.map, this.latitudeCurrent, this.longitudeCurrent);
	}

	close(): void {
		this.dialogRef.close();
	}

	save(): void {
		if(this.mapService.currentLocationData) this.data.location = this.mapService.currentLocationData;
		this.dialogRef.close(this.data);
	}
}
