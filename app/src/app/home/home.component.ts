import { Component } from "@angular/core";
import { RoutingService } from "../services/routing-service";

@Component({
	selector: "gc-app-home",
	templateUrl: "./home.component.html",
	styleUrls: ["./home.component.css"]
})
export class HomeComponent {
	constructor(public routingService: RoutingService) {}
}
