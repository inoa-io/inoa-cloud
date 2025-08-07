import { AfterContentChecked, ChangeDetectorRef, Component } from "@angular/core";
import { InternalCommunicationService } from "./services/internal-communication-service";
import { RoutingService } from "./services/routing-service";

@Component({
	selector: "gc-app-root",
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.sass"]
})
export class AppComponent implements AfterContentChecked {
	title = "INOA Ground Control";
	sideNavOpen = false;
	httpDataLoading = false;

	constructor(
		public routingService: RoutingService,
		public intercomService: InternalCommunicationService,
		private cdr: ChangeDetectorRef
	) {}

	ngAfterContentChecked(): void {
		this.httpDataLoading = this.intercomService.httpDataLoading;
		this.cdr.detectChanges();
	}

	toggleSideBar() {
		this.sideNavOpen = !this.sideNavOpen;
	}
}
