import { AfterViewInit, Component } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";

@Component({
    selector: "gc-whoami-box",
    templateUrl: "./whoami-box.component.html",
    styleUrls: ["./whoami-box.component.css"]
})
export class WhoamiBoxComponent implements AfterViewInit {
    // whoami$ = this.authService.whoami();
    whoami$: any;
    indicatorLanguage = "de";
    sessionPercentLeft = 100;
    sessionExpirationDate = new Date();
    sessionStartDate = new Date();
    sessionTimeLeftString = "";

    constructor(
        // private authService: AuthService,
        public intercomService: InternalCommunicationService
    ) {
        this.sessionExpirationDate.setTime(this.sessionStartDate.getTime() + 300000);
        //recalculates the session time progress every second
        setInterval(() => {
            this.recalcSessionTimeSpinnerProgress();
        }, 1000);
    }

    //this is just to make time calculations work like they should
    adjustForTimezone(date: Date): Date {
        const timeOffsetInMS: number = date.getTimezoneOffset() * 60000;
        date.setTime(date.getTime() + timeOffsetInMS);
        return date;
    }

    ngAfterViewInit() {
        console.log("Whoami Box will be available soon (once Keycloak is implemented)");
        // this.whoami$.subscribe(data =>
        // {
        // 	this.sessionExpirationDate = new Date(data.session_expires);
        // 	this.sessionStartDate = new Date();
        // 	// give email, permissions and other data to the intercom service so all components can see it
        // 	this.intercomService.userEmail = data.email;
        // 	this.intercomService.currentUserPermissions = data.permissions ? data.permissions : [];
        // 	this.intercomService.userIsAdmin = data.is_landlord && data.is_reseller; //only admins have both flags set to true
        // 	this.intercomService.userIsReseller = data.is_reseller && !data.is_landlord;
        // 	this.intercomService.userIsLandlord = data.is_landlord && !data.is_reseller;
        // 	this.intercomService.associatedLandlordId = data.associated_landlord;
        // 	this.intercomService.raisePermissionsLoadedEvent();
        // 	console.log("User " + this.intercomService.userEmail + " has the following permissions:");
        // 	this.intercomService.currentUserPermissions.forEach(permission => { console.log(permission); });
        // });
    }

    //calculates the mini spinner progress to show how much session time is left and the time left for the tooltip
    recalcSessionTimeSpinnerProgress() {
        const timeFormattingOptions: Intl.DateTimeFormatOptions = { hour: "numeric", minute: "numeric", second: "numeric" };

        const nowTime = Date.now();
        const sessionExpirationTime = this.sessionExpirationDate.getTime();
        const sessionStartTime = this.sessionStartDate.getTime();

        const timeLeftInSession = sessionExpirationTime - nowTime;
        const sessionLength = sessionExpirationTime - sessionStartTime;
        const timeInSession = nowTime - sessionStartTime;

        const timeLeftDate = new Date(timeLeftInSession);

        //only show time left while there is actually still time left
        if (timeLeftInSession > 0) {
            this.sessionTimeLeftString = "This session expires in: " + this.adjustForTimezone(timeLeftDate).toLocaleString("de-De", timeFormattingOptions);
            this.sessionPercentLeft = 100 - (timeInSession / sessionLength) * 100;
        } else {
            this.sessionTimeLeftString = "Session expired";
            this.sessionPercentLeft = 0;
        }
    }

    //open user manual in new window
    onUserManualButtonPressed() {
        window.open(window.location + "docs", "_blank");
    }

    //logout
    onLogoutButtonPressed() {
        window.open("/oauth/logout", "_self");
    }

	toggleExpertMode() {
		this.intercomService.expertMode = !this.intercomService.expertMode;

		if (this.intercomService.expertMode) console.log("Expert Mode unlocked!");
		else console.log("Expert Mode locked!");
	}
}
