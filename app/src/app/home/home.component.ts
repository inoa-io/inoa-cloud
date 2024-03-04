import { Component } from "@angular/core";
import { Router } from "@angular/router";
import { InternalCommunicationService } from "../internal-communication-service";

@Component({
  selector: "gc-app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"]
})
export class HomeComponent
{
  constructor(private router: Router, private intercomService: InternalCommunicationService) { }

  navigate(route: string)
  {
    this.intercomService.pageSelect = route;
    this.router.navigate([route]);
  }
}

