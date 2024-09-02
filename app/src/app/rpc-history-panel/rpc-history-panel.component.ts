import { AfterViewInit, Component, OnInit } from "@angular/core";
import { InternalCommunicationService } from "../internal-communication-service";
import { RpcExchange, RpcHistoryService } from "./rpc-history-service";
import { animate, state, style, transition, trigger } from "@angular/animations";

@Component({
    selector: "gc-rpc-history-panel",
    templateUrl: "./rpc-history-panel.component.html",
    styleUrls: ["./rpc-history-panel.component.css"],
    animations: [
        trigger("detailExpand", [
            state("collapsed", style({ height: "0px", minHeight: "0" })),
            state("expanded", style({ height: "*" })),
            transition("expanded <=> collapsed", animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)"))
        ])
    ]
})
export class RpcHistoryComponent implements AfterViewInit, OnInit {
    expandedElement: RpcExchange | null = null;
    displayedColumnsRpcHistory: string[] = ["gateway_id", "method", "status"];

    constructor(public rpcHistoryService: RpcHistoryService, public intercomService: InternalCommunicationService) {}

    ngOnInit() {
        console.log("Init RPC History Panel");
    }

    ngAfterViewInit() {
        console.log("Init Done");
    }
}
