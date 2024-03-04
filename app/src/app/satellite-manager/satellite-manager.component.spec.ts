import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SatelliteManagerComponent } from "./satellite-manager.component";

describe("SatelliteManagerComponent", () => {
  let component: SatelliteManagerComponent;
  let fixture: ComponentFixture<SatelliteManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SatelliteManagerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SatelliteManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
