import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MeasurementCollectorComponent } from "./measurement-collector.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatCardModule} from "@angular/material/card";
import {MatStepperModule} from "@angular/material/stepper";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

describe("MeasurementCollectorComponent", () => {
  let component: MeasurementCollectorComponent;
  let fixture: ComponentFixture<MeasurementCollectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatCardModule,
        MatStepperModule,
        MatAutocompleteModule,
        BrowserAnimationsModule,
      ],
      declarations: [ MeasurementCollectorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MeasurementCollectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
