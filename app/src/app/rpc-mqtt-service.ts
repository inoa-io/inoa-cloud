import { Injectable } from "@angular/core";
import { RemoteService } from "@inoa/api";
import { RpcCommandVO } from "@inoa/model";
import { v4 as uuidv4 } from "uuid";
import { RpcExchange, RpcExchangeStatus, RpcHistoryService } from "./rpc-history-panel/rpc-history-service";

@Injectable({
	providedIn: "root"
})
export class RpcMqttService
{
    constructor(
        private remoteService: RemoteService,
        public rpcHistoryService: RpcHistoryService
    ) { }

    public sendRpcCommand(gatewayId: string, rpcCommand: RpcCommandVO) {
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

        this.remoteService.sendRpcCommand(gatewayId, rpcCommand, undefined, undefined).subscribe((response) => {
            exchange.id = response.id;
            exchange.status = RpcExchangeStatus.OK;

            if (response.error) {
                exchange.response = response.error.message!;
                exchange.status = RpcExchangeStatus.ERROR;
            }
            if (response.result) {
                exchange.response = JSON.stringify(response.result, undefined, 4);
            }
        });
    }

    public sendRpcReboot(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.reboot" }
        this.sendRpcCommand(gatewayId, rpcCommand);
    }

    public sendRpcWink(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.wink" }
        this.sendRpcCommand(gatewayId, rpcCommand);
    }
}