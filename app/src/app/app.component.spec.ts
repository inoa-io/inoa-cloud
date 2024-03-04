import { TestBed } from "@angular/core/testing";
import { AppComponent } from "./app.component";
import {AppModule} from "./app.module";
import {RouterTestingModule} from "@angular/router/testing";

describe("AppComponent", () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppModule,
        RouterTestingModule
      ],
      declarations: [
        AppComponent
      ],
    }).compileComponents();
  });

  it("should create the app", () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it("should have as title 'INOA Ground Control'", () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual("INOA Ground Control");
  });

  it("should render title", () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const app = fixture.componentInstance;
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector("#title")?.textContent).toContain(app.title);
  });
});
