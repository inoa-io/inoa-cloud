import {HttpClientModule} from "@angular/common/http";
import {NgModule} from "@angular/core";
import {ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {BrowserModule} from "@angular/platform-browser";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";
import {environment} from "../environments/environment.production";
import {BASE_PATH} from "../gen";
import {AppRoutingModule} from "./app-routing.module";
import {AppComponent} from "./app.component";

@NgModule({
	declarations: [
		AppComponent
	],
	imports: [
		BrowserModule,
		HttpClientModule,
		AppRoutingModule,
		ReactiveFormsModule,
		NoopAnimationsModule,
		MatFormFieldModule,
		MatInputModule,
		MatSelectModule
	],
	providers: [{provide: BASE_PATH, useValue: environment.backendUri}],
	bootstrap: [AppComponent]
})
export class AppModule {}
