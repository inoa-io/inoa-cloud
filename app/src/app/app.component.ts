import { Component } from "@angular/core";
import { routes } from "./app-routing.module";
import { Router } from "@angular/router";
import { InternalCommunicationService } from "./services/internal-communication-service";

@Component({
    selector: "gc-app-root",
    templateUrl: "./app.component.html",
    styleUrls: ["./app.component.sass"]
})
export class AppComponent {
    title = "INOA Ground Control";
    sideNavOpen = false;
    routes = routes;

    toggleSideBar() {
        this.sideNavOpen = !this.sideNavOpen;
    }

    constructor(private router: Router, public intercomService: InternalCommunicationService) {}

    navbarClick(index: number) {
        this.router.navigate([this.routes[index].path]);
    }
}
