import { ApiModule as FleetApiModule, BASE_PATH as FleetBasePath, Configuration as FleetConfiguration, ConfigurationParameters as FleetConfigurationParameters } from "@inoa/api";
import { environment } from "../environments/environment.production";
import { FilterPipe } from "./filter.pipe";

import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { RouterModule } from "@angular/router";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { ErrorHandlingModule } from "./error-handling/error-handling.module";
import { AppRoutingModule } from "./app-routing.module";
import { MonacoEditorModule } from "ngx-monaco-editor";
import { MaterialModule } from "./material.module";

import { AppComponent } from "./app.component";
import { HomeComponent } from "./home/home.component";
import { MeasurementCollectorComponent } from "./measurement-collector/measurement-collector.component";
import { SatelliteManagerComponent } from "./satellite-manager/satellite-manager.component";
import { InstallationMonitorComponent } from "./installation-monitor/installation-monitor.component";
import { SetupConfiguratorComponent } from "./setup-configurator/setup-configurator.component";
import { EmsDashboardComponent } from "./ems-dashboard/ems-dashboard.component";

import { RpcHistoryComponent } from "./satellite-manager/gateway-detail/rpc-history-panel/rpc-history-panel.component";
import { GatewayTableComponent } from "./satellite-manager/gateway-table/gateway-table.component";
import { GatewayDetailComponent } from "./satellite-manager/gateway-detail/gateway-detail.component";
import { GatewayConfigurationComponent } from "./satellite-manager/gateway-detail/gateway-configuration/gateway-configuration.component";
import { GatewayDatapointsComponent } from "./satellite-manager/gateway-detail/gateway-datapoints/gateway-datapoints.component";
import { GatewayOverviewComponent } from "./satellite-manager/gateway-detail/gateway-overview/gateway-overview.component";
import { GatewayFleetMapComponent } from "./satellite-manager/gateway-detail/gateway-fleet-map/gateway-fleet-map.component";

import { ThingDialogComponent } from "./dialogs/thing-dialog/thing-dialog.component";
import { RenameSatelliteDialogComponent } from "./dialogs/rename-satellite-dialog/rename-satellite-dialog.component";
import { ConfigEditDialogComponent } from "./dialogs/config-edit-dialog/config-edit-dialog.component";
import { LocationEditorDialogComponent } from "./dialogs/location-editor-dialog/location-editor-dialog.component";
import { WhoamiBoxComponent } from "./whoami-box/whoami-box.component";

export function fleetApiConfigFactory(): FleetConfiguration {
    const params: FleetConfigurationParameters = {
        basePath: environment.fleetApiBasePath,
        accessToken: environment.fleetApiToken
    };
    return new FleetConfiguration(params);
}
@NgModule({
    declarations: [
        FilterPipe,
        AppComponent,
        HomeComponent,
        MeasurementCollectorComponent,
        SatelliteManagerComponent,
        InstallationMonitorComponent,
        SetupConfiguratorComponent,
        EmsDashboardComponent,
        RpcHistoryComponent,
        GatewayTableComponent,
        GatewayDetailComponent,
        GatewayConfigurationComponent,
        GatewayDatapointsComponent,
        GatewayOverviewComponent,
        GatewayFleetMapComponent,
        ThingDialogComponent,
        RenameSatelliteDialogComponent,
        ConfigEditDialogComponent,
        LocationEditorDialogComponent,
        WhoamiBoxComponent
    ],
    imports: [
        MonacoEditorModule.forRoot(),
        FleetApiModule.forRoot(fleetApiConfigFactory),
        BrowserModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        RouterModule,
        AppRoutingModule,
        MaterialModule,
        ErrorHandlingModule,
        HttpClientModule,
        FormsModule,
        MaterialModule
    ],
    providers: [{ provide: FleetBasePath, useValue: environment.fleetApiBasePath }],
    bootstrap: [AppComponent]
})
export class AppModule {}
