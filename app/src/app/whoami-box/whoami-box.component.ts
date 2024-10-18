import { AfterViewInit, Component } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { AuthService } from "@inoa/api";

@Component({
    selector: "gc-whoami-box",
    templateUrl: "./whoami-box.component.html",
    styleUrls: ["./whoami-box.component.css"]
})
export class WhoamiBoxComponent implements AfterViewInit {
    whoami$ = this.authService.whoami();
    indicatorLanguage = "de";
    sessionPercentLeft = 100;
    sessionExpirationDate = new Date();
    sessionStartDate = new Date();
    sessionTimeLeftString = "";

    constructor(
        private authService: AuthService,
        public intercomService: InternalCommunicationService
    ) {
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
        this.whoami$.subscribe(data =>
        {
            console.log("received whoami data");
            console.log(data);

            // set session time data
            this.sessionStartDate = new Date();
            this.sessionExpirationDate = new Date(data.session_expires);
            
        	// give email to the intercom service so all components can see it
        	this.intercomService.userEmail = data.email;
        });
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
