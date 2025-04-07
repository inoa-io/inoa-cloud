// Add to your component's imports
import { Component, ElementRef, ViewChild, AfterViewInit, OnDestroy } from "@angular/core";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import * as L from "leaflet";
import "leaflet-control-geocoder";
import { MatAutocompleteSelectedEvent } from "@angular/material/autocomplete";
import { AddressSuggestion, MapService } from "src/app/services/map-service";
import { GatewaysService } from "@inoa/api";

@Component({
	selector: "gc-gateway-fleet-map",
	templateUrl: "./gateway-fleet-map.component.html",
	styleUrls: ["./gateway-fleet-map.component.css"]
})
export class GatewayFleetMapComponent implements AfterViewInit, OnDestroy {
	@ViewChild("mapRef") mapContainer!: ElementRef;
	longitudeCurrent = 13.754756994089194;
	latitudeCurrent = 51.03647591263929;
	map!: L.Map;
	resizeObserver?: ResizeObserver;
	
	constructor(public gatewaysService: GatewaysService, public mapService: MapService, public intercomService: InternalCommunicationService) {
		this.mapService.mapTypeFormControl.setValue(this.mapService.mapType);
		this.mapService.mapTypeFormControl.valueChanges.subscribe(() => {
			this.mapService.switchMapType(this.map);
		});
	}

	ngOnDestroy() {
		if (this.resizeObserver) {
			this.resizeObserver.disconnect();
		}
	}

	ngAfterViewInit() {
		setTimeout(() => {
			this.initMap();
			this.updateMap();

			//sub to selected gateway changes
			this.intercomService.selectedGatewayChangedEventEmitter.subscribe(() => {
				const gateway = this.intercomService.selectedGateway;
	
				if (gateway && gateway.location && gateway.location.latitude && gateway.location.longitude) {
					this.latitudeCurrent = gateway.location.latitude;
					this.longitudeCurrent = gateway.location.longitude;
	
					this.mapService.onCoordsChanged(this.map, gateway.location.latitude, gateway.location.longitude)
				}

				setTimeout(() => {
					this.updateMap();
				}, 0);
			});
	
			//sub to gateway location changes
			this.mapService.updateMapEventEmitter.subscribe(() => {
				setTimeout(() => {
					this.updateMap();
				}, 0);
			});
		}, 0);
	}

	initMap() {
		// Initialize the map
		this.map = L.map(this.mapContainer.nativeElement, {
			zoomControl: true,
			maxZoom: 18,
			minZoom: 3
		});

		// Set up resize observer
		this.resizeObserver = new ResizeObserver(() => {
			this.map.invalidateSize();
		});
		this.resizeObserver.observe(this.mapContainer.nativeElement);
	}

	updateMap() {
		// Initial map load
		this.mapService.switchMapType(this.map);

		// set coordinates to selected gateway (if they are known)
		const gateway = this.intercomService.selectedGateway;
		if (gateway && gateway.location && gateway.location.latitude && gateway.location.longitude) {
			this.latitudeCurrent = gateway.location.latitude;
			this.longitudeCurrent = gateway.location.longitude;
		}

		// Move mapview to coords
		this.map.setView([this.latitudeCurrent, this.longitudeCurrent], 16);
		
		// Add device marker layer to the map
		this.mapService.gatewayMarkerLayer.addTo(this.map);

		this.gatewaysService.findGateways().subscribe((gateways) => {
			this.mapService.updateDeviceMarkers(this.map, gateways.content);
		});

	}

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
			
				// Clear the input
				this.mapService.addressSearchControl.setValue("");
			}
		});
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
}