import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SetupConfiguratorComponent } from "./setup-configurator.component";

describe("SetupConfiguratorComponent", () => {
  let component: SetupConfiguratorComponent;
  let fixture: ComponentFixture<SetupConfiguratorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SetupConfiguratorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SetupConfiguratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
