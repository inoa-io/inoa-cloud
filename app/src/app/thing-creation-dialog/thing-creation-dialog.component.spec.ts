import { HttpClientTestingModule } from "@angular/common/http/testing";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { GatewayVO, ThingCreateVO, ThingTypeVO } from "@inoa/model";
import { ThingCreationDialogComponent, ThingCreationDialogData } from "./thing-creation-dialog.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { FormlyModule } from "@ngx-formly/core";
import { ReactiveFormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

describe("ThingCreationDialogComponent", () => {
  let component: ThingCreationDialogComponent;
  let fixture: ComponentFixture<ThingCreationDialogComponent>;

  beforeEach(async () => {
    const sampleGateway: GatewayVO = {
      gateway_id: "sample_gateway_id",
      name: "Sample Gateway",
      enabled: true,
      created: "2022-01-01",
      updated: "2022-01-01",
    };

    const sampleCreateThing: ThingCreateVO = {
      thing_type_id: "sample_thing_type_id",
      name: "Sample Thing",
      gateway_id: "sample_gateway_id",
      config: { key1: "value1", key2: "value2" }
    }

    const sampleThingType: ThingTypeVO = {
      id: "sample_thing_type_id",
      name: "Sample Thing Type",
      created: "2022-01-01",
      updated: "2022-01-01",
    };

    const sampleDialogData: ThingCreationDialogData = {
      gateway: sampleGateway,
      thing: sampleCreateThing,
      thingTypes: [sampleThingType],
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        MatSelectModule,
        FormlyModule.forRoot(),
        ReactiveFormsModule,
        MatDialogModule,
        BrowserAnimationsModule
      ],
      declarations: [ThingCreationDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: sampleDialogData }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ThingCreationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
