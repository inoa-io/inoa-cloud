import {HttpClientModule} from "@angular/common/http";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";
import {MatTabsModule} from "@angular/material/tabs";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {SatelliteManagerComponent} from "./satellite-manager.component";
import {FormsModule} from "@angular/forms";

describe("SatelliteManagerComponent", () => {
  let component: SatelliteManagerComponent;
  let fixture: ComponentFixture<SatelliteManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatTabsModule,
        MatCardModule,
        MatSortModule,
        MatPaginatorModule,
        MatTableModule,
        MatIconModule,
        MatSelectModule,
        MatFormFieldModule,
        BrowserAnimationsModule,
        FormsModule
      ],
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
