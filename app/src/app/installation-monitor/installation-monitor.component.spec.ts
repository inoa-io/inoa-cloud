import { ComponentFixture, TestBed } from "@angular/core/testing";
import { MatCardModule } from "@angular/material/card";

import { InstallationMonitorComponent } from "./installation-monitor.component";

describe("InstallationMonitorComponent", () => {
  let component: InstallationMonitorComponent;
  let fixture: ComponentFixture<InstallationMonitorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatCardModule
      ],
      declarations: [ InstallationMonitorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InstallationMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
