import { Component, OnDestroy, OnInit } from "@angular/core";
import { InternalCommunicationService } from "../../../services/internal-communication-service";
import { RpcExchange, RpcHistoryService } from "../../../services/rpc-history-service";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { Subscription } from "rxjs";

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
export class RpcHistoryComponent implements OnInit, OnDestroy {
    expandedElement: RpcExchange | null = null;
    displayedColumnsRpcHistory: string[] = ["gateway_id", "method", "status"];
    private rpcHistorySubscription: Subscription = new Subscription();

    constructor(public rpcHistoryService: RpcHistoryService, public intercomService: InternalCommunicationService) {}

    ngOnInit() {
        console.log("Initializing RPC History Panel...");
        this.subscribeToRpcHistory();
    }

    ngOnDestroy() {
        if (this.rpcHistorySubscription) {
            this.rpcHistorySubscription.unsubscribe();
        }
    }

    private subscribeToRpcHistory() {
        this.rpcHistorySubscription = this.rpcHistoryService.getRpcHistory().subscribe(history => {
            if (history.length > 0) {
                this.expandedElement = history[0];
            }
        });
    }
}
