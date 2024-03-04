import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ThingCreationDialogComponent } from "./thing-creation-dialog.component";

describe("ThingCreationDialogComponent", () => {
  let component: ThingCreationDialogComponent;
  let fixture: ComponentFixture<ThingCreationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThingCreationDialogComponent ]
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
