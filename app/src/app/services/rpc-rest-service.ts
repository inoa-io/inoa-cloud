import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RpcCommandVO } from "@inoa/model";
import { v4 as uuidv4 } from "uuid";

@Injectable({
    providedIn: "root"
})
export class RpcRestService {
    constructor(private http: HttpClient) {}

    getUrlFromGatewayId(gatewayId: string) {
        return `http://${gatewayId}/rpc`;
    }

    public sendRpcCommand(gatewayId: string, rpcCommand: RpcCommandVO) {
        const url = this.getUrlFromGatewayId(gatewayId);

        //generate UUID if not set
        if (!rpcCommand.id) rpcCommand.id = uuidv4();

        return this.http.post(url, JSON.stringify(rpcCommand));
    }

    public sendRpcReboot(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.reboot" };

        this.sendRpcCommand(gatewayId, rpcCommand).subscribe();
    }

    public sendRpcWink(gatewayId: string) {
        const rpcCommand: RpcCommandVO = { method: "sys.wink" };

        this.sendRpcCommand(gatewayId, rpcCommand).subscribe();
    }
}
