import { HttpContext } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { GatewaysService, RemoteService, ThingsService } from "@inoa/api";
import { GatewayVO, ThingVO, RpcCommandVO } from "@inoa/model";
import { InternalCommunicationService } from "../internal-communication-service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable, MatTableDataSource } from "@angular/material/table";

const ELEMENT_DATA: GatewayVO[] =
[
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000023", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: true, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000024", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000025", created: "2023-01-01 12:12", enabled: false, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
  { gateway_id: "ISRL011-0000026", created: "2023-01-01 12:12", enabled: true, status: { mqtt: { connected: false, timestamp: "2023-10-10 13:12ß"} }, updated: "2023-01-02 12:13" },
];

const THINGS_DATA: ThingVO[] =
[
  { id: "0001", name: "DZG", gateway_id: "ISRL011-0000001", thing_type_id: "DZG", config: { }, created: "2023-01-01 12:12", updated: "2023-01-02 12:13"},
  { id: "0001", name: "DZG", gateway_id: "ISRL011-0000001", thing_type_id: "DZG", config: { }, created: "2023-01-01 12:12", updated: "2023-01-02 12:13"},
  { id: "0001", name: "MyStrom", gateway_id: "ISRL011-0000001", thing_type_id: "MyStrom", config: { }, created: "2023-01-01 12:12", updated: "2023-01-02 12:13"},
  { id: "0001", name: "DZG", gateway_id: "ISRL011-0000001", thing_type_id: "DZG", config: { }, created: "2023-01-01 12:12", updated: "2023-01-02 12:13"},
]

@Component({
  selector: "gc-satellite-manager",
  templateUrl: "./satellite-manager.component.html",
  styleUrls: ["./satellite-manager.component.css"]
})
export class SatelliteManagerComponent implements AfterViewInit, OnInit
{
  @ViewChild("sortRef") sort!: MatSort;
	@ViewChild("paginatorRef") paginator!: MatPaginator;
	@ViewChild("tableRef") table!: MatTable<GatewayVO>;

  displayedColumnsGatewayTable: string[] = ["gateway_id", "name", "enabled", "status", "actions", "created", "updated"];
  displayedColumnsThingsTable: string[] = ["id", "gateway_id", "thing_type_id", "name", "created", "updated"];

  dataSourceGateways = new MatTableDataSource<GatewayVO>(ELEMENT_DATA);
  dataSourceThings = new MatTableDataSource<ThingVO>(THINGS_DATA);

  selectedSatellite: GatewayVO | undefined;
  selectedThing: ThingVO | undefined;

  selectedTabIndex = 0;

  httpResult: any;

  functions = [];
  function = "";

  jsonCode = "{ \"key\": \"value\" }";

  monacoOptions =
  {
    theme: "vs-dark",
    language: "json",
    automaticLayout: true,
    fontSize: 14,
    scrollBeyondLastLine: false
  };

  measurements = this.formBuilder.group(
  {
    water: false,
    heat: false,
    power: true,
  });

  constructor(
    private formBuilder: FormBuilder,
    private gatewaysService: GatewaysService,
    private thingsService: ThingsService,
    private remoteService: RemoteService,
    public intercomService: InternalCommunicationService)
  { this.selectedSatellite = undefined; }

  ngOnInit()
  {
    this.gatewaysService.findGateways().subscribe(data => { this.dataSourceGateways.data = data.content; });
  }

  ngAfterViewInit()
  {
    this.dataSourceGateways.paginator = this.paginator;
    this.dataSourceGateways.sort = this.sort;
  }

  rowClicked(satellite: GatewayVO)
  {
    this.selectedTabIndex = 1;
    this.selectedSatellite = satellite;

    this.thingsService.findThingsByGatewayId(satellite.gateway_id).subscribe(data => { this.dataSourceThings.data = data.content; });
  }

  openConsoleClick(index: number, event: Event)
  {
    event.stopPropagation();

    this.selectedSatellite = this.dataSourceGateways.data[index];
    this.selectedTabIndex = 2;
  }

  restartClick(gateway: GatewayVO, event: Event)
  {
    event.stopPropagation();
    //TODO: add restart command here
  }

  isRowSelected(rowData: GatewayVO)
  {
    if(!this.selectedSatellite) return false;

    if(this.selectedSatellite.created !== rowData.created) return false;
    if(this.selectedSatellite.updated !== rowData.updated) return false;
    if(this.selectedSatellite.name !== rowData.name) return false;
    if(this.selectedSatellite.gateway_id !== rowData.gateway_id) return false;
    if(this.selectedSatellite.status?.mqtt?.connected !== rowData.status?.mqtt?.connected) return false;
    if(this.selectedSatellite.status?.mqtt?.timestamp !== rowData.status?.mqtt?.timestamp) return false;

    return true;
  }

  sendRPC()
  {
    if(this.selectedSatellite)
    {
      console.log("%cTrying to send Json-Code:", "color: lime;");
      console.log(this.jsonCode);
      console.log();

      const options = { httpHeaderAccept: "application/json" as const, context: new HttpContext() };
      const rpcCommand: RpcCommandVO = { id: "1", method: "unknown", params: JSON.parse(this.jsonCode) }

      this.remoteService.sendRpcCommand(this.selectedSatellite.gateway_id, rpcCommand, undefined, undefined, options)
      .subscribe((response) =>
      {
        console.log("%cGot respone from: " + response.id, "color: lightblue;");
        if(response.error) console.log("%cSomething went wrong: " + response.error.code + " | " + response.error.message, "color: orange;");
        if(response.result) console.log("This is the result: " + response.result);
      });
    }
  }

  cellClickThings(thing: ThingVO) { this.selectedThing = thing; }
  closeDetailsView() { this.selectedSatellite = undefined; }
  closeMeasurementView() { this.selectedThing = undefined; }
  createThingDialog()
  {
    //TODO: Fill with thing creation. Use the one we already have on the measurement collector
    console.log("Nothing here yet");
  }
}
