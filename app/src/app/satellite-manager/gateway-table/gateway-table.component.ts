import { SelectionModel } from "@angular/cdk/collections";
import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { GatewaysService, TenantsService } from "@inoa/api";
import { GatewayUpdateVO, GatewayVO } from "@inoa/model";
import { interval, of, Subscription, switchMap } from "rxjs";
import { DialogService } from "src/app/services/dialog-service";
import { InternalCommunicationService } from "src/app/services/internal-communication-service";
import { RoutingService } from "src/app/services/routing-service";
import { RpcMqttService } from "src/app/services/rpc-mqtt-service";

@Component({
	selector: "gc-gateway-table",
	templateUrl: "./gateway-table.component.html",
	styleUrl: "./gateway-table.component.css"
})
export class GatewayTableComponent implements AfterViewInit, OnInit, OnDestroy {
	@ViewChild("sortRef") sort!: MatSort;
	@ViewChild("paginatorRef") paginator!: MatPaginator;
	@ViewChild("tableRef") table!: MatTable<GatewayVO>;

	dataSourceGateways = new MatTableDataSource<GatewayVO>();
	selectionGateways = new SelectionModel<GatewayVO>(true, []);
	displayedColumnsGatewayTable: string[] = ["gateway_id", "name", "tenant", "filler", "enabled", "status", "actions"];
	displayedColumnsGatewayTableMini: string[] = ["id_name_combo"];

	private autoTableRefresher!: Subscription;
	autoRefreshIntervals = ["Off", "1s", "5s", "10s", "30s"];
	autoRefreshInterval = "30s";

	constructor(
		public intercomService: InternalCommunicationService,
		private gatewaysService: GatewaysService,
		private dialogService: DialogService,
		private rpcMqttService: RpcMqttService,
		private tenantService: TenantsService,
		private routingService: RoutingService
	) {}

	ngOnDestroy() {
		this.autoTableRefresher.unsubscribe();
	}

	ngAfterViewInit() {
		this.dataSourceGateways.paginator = this.paginator;
		this.dataSourceGateways.sort = this.sort;
	}

	ngOnInit() {
		this.startAutoRefresh();

		// get all tenants from the database
		this.tenantService.findTenants().subscribe((tenantList) => {
			this.intercomService.tenantList = tenantList;
		});

		// get the gateway id parameter and ensure data is loaded
		this.routingService.route.queryParamMap
			.pipe(
				switchMap((params) => {
					// get gateway id from query parameter
					this.intercomService.selectedGatewayId = params.get("selectedGatewayId");

					// Check if dataSourceGateways.data is empty before fetching new data
					if (this.dataSourceGateways.data.length === 0) {
						// Fetch gateways here
						return this.gatewaysService.findGateways();
					} else {
						// If data is already loaded, return an empty observable
						return of({ content: this.dataSourceGateways.data });
					}
				})
			)
			.subscribe((data) => {
				// populate the data source first
				this.dataSourceGateways.data = data.content;

				// find gateway via id we got from the query parameter
				this.intercomService.selectedGateway = this.dataSourceGateways.data.find((gateway) => gateway.gateway_id === this.intercomService.selectedGatewayId);

				// if no gateway with that id was found, navigate to the main table
				if (!this.intercomService.selectedGateway) this.routingService.navigateName("satellite-manager");
				else this.intercomService.raiseSelectedGatewayChangedEvent();
			});
	}

	/** Whether the number of selected elements matches the total number of rows. */
	isAllSelected() {
		const numSelected = this.selectionGateways.selected.length;
		const numRows = this.dataSourceGateways.data.length;
		return numSelected === numRows;
	}

	/** Selects all rows if they are not all selected; otherwise clear selection. */
	toggleAllRows() {
		if (this.isAllSelected()) {
			this.selectionGateways.clear();
			return;
		}

		this.selectionGateways.select(...this.dataSourceGateways.data);
	}

	/** The label for the checkbox on the passed row */
	checkboxLabel(row?: GatewayVO): string {
		if (!row) {
			return `${this.isAllSelected() ? "deselect" : "select"} all`;
		}
		return `${this.selectionGateways.isSelected(row) ? "deselect" : "select"} row ${row.gateway_id}`;
	}
	applyFilter(filterValue: string | null) {
		const filter = filterValue?.trim().toLowerCase() || "";

		this.dataSourceGateways.filter = filter;

		// Update the paginators after applying the filter
		if (this.dataSourceGateways.paginator) {
			this.dataSourceGateways.paginator.length = this.dataSourceGateways.filteredData.length;
		}
	}
	// does not work yet since gatewayVO does not yet have an associated tenantID but once that is taken care of this will return the correct name
	getTenantNameById(id: string): string {
		const tenant = this.intercomService.tenantList.find((tenant) => tenant.tenant_id === id);

		return tenant ? tenant.name : "-";
	}

