import { Injectable } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

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

    constructor() { }

    getRpcStatusColor(status: RpcExchangeStatus): string {
        switch (status) {
            case RpcExchangeStatus.OK: return "green";
            case RpcExchangeStatus.ERROR: return "red";
            case RpcExchangeStatus.NONE: return "gray";
        }
    }

    addRpcExchange(exchange: RpcExchange) { this.rpcExchangeHistory.data = [exchange, ...this.rpcExchangeHistory.data]; }

    getRpcHistory(): MatTableDataSource<RpcExchange> { return this.rpcExchangeHistory; }

    deleteRpcExchange(id: string) { this.rpcExchangeHistory.data = this.rpcExchangeHistory.data.filter((x) => x.id !== id); }

    clearRpcHistory() { this.rpcExchangeHistory.data = []; }
}