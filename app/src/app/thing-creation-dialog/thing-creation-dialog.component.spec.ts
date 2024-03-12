import {HttpClientTestingModule} from "@angular/common/http/testing";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GatewayVO, ThingTypeVO, ThingVO} from "@inoa/model";
import {ThingCreationDialogComponent, ThingCreationDialogData} from "./thing-creation-dialog.component";

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

    const sampleThing: ThingVO = {
      id: "sample_thing_id",
      name: "Sample Thing",
      gateway_id: "sample_gateway_id",
      thing_type_id: "sample_thing_type_id",
      config: { key1: "value1", key2: "value2" },
      created: "2022-01-01",
      updated: "2022-01-01",
    };

    const sampleThingType: ThingTypeVO = {
      id: "sample_thing_type_id",
      name: "Sample Thing Type",
      created: "2022-01-01",
      updated: "2022-01-01",
    };

    const sampleDialogData: ThingCreationDialogData = {
      gateway: sampleGateway,
      thing: sampleThing,
      thingTypes: [sampleThingType],
    };

    await TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      declarations: [ ThingCreationDialogComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: sampleDialogData }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ThingCreationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
