import {HttpClientModule} from "@angular/common/http";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {MatCardModule} from "@angular/material/card";
import {MatIconModule} from "@angular/material/icon";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSortModule} from "@angular/material/sort";
import {MatTableModule} from "@angular/material/table";
import {MatTabsModule} from "@angular/material/tabs";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {RpcHistoryComponent} from "./rpc-history-panel.component";

describe("SatelliteManagerComponent", () => {
  let component: RpcHistoryComponent;
  let fixture: ComponentFixture<RpcHistoryComponent>;

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
        BrowserAnimationsModule
      ],
      declarations: [ RpcHistoryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RpcHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
