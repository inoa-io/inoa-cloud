import { AfterViewInit, Component } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";
import { AuthService } from "@inoa/api";
import * as CryptoJS from "crypto-js";

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
    ) { }

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
	
	getGravatarUrl(email: string): string {
		const trimmedEmail = email.trim().toLowerCase();

		return "https://gravatar.com/avatar/" + CryptoJS.SHA256(trimmedEmail).toString();
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
