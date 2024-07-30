import {
	ApiModule as FleetApiModule, BASE_PATH as FleetBasePath,
	Configuration as FleetConfiguration,
	ConfigurationParameters as FleetConfigurationParameters,
} from "@inoa/api";
import {NgModule} from "@angular/core";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatSelectModule} from "@angular/material/select";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {RouterModule} from "@angular/router";
import {HomeComponent} from "./home/home.component";
import {MeasurementCollectorComponent} from "./measurement-collector/measurement-collector.component";
import {SatelliteManagerComponent} from "./satellite-manager/satellite-manager.component";
import {InstallationMonitorComponent} from "./installation-monitor/installation-monitor.component";
import {SetupConfiguratorComponent} from "./setup-configurator/setup-configurator.component";
import {MatCardModule} from "@angular/material/card";
import {FilterPipe} from "./filter.pipe";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatStepperModule} from "@angular/material/stepper";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatRadioModule} from "@angular/material/radio";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {HttpClientModule} from "@angular/common/http";
import {MatDialogModule} from "@angular/material/dialog";
import {ThingCreationDialogComponent} from "./thing-creation-dialog/thing-creation-dialog.component";
import {environment} from "../environments/environment.production";
import {AppRoutingModule} from "./app-routing.module";
import {AppComponent} from "./app.component";
import {MatTableModule} from "@angular/material/table";
import {MatTabsModule} from "@angular/material/tabs";
import {FormlyModule} from "@ngx-formly/core";
import {FormlyMaterialModule} from "@ngx-formly/material";
import {ObjectTypeComponent} from "./thing-creation-dialog/object.type";
import {NullTypeComponent} from "./thing-creation-dialog/null.type";
import {ArrayTypeComponent} from "./thing-creation-dialog/array.type";
import {MultiSchemaTypeComponent} from "./thing-creation-dialog/multischema.type";
import {MonacoEditorModule} from "ngx-monaco-editor";


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
		MeasurementCollectorComponent,
		SatelliteManagerComponent,
		InstallationMonitorComponent,
		SetupConfiguratorComponent,
		FilterPipe,
		ThingCreationDialogComponent,
        ArrayTypeComponent, ObjectTypeComponent, MultiSchemaTypeComponent, NullTypeComponent
	],
  imports: [
    MonacoEditorModule.forRoot(),
    FleetApiModule.forRoot(fleetApiConfigFactory),
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatSidenavModule,
    MatListModule,
    MatCardModule,
    MatGridListModule,
    MatStepperModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule,
    RouterModule,
    AppRoutingModule,
    MatRadioModule,
    MatButtonToggleModule,
    HttpClientModule,
    MatDialogModule,
    FormsModule,
    MatTableModule,
    MatTabsModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule,
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
              { name: "multischema", component: MultiSchemaTypeComponent },
          ],
      }),
    FormlyMaterialModule
  ],
    providers: [
        {provide: FleetBasePath, useValue: environment.fleetApiBasePath},
    ],
    bootstrap: [AppComponent]
})
export class AppModule {}
