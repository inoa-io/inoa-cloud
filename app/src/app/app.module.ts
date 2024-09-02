import { ApiModule as FleetApiModule, BASE_PATH as FleetBasePath, Configuration as FleetConfiguration, ConfigurationParameters as FleetConfigurationParameters } from "@inoa/api";
import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { RouterModule } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { MeasurementCollectorComponent } from "./measurement-collector/measurement-collector.component";
import { SatelliteManagerComponent } from "./satellite-manager/satellite-manager.component";
import { InstallationMonitorComponent } from "./installation-monitor/installation-monitor.component";
import { SetupConfiguratorComponent } from "./setup-configurator/setup-configurator.component";
import { FilterPipe } from "./filter.pipe";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";
import { ThingCreationDialogComponent } from "./thing-creation-dialog/thing-creation-dialog.component";
import { ErrorHandlingModule } from "./error-handling/error-handling.module";
import { environment } from "../environments/environment.production";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { FormlyModule } from "@ngx-formly/core";
import { FormlyMaterialModule } from "@ngx-formly/material";
import { ObjectTypeComponent } from "./thing-creation-dialog/object.type";
import { NullTypeComponent } from "./thing-creation-dialog/null.type";
import { ArrayTypeComponent } from "./thing-creation-dialog/array.type";
import { MultiSchemaTypeComponent } from "./thing-creation-dialog/multischema.type";
import { MonacoEditorModule } from "ngx-monaco-editor";
import { RpcHistoryComponent } from "./rpc-history-panel/rpc-history-panel.component";
import { MaterialModule } from "./material.module";

export function fleetApiConfigFactory(): FleetConfiguration {
    const params: FleetConfigurationParameters = {
        basePath: environment.fleetApiBasePath,
        accessToken: environment.fleetApiToken
    };
    return new FleetConfiguration(params);
}
@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        RpcHistoryComponent,
        MeasurementCollectorComponent,
        SatelliteManagerComponent,
        InstallationMonitorComponent,
        SetupConfiguratorComponent,
        FilterPipe,
        ThingCreationDialogComponent,
        ArrayTypeComponent,
        ObjectTypeComponent,
        MultiSchemaTypeComponent,
        NullTypeComponent
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
        MaterialModule,
        FormlyModule.forRoot({
            // validationMessages: [
            //     { name: 'required', message: 'This field is required' },
            //     { name: 'uniqueItems', message: 'should NOT have duplicate items' },
            //     { name: 'enum', message: `must be equal to one of the allowed values` },
            // ],
            types: [
                { name: "null", component: NullTypeComponent, wrappers: ["form-field"] },
                { name: "array", component: ArrayTypeComponent },
                { name: "object", component: ObjectTypeComponent },
                { name: "multischema", component: MultiSchemaTypeComponent }
            ]
        }),
        FormlyMaterialModule
    ],
    providers: [{ provide: FleetBasePath, useValue: environment.fleetApiBasePath }],
    bootstrap: [AppComponent]
})
export class AppModule {}
