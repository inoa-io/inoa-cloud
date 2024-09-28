import { Injectable } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { BehaviorSubject, Observable } from "rxjs";

export interface RpcExchange {
    id: string;
    gateway_id: string;
    method: string;
    parameters: string;
    response: string;
    status: RpcExchangeStatus;
  }
  
  export enum RpcExchangeStatus {
    OK,
    ERROR,
    NONE
}
  
@Injectable({
    providedIn: "root"
})
export class RpcHistoryService {
    private rpcExchangeHistory: MatTableDataSource<RpcExchange> = new MatTableDataSource<RpcExchange>([]);
    private rpcHistorySubject: BehaviorSubject<RpcExchange[]> = new BehaviorSubject<RpcExchange[]>([]);

    constructor() { }

    getRpcStatusColor(status: RpcExchangeStatus): string {
        switch (status) {
            case RpcExchangeStatus.OK: return "green";
            case RpcExchangeStatus.ERROR: return "red";
            case RpcExchangeStatus.NONE: return "gray";
        }
    }

    addRpcExchange(exchange: RpcExchange) {
        this.rpcExchangeHistory.data = [exchange, ...this.rpcExchangeHistory.data];
        this.rpcHistorySubject.next(this.rpcExchangeHistory.data);
    }

    getRpcHistory(): Observable<RpcExchange[]> {
        return this.rpcHistorySubject.asObservable();
    }

    deleteRpcExchange(id: string) { this.rpcExchangeHistory.data = this.rpcExchangeHistory.data.filter((x) => x.id !== id); }

    clearRpcHistory() { this.rpcExchangeHistory.data = []; }
}