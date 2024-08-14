import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RpcCommandVO, RpcResponseVO } from "@inoa/model";

@Injectable({
	providedIn: "root"
})
export class RpcRestService
{
    constructor(private http: HttpClient) { }

    getUrlFromGatewayId(gatewayId: string) {
        return "http://Satellite-" + gatewayId.substring(gatewayId.length - 6) + ".local/rpc";
    }

    
    public generateUUID(): string {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    public sendRpcCommand(gatewayId: string, rpcCommand: RpcCommandVO) {
        const url = this.getUrlFromGatewayId(gatewayId);

        //generate UUID if not set
        if (rpcCommand.id == undefined) rpcCommand.id = this.generateUUID();
        
        let body = "{";
        
        body += "\"id\": \"" + rpcCommand.id + "\", ";
        body += "\"method\": \"" + rpcCommand.method + "\"";
        if (rpcCommand.params != undefined) body += ", \"params\": " + JSON.stringify(rpcCommand.params);
        body += " }";

        return this.http.post(url, body);
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