import { EventEmitter, Injectable } from "@angular/core";
import { GatewayLocationDataVO } from "src/openapi/model/gatewayLocationData";
import * as L from "leaflet";
import { FormControl } from "@angular/forms";
import { GatewayVO } from "@inoa/model";
import { InternalCommunicationService } from "./internal-communication-service";
import { RoutingService } from "./routing-service";

export const betterMarker = L.icon({
	iconUrl: "/assets/images/betterPin.png",
	iconSize: [50, 50],
	iconAnchor: [25, 50],
	popupAnchor: [-5, -38]
});

export const deviceIconNormal = L.icon({
	iconUrl: "/assets/inoa/satellite-color-filled.png",
	iconSize: [32, 32],
	iconAnchor: [16, 16],
	popupAnchor: [0, -32]
});

export const deviceIconRed = L.icon({
	iconUrl: "/assets/inoa/satellite-color-filled-red.png",
	iconSize: [32, 32],
	iconAnchor: [16, 16],
	popupAnchor: [0, -32]
});

export const deviceIconGreen = L.icon({
	iconUrl: "/assets/inoa/satellite-color-filled-green.png",
	iconSize: [32, 32],
	iconAnchor: [16, 16],
	popupAnchor: [0, -32]
});

export interface CityView {
	lat: number;
	lng: number;
	zoom: number;
}

export interface AddressSuggestion {
	name: string;
	street?: string;
	houseNumber?: string;
	city?: string;
	postcode?: string;
	district?: string;
	center: { lat: number; lng: number };
}

export interface StaticMapOptions {
	lat: number;
	lng: number;
	zoom?: number;
	width?: number;
	height?: number;
	mapType?: "normal" | "opvn" | "topo" | "gray" | "sat" | "clear" | "dark";
}

@Injectable({
	providedIn: "root"
})
export class MapService {
	mapTypeFormControl = new FormControl("");
	addressSearchControl = new FormControl("");

	mapType = "normal";

	tileLayer!: L.TileLayer;
	marker!: L.Marker;
	currentLocationData: GatewayLocationDataVO | null = null;

	gatewayMarkers: L.Marker[] = [];  // Array to keep track of device markers
	gatewayMarkerLayer: L.LayerGroup = L.layerGroup();  // Layer group for all device markers

	addressSuggestions: AddressSuggestion[] = [];
	isSearching = false;

	cities: Record<string, CityView> = {
		"Dresden": { lat: 51.0504, lng: 13.7373, zoom: 13 },
		"Leipzig": { lat: 51.3397, lng: 12.3731, zoom: 13 },
		"Rabenau": { lat: 50.96545443218372, lng: 13.642101287841799, zoom: 16 },
		"Hamburg": { lat: 53.5978088, lng: 9.9655024, zoom: 12 },
		"Schmalkalden": { lat: 50.7213643, lng: 10.4510467, zoom: 15 }
	};

