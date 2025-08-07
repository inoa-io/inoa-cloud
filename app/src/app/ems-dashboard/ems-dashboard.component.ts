import { Component, OnInit } from "@angular/core";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";

@Component({
  selector: "gc-ems-dashboard",
  templateUrl: "./ems-dashboard.component.html",
  styleUrl: "./ems-dashboard.component.css"
})
export class EmsDashboardComponent implements OnInit {
  safeUrl: SafeResourceUrl | null = null;
  constructor(private sanitizer: DomSanitizer) { }
  
  ngOnInit() {
    const dynamicUrl = this.getGrafanaDashboardUrl();
    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(dynamicUrl);
  }
  getGrafanaDashboardUrl(): string {
    const currentUrl = window.location.href;
    const newUrl = currentUrl.replace("inoa", "grafana");
    const baseUrl = newUrl.split("/")[0] + "//" + newUrl.split("/")[2];

    const dashboardUrl = baseUrl + "/d/aed25ae6-bcfd-4d5b-9571-adad2755e486/ems-power-data?orgId=1&refresh=15m&theme=light&kiosk&from=now-3h&to=now";

    console.log("This should be the Grafana Dashboard Url: " + dashboardUrl);

    return dashboardUrl;
  }
}