	toggleEnabledClick(gateway: GatewayVO, event: Event, enable: boolean) {
		event.stopPropagation();

		const updateData: GatewayUpdateVO = { enabled: enable };

		this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((returnData) => {
			gateway.enabled = returnData.enabled;
		});
	}

	renameSatelliteClick(gateway: GatewayVO | undefined, event: Event) {
		event.stopPropagation();

		if (!gateway) return;

		this.dialogService
			.openRenameSatelliteDialog(gateway.name ? gateway.name : "")
			?.afterClosed()
			.subscribe((data) => {
				this.updateSatelliteName(gateway, data.name);
			});
	}

	updateSatelliteName(gateway: GatewayVO, name: string) {
		const updateData: GatewayUpdateVO = {
			name: name
		};

		this.gatewaysService.updateGateway(gateway.gateway_id, updateData).subscribe((data) => {
			if (!data) return;

			gateway.name = data.name;

			this.startAutoRefresh();
		});
	}
	startAutoRefresh() {
		// Cancel existing auto-refresh subscription if it exists
		if (this.autoTableRefresher) {
			this.autoTableRefresher.unsubscribe();
		}

		// Always make an initial API call
		this.gatewaysService.findGateways().subscribe((data) => {
			this.dataSourceGateways.data = data.content;
		});

		// Parse autoRefreshInterval
		const autoRefreshInterval = this.autoRefreshInterval === "Off" ? 0 : parseInt(this.autoRefreshInterval.substring(0, this.autoRefreshInterval.length - 1)) * 1000;

		// If autoRefreshInterval is  0, don't set up interval
		if (autoRefreshInterval === 0) return;

		// otherwise do set up an interval
		this.autoTableRefresher = interval(autoRefreshInterval)
			.pipe(switchMap(() => this.gatewaysService.findGateways()))
			.subscribe((data) => {
				this.dataSourceGateways.data = data.content;

				const selectedGateway = data.content.find((gateway) => gateway.gateway_id === this.intercomService.selectedGateway?.gateway_id);

				// If the selected gateway is not in the list, set selectedGateway to undefined and and navigate back to the full table
				if (!selectedGateway) {
					this.intercomService.selectedGateway = undefined;
					this.routingService.navigateName("satellite-manager");
				}
			});
	}

	onIntervalChange() {
		this.startAutoRefresh();
	}

	openConsoleClick(index: number, event: Event) {
		event.stopPropagation();

		this.intercomService.selectedGateway = this.dataSourceGateways.data[index];
	}

	winkClick(gateway: GatewayVO, event: Event) {
		event.stopPropagation();

		this.rpcMqttService.sendRpcWink(gateway.gateway_id);
	}

	restartClick(gateway: GatewayVO | undefined, event: Event) {
		event.stopPropagation();

		if (!gateway) return;

		this.rpcMqttService.sendRpcReboot(gateway.gateway_id);

		gateway.status!.mqtt!.connected = false;
		// this.refreshThingsListDatabase(gateway);
	}

	isRowSelected(rowData: GatewayVO) {
		if (!this.intercomService.selectedGateway) return false;

		if (this.intercomService.selectedGateway.created !== rowData.created) return false;
		if (this.intercomService.selectedGateway.updated !== rowData.updated) return false;
		if (this.intercomService.selectedGateway.name !== rowData.name) return false;
		if (this.intercomService.selectedGateway.gateway_id !== rowData.gateway_id) return false;
		if (this.intercomService.selectedGateway.status?.mqtt?.connected !== rowData.status?.mqtt?.connected) return false;
		if (this.intercomService.selectedGateway.status?.mqtt?.timestamp !== rowData.status?.mqtt?.timestamp) return false;

		return true;
	}

	gatewayRowClicked(gateway: GatewayVO) {
		this.routingService.navigateToSatelliteManager(gateway.gateway_id);
	}

	deselectGateway() {
		this.intercomService.selectedGateway = undefined;
		this.routingService.navigateName("satellite-manager");
	}
}
