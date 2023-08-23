import {Component} from "@angular/core";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ThingsService} from "../gen/api/things.service";

@Component({
	selector: "gc-root",
	templateUrl: "./app.component.html",
	styleUrls: ["./app.component.sass"]
})
export class AppComponent {

	constructor(private thingsService: ThingsService, private formBuilder: FormBuilder) {
	}

}
