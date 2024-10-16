import { Injectable } from "@angular/core";
import { routes } from "../app-routing.module";
import { ActivatedRoute, Router } from "@angular/router";

@Injectable({
    providedIn: "root"
})
export class RoutingService {
    // shows what routing page is selected in the sidebar
    selectedPageIndex = 0;

    routes = routes;

	constructor(
		private router: Router,
		public route: ActivatedRoute
	) {
        const currentUrl = window.location.pathname.split("/").pop();
        const currentIndex = this.routes.findIndex((route) => route.path === currentUrl);

        if (currentIndex !== -1) this.selectedPageIndex = currentIndex;
    }

    navigateName(routeName: string) {
        const routeIndex = this.routes.findIndex((route) => route.path === routeName);
        
        if (routeIndex !== -1) {
            this.selectedPageIndex = routeIndex;
            this.router.navigate([routeName]);
        }
        else console.log("Trying to navigate to unknown route: " + routeName);
    }

    navigateIndex(index: number) {
        this.selectedPageIndex = index;
        this.router.navigate([this.routes[index].path]);
    }

    navigateToSatelliteManager(gatewayId: string) {
        this.router.navigate(["/satellite-manager"], {
            queryParams: { selectedGatewayId: gatewayId }
        });
    }
}
