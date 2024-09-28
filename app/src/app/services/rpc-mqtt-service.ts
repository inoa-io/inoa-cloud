import { Injectable } from "@angular/core";
import { RemoteService } from "@inoa/api";
import { RpcCommandVO } from "@inoa/model";
import { v4 as uuidv4 } from "uuid";
import { RpcExchange, RpcExchangeStatus, RpcHistoryService } from "./rpc-history-service";
import { Observable } from "rxjs";

@Injectable({
	providedIn: "root"
})
export class RpcMqttService
{
    constructor(
        private remoteService: RemoteService,
        public rpcHistoryService: RpcHistoryService
    ) { }

    public sendRpcCommand(gatewayId: string, rpcCommand: RpcCommandVO): Observable<RpcExchange> {
        const exchange: RpcExchange = {
            id: rpcCommand.id ? rpcCommand.id : "",
            gateway_id: gatewayId,
            method: rpcCommand.method,
            parameters: JSON.stringify(rpcCommand.params, undefined, 4),
            response: "",
            status: RpcExchangeStatus.NONE
        };
    
        this.rpcHistoryService.addRpcExchange(exchange);
    
        //generate UUID if not set
        if (!rpcCommand.id) rpcCommand.id = uuidv4();
    
        return new Observable<RpcExchange>(observer => {
            this.remoteService.sendRpcCommand(gatewayId, rpcCommand, undefined, undefined).subscribe({
                next: (response) => {
                    exchange.id = response.id;
                    exchange.status = RpcExchangeStatus.OK;

                    if (response.error) {
                        exchange.response = response.error.message!;
                        exchange.status = RpcExchangeStatus.ERROR;
                    }
                    if (response.result) {
                        exchange.response = JSON.stringify(response.result, undefined, 4);
                    }
    
                    observer.next(exchange);
                    observer.complete();
                },
                error: (error) => {
                    exchange.status = RpcExchangeStatus.ERROR;
                    exchange.response = error.message || "Unknown error occurred";
                    observer.error(exchange);
                }
            });
        });
    }

    public sendRpcReboot(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.reboot" }
        this.sendRpcCommand(gatewayId, rpcCommand).subscribe();
    }

    public sendRpcWink(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.wink" }
        this.sendRpcCommand(gatewayId, rpcCommand).subscribe();
    }
}