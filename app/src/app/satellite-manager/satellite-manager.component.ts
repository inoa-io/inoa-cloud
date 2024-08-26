import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { GatewaysService, GatewayUpdateVO, GatewayVO, RemoteService, RpcCommandVO, ThingsService, ThingVO } from "@inoa/api";
import { InternalCommunicationService } from "../internal-communication-service";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { RpcMqttService } from "../rpc-mqtt-service";
import { RpcExchange, RpcHistoryService } from "../rpc-history-panel/rpc-history-service";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { interval, Subscription, switchMap } from "rxjs";

@Component({
  selector: "gc-satellite-manager",
  templateUrl: "./satellite-manager.component.html",
  styleUrls: ["./satellite-manager.component.css"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({height: "0px", minHeight: "0"})),
      state("expanded", style({height: "*"})),
      transition("expanded <=> collapsed", animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")),
    ]),
  ]
})
export class SatelliteManagerComponent implements AfterViewInit, OnInit, OnDestroy
{
  @ViewChild("sortRef") sort!: MatSort;
	@ViewChild("paginatorRef") paginator!: MatPaginator;
  @ViewChild("tableRef") table!: MatTable<GatewayVO>;
  
  expandedElement: RpcExchange | null = null;

  displayedColumnsRpcHistory: string[] = ["method", "id", "status"];
  displayedColumnsGatewayTable: string[] = ["gateway_id", "name", "enabled", "status", "actions"];
  displayedColumnsThingsTable: string[] = ["id", "gateway_id", "thing_type_id", "name"];

  dataSourceGateways = new MatTableDataSource<GatewayVO>;
  dataSourceThings = new MatTableDataSource<ThingVO>;

  selectedSatellite: GatewayVO | undefined;
  selectedThing: ThingVO | undefined;

  selectedTabIndex = 0;

  jsonCode = "{ \"method\": \"rpc.list\" }";

  monacoOptions = {
    theme: "vs-dark",
    language: "json",
    automaticLayout: true,
    fontSize: 18,
    scrollBeyondLastLine: false,
    minimap: { enabled: false }
  };

  measurements = this.formBuilder.group({
    water: false,
    heat: false,
    power: true,
  });

  private autoTableRefresher!: Subscription;
  autoRefreshIntervals = ["Off", "1s", "5s", "10s", "30s"];
  autoRefreshInterval = "5s";

  constructor(
    private formBuilder: FormBuilder,
    private gatewaysService: GatewaysService,
    private thingsService: ThingsService,
    private remoteService: RemoteService,
    private rpcMqttService: RpcMqttService,
    public rpcHistoryService: RpcHistoryService,
    public intercomService: InternalCommunicationService,
    private changeDetector: ChangeDetectorRef
  ) {
      this.selectedSatellite = undefined;
  }

  ngOnInit() {
    this.startAutoRefresh();
  }

  startAutoRefresh() {
    // Cancel existing auto-refresh subscription if it exists
    if (this.autoTableRefresher) { this.autoTableRefresher.unsubscribe(); }

    // Always make an initial API call
    this.gatewaysService.findGateways().subscribe(data => { this.dataSourceGateways.data = data.content; });

    // Parse autoRefreshInterval
    const autoRefreshInterval = this.autoRefreshInterval === "Off" ? 0 : parseInt(this.autoRefreshInterval.substring(0, this.autoRefreshInterval.length - 1)) * 1000;

    // If autoRefreshInterval is  0, don't set up interval
    if (autoRefreshInterval === 0) return;

    // otherwise do set up an interval
    this.autoTableRefresher = interval(autoRefreshInterval).pipe(
      switchMap(() => this.gatewaysService.findGateways())
    ).subscribe(data => { this.dataSourceGateways.data = data.content; });
  }

  onIntervalChange() {
    this.startAutoRefresh();
  }

  ngOnDestroy() {
      this.autoTableRefresher.unsubscribe();
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
    this.rpcMqttService.sendRpcReboot(gateway.gateway_id);
  }

  winkClick(gateway: GatewayVO, event: Event)
  {
    event.stopPropagation();
    this.rpcMqttService.sendRpcWink(gateway.gateway_id);
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

  sendRPC() {
    if (this.selectedSatellite) {
      const rpcCommand: RpcCommandVO = JSON.parse(this.jsonCode);
      
      this.rpcMqttService.sendRpcCommand(this.selectedSatellite.gateway_id, rpcCommand);
      this.changeDetector.detectChanges();
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