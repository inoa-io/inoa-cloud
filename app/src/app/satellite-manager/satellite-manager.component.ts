import { HttpContext, HttpErrorResponse } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { GatewaysService, GatewayUpdateVO, GatewayVO, RemoteService, RpcCommandVO, ThingsService, ThingVO } from "@inoa/api";
import { InternalCommunicationService } from "../internal-communication-service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { RpcRestService } from "../rpc-rest-service";

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

  dataSourceGateways = new MatTableDataSource<GatewayVO>;
  dataSourceThings = new MatTableDataSource<ThingVO>;

  selectedSatellite: GatewayVO | undefined;
  selectedThing: ThingVO | undefined;

  selectedTabIndex = 0;

  functions = [];
  function = "";

  jsonCode = "{\"id\": \"value\", \"method\": \"sys.wink\" }";

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
    private rpcRestService: RpcRestService,
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
    //TODO: add mqtt restart command here and remove the rest version
    this.rpcRestService.sendRpcReboot(gateway.gateway_id);
  }

  toggleEnabledClick(gateway: GatewayVO, event: Event, enable: boolean)
  {
    event.stopPropagation();

    const updateData: GatewayUpdateVO = { enabled: enable }

    this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => gateway.enabled = returnData.enabled);
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
      const options = { httpHeaderAccept: "application/json" as const, context: new HttpContext() };
      const rpcCommand: RpcCommandVO = JSON.parse(this.jsonCode);

      console.log("%cTrying to send Json-Code:", "color: lime;");
      console.log(rpcCommand);
      console.log();

      // this.remoteService.sendRpcCommand(this.selectedSatellite.gateway_id, rpcCommand, undefined, undefined, options)
      // .subscribe((response) =>
      // {
      //   console.log("%cGot respone from: " + response.id, "color: lightblue;");
      //   if(response.error) console.log("%cSomething went wrong: " + response.error.code + " | " + response.error.message, "color: orange;");
      //   if(response.result) console.log("This is the result: " + response.result);
      // });

      this.rpcRestService.sendRpcCommand(this.selectedSatellite.gateway_id, rpcCommand)
        .subscribe((response) => console.log("Respone: " + JSON.stringify(response)));
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