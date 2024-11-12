import { Component } from "@angular/core";

@Component({
  selector: "gc-ems-dashboard",
  templateUrl: "./ems-dashboard.component.html",
  styleUrl: "./ems-dashboard.component.css"
})
export class EmsDashboardComponent {
  getGrafanaDashboardUrl(): string {

    let currentUrl = window.location.href;
    let newUrl = currentUrl.replace('inoa', 'grafana');
    let baseUrl = newUrl.split('/')[0] + '//' + newUrl.split('/')[2];

    const dashboardUrl = baseUrl + "/d/aed25ae6-bcfd-4d5b-9571-adad2755e486/ems-power-data?orgId=1&refresh=15m&theme=light&kiosk";

    console.log("This should be the Grafana Dashboard Url: " + dashboardUrl);

    return dashboardUrl;
  }
}