	// TODO: remove this data once a real database has been connected to the project, this is just to have something showing up
	gateways: GatewayVO[] = [
		// Dresden
		{ gateway_id: "ISRL02-90380CA9C334", name: "Cheesebox", location: { latitude: 51.06817245, longitude: 13.78458513118421 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },

		// Leipzig

		// Schmalkalden
		{ gateway_id: "ISRL02-90380CA9C340", name: "-", location: { latitude: 50.7245541, longitude: 10.4727792 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-34865DB491D4", name: "-", location: { latitude: 50.72064159068637, longitude: 10.465727448463442 }, enabled: false, created: new Date().toDateString(), updated: new Date().toDateString() },

		// Rabenau
		{ gateway_id: "ISRL01-E0E2E6BCBAE4", name: "Hauptanschluss", location: { latitude: 50.96705625, longitude: 13.636536725308048 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL01-E0E2E6BCBAB8", name: "Keller", location: { latitude: 50.96705625, longitude: 13.636536725308048 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },

		// Hamburg
		{ gateway_id: "ISRL01-E0E2E6BCBB18", name: "-", location: { latitude: 53.61542593796532, longitude: 10.023500919342043 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL01-E0E2E6BCBBF4", name: "-", location: { latitude: 53.614168956428635, longitude: 10.021913051605226 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL01-E0E2E6BCB954", name: "-", location: { latitude: 53.61390482643628, longitude: 10.022256374359133 }, enabled: false, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-083AF20D4CE0", name: "-", location: { latitude: 53.61441399120907, longitude: 10.021569728851318 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-90380CA9E958", name: "-", location: { latitude: 53.56287130865701, longitude: 9.924237728118898 }, enabled: false, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-34865DB491B8", name: "Funktionierende OKKs", location: { latitude: 53.5622981, longitude: 9.9257111 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-90380CA9CF98", name: "Funktionierende OKKs", location: { latitude: 53.562495341957714, longitude: 9.925117492675783 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL02-34865DB491CC", name: "Funktionierende OKKs", location: { latitude: 53.56266420876996, longitude: 9.924736618995668 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
		{ gateway_id: "ISRL011-0000000012", name: "Austauschgerät (geht zurück an uns)", location: { latitude: 53.61441399120907, longitude: 10.021569728851318 }, enabled: true, created: new Date().toDateString(), updated: new Date().toDateString() },
	];

	// Maps for each gateway location
	mapRegistry: Record<string, L.Map> = {};

	//should be raised after location data for gateway has been changed
	updateMapEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
	raiseUpdateMapEvent() {
		this.updateMapEventEmitter.emit();
	}

	constructor(public intercomService: InternalCommunicationService, public routingService: RoutingService) {	}

	// Display function for map suggestion autocomplete
	displayFn(suggestion: AddressSuggestion | string | null): string {
		if (!suggestion) return "";

		// If it's a suggestion object
		if (typeof suggestion === "object") {
			const parts = [
				suggestion.street,
				suggestion.houseNumber,
				suggestion.city
			].filter(Boolean);
			return parts.join(" ");
		}

		// If it's a manual string input
		return suggestion;
	}

	moveMapToSelectedGatewayLocation(map: L.Map) {
		// Move mapview to gateway coords
		if (!this.checkSelectedGatewayLocationValid()) return;
		if (!map) return;

		const location = this.intercomService.selectedGateway!.location;
		map.setView([location!.latitude!, location!.longitude!], 17);
		this.setMarkerPos(map, location!.latitude!, location!.longitude!);
		map.invalidateSize();
	}

	checkSelectedGatewayLocationValid(): boolean {
		if (!this.intercomService.selectedGateway) return false;

		const location = this.intercomService.selectedGateway.location;

		if (!location) return false;
		if (!location.longitude) return false;
		if (!location.latitude) return false;

		return true;
	}

	setMarkerPos(map: L.Map, lat: number, lng: number) {
		//remove old marker
		if (this.marker) { this.marker.remove(); }

		//set new marker
		this.marker = L.marker([lat, lng], { icon: betterMarker, interactive: false }).addTo(map);
	}

	// Search for address suggestions
	searchAddressSuggestions(query: string) {
		this.isSearching = true;

		const geocoder = new (L.Control as any).Geocoder.Nominatim({
			geocodingQueryParams: {
				limit: 5,
				countrycodes: "de",
				addressdetails: 1
			}
		});

		geocoder.geocode(query, (results: any[]) => {
			this.isSearching = false;

			if (results && results.length > 0) {
				this.addressSuggestions = results.map(result => {
					// Safely access the address object
					const addr = result.properties.address || {};

					// Create a suggestion with fallbacks for each property
					return {
						name: result.name || "",
						street: addr.road || addr.pedestrian || addr.street || "",
						houseNumber: addr.house_number || "",
						city: addr.city || addr.town || addr.village || "",
						postcode: addr.postcode || "",
						district: addr.suburb || addr.district || addr.city_district || "",
						center: result.center || { lat: 0, lng: 0 }
					};
				});
			} else {
				this.addressSuggestions = [];
			}
		});
	}

	onCoordsChanged(map: L.Map, lat: number, lng: number) {
		this.setMarkerPos(map, lat, lng);
		map.setView([lat, lng], map.options.maxZoom);

		//get address data from geo coords and set it on the dialog data object
		this.getAddressFromCoords(map, lat, lng);
	}

	// Add method to fly to a city
	flyToCity(map: L.Map, cityName: keyof typeof this.cities) {
		const city = this.cities[cityName];
		map.flyTo([city.lat, city.lng], city.zoom);
	}

	getAddressFromCoords(map: L.Map, lat: number, lng: number) {
		const reverseGeocoder = new (L.Control as any).Geocoder.Nominatim({
			reverseQueryParams: {
				addressdetails: 1, // Request detailed address information
				zoom: 18 // Request highest detail level
			}
		});

		reverseGeocoder.reverse(
			{ lat, lng },
			map.getZoom(),
			(results: any[]) => {
				if (results && results.length > 0) {
					const geoData = results[0].properties;

					// Build a detailed address string
					if (geoData.address) {
						const address = geoData.address;

						this.currentLocationData = {
							house_number: address.house_number || null,
							road: address.road || null,
							neighbourhood: address.neighbourhood || null,
							suburb: address.suburb || null,
							city_district: address.city_district || null,
							city: address.city || null,
							state: address.state || null,
							postcode: address.postcode || null,
							country: address.country || null,
							country_code: address.country_code || null,
							longitude: lng,
							latitude: lat
						};

						const formattedAddress = this.buildAddressString(this.currentLocationData);

						this.addressSearchControl.setValue(formattedAddress);
					}
				}
			}
		);
	}

	// New method to update device markers
	updateDeviceMarkers(map: L.Map, gateways: GatewayVO[]) {
		// Clear existing markers
		this.gatewayMarkers.forEach(marker => this.gatewayMarkerLayer.removeLayer(marker));
		this.gatewayMarkers = [];

		// Add new markers
		gateways.forEach(gateway => {
			if (gateway.location && gateway.location.longitude && gateway.location.latitude) {
				const marker = L.marker(
					[gateway.location.latitude, gateway.location.longitude],
					{ icon: this.intercomService.selectedGateway?.gateway_id === gateway.gateway_id ? deviceIconGreen : gateway.enabled ? deviceIconNormal : deviceIconRed });
				
				marker.bindPopup(`<strong>${gateway.name}</strong><br><br>Gateway-Id:<br>${gateway.gateway_id}<br>`);
				marker.on("click", () => { this.selectClickedGateway(map, gateway); });

				this.gatewayMarkers.push(marker);
				this.gatewayMarkerLayer.addLayer(marker);
			}
		});

		// TODO: Remove this when the real database in connected, this only adds the test data gateways so there is something on the map
		this.gateways.forEach(gateway => {
			if (gateway.location && gateway.location.longitude && gateway.location.latitude) {
				const marker = L.marker(
					[gateway.location.latitude, gateway.location.longitude],
					{ icon: gateway.enabled ? deviceIconNormal : deviceIconRed }
				).bindPopup(`
					<strong>${gateway.name}</strong><br><br>
					Gateway-Id:<br>${gateway.gateway_id}<br>
            `);

				this.gatewayMarkers.push(marker);
				this.gatewayMarkerLayer.addLayer(marker);
			}
		});
	}

	selectClickedGateway(map: L.Map, gateway: GatewayVO) {
		// this.intercomService.selectedGateway = gateway;
		this.routingService.navigateToSatelliteManager(gateway.gateway_id);
		map.setView([gateway.location!.latitude!, gateway.location!.longitude!], 17);
	}

	switchMapType(map: L.Map, mapType?: "normal" | "opvn" | "topo" | "gray" | "sat" | "clear" | "dark") {
		// remove old map layer
		if (this.tileLayer) { map.removeLayer(this.tileLayer); }

		// use optional mapType when available otherwise use global maptype
		const type = mapType ? mapType : this.mapType;

		switch (type) {
			case "normal":
				this.tileLayer = L.tileLayer("https://tile.openstreetmap.de/{z}/{x}/{y}.png").addTo(map);
				map.options.maxZoom = 18;
				break;
			case "opvn":
				this.tileLayer = L.tileLayer("https://tileserver.memomaps.de/tilegen/{z}/{x}/{y}.png").addTo(map);
				map.options.maxZoom = 18;
				break;
			case "topo":
				this.tileLayer = L.tileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png").addTo(map);
				map.options.maxZoom = 17;
				break;
			case "gray":
				this.tileLayer = L.tileLayer("https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png").addTo(map);
				map.options.maxZoom = 18;
				break;
			case "sat":
				this.tileLayer = L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}").addTo(map);
				map.options.maxZoom = 18;
				break;
			case "clear":
				this.tileLayer = L.tileLayer("http://tile.mtbmap.cz/mtbmap_tiles/{z}/{x}/{y}.png").addTo(map);
				map.options.maxZoom = 18;
				break;
			case "dark":
				this.tileLayer = L.tileLayer("https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png").addTo(map);
				map.options.maxZoom = 18;
				break;
		}

		this.correctZoom(map);
	}

	correctZoom(map: L.Map) {
		if (map.getZoom() > map.options.maxZoom!) map.setZoom(map.options.maxZoom!);
	}

	buildAddressString(address: GatewayLocationDataVO): string {
		// Fallback chain for location descriptors
		const getLocationFallback = (...keys: (keyof GatewayLocationDataVO)[]) => {
			for (const key of keys) if (address[key]) return address[key];

			return "";
		};

		// Components with their fallback alternatives
		const road = address.road ?? "";
		const houseNumber = address.house_number ?? "";
		const suburb = getLocationFallback("suburb", "city_district", "neighbourhood");
		const city = address.city ?? "";
		const postcode = address.postcode ?? "";

		// Construct address parts with smart spacing
		const parts: string[] = [];

		// Road and house number
		if (road || houseNumber) parts.push(`${road} ${houseNumber}`.trim());

		// Postcode and city
		const locationParts = [postcode, city].filter(Boolean);
		if (locationParts.length) parts.push(locationParts.join(" "));

		// Suburb/alternative location descriptor
		if (suburb) parts[parts.length - 1] += ` (${suburb})`;

		return parts.join(", ").trim();
	}

	getStaticMapUrl({
		lat,
		lng,
		zoom = 15,
		width = 400,
		height = 300,
		mapType = "normal"
	  }: StaticMapOptions): string {
		// Base URL for static map service
		let baseUrl = "https://staticmap.openstreetmap.de/staticmap.php";
		
		// Select tile layer based on mapType
		let mapStyle: string;
		switch (mapType) {
		  case "normal":
			mapStyle = "osm";
			break;
		  case "opvn":
			baseUrl = "https://tileserver.memomaps.de/staticmap";
			mapStyle = "tilegen";
			break;
		  case "topo":
			mapStyle = "topo";
			break;
		  case "gray":
			baseUrl = "https://cartodb-basemaps-a.global.ssl.fastly.net/light_all/staticmap";
			mapStyle = "light";
			break;
		  case "sat":
			baseUrl = "https://server.arcgisonline.com/staticmap";
			mapStyle = "World_Imagery";
			break;
		  case "dark":
			baseUrl = "https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/staticmap";
			mapStyle = "dark";
			break;
		  default:
			mapStyle = "osm";
		}
	  
		// Construct the URL with parameters
		const params = new URLSearchParams({
		  center: `${lat},${lng}`,
		  zoom: zoom.toString(),
		  size: `${width}x${height}`,
		  markers: `${lat},${lng},red`,
		  maptype: mapStyle
		});
	  
		return `${baseUrl}?${params.toString()}`;
	  }
}
