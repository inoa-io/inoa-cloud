import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { MeasurementCollectorComponent } from "./measurement-collector/measurement-collector.component";
import { SatelliteManagerComponent } from "./satellite-manager/satellite-manager.component";
import { SetupConfiguratorComponent } from "./setup-configurator/setup-configurator.component";
import { InstallationMonitorComponent } from "./installation-monitor/installation-monitor.component";
import { EmsDashboardComponent } from "./ems-dashboard/ems-dashboard.component";
import { SatelliteView3dComponent } from "./satellite-view-3d/satellite-view-3d.component";

export const routes = [
	{
		path: "",
		component: HomeComponent,
		label: "Home",
		type: "static"
	},
	{
		path: "satellite-manager",
		component: SatelliteManagerComponent,
		label: "Manage Satellites",
		type: "wizard",
		subTitle: "Control and maintain your satellites.",
		icon: ""
	},
	{
		path: "measurement-collector",
		component: MeasurementCollectorComponent,
		label: "Collect Measurements",
		type: "wizard",
		subTitle: "Start now to connect new devices and measure data from it.",
		icon: ""
	},
	{
		path: "setup-configurator",
		component: SetupConfiguratorComponent,
		label: "Configure Setups",
		type: "wizard",
		subTitle: "Configure the connected meters, devices and the measurements to read out.",
		icon: ""
	},
	{
		path: "installation-monitor",
		component: InstallationMonitorComponent,
		label: "Monitor Installations",
		type: "wizard",
		subTitle: "View the data measured by your fleet.",
		icon: ""
	},
	{
		path: "ems-dashboard",
		component: EmsDashboardComponent,
		label: "EMS Data",
		type: "wizard",
		subTitle: "View your EMS Dashboard.",
		icon: ""
	},
	{
		path: "satellite-view-3d",
		component: SatelliteView3dComponent,
		label: "Satellite View 3D",
		type: "wizard",
		subTitle: "Monitor online Satellite count in style.",
		icon: ""
	}
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule {}
