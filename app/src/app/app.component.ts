import { Component } from "@angular/core";
import { InternalCommunicationService } from "./services/internal-communication-service";
import { RoutingService } from "./services/routing-service";

@Component({
	selector: "gc-app-root",
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.sass"]
})
export class AppComponent {
	title = "INOA Ground Control";
	sideNavOpen = false;

	toggleSideBar() {
		this.sideNavOpen = !this.sideNavOpen;
	}

	constructor(public routingService: RoutingService, public intercomService: InternalCommunicationService) {}
}
