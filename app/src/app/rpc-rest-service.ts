import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { RpcCommandVO, RpcResponseVO } from "@inoa/model";

@Injectable({
	providedIn: "root"
})
export class RpcRestService
{
    constructor(private http: HttpClient) { }

    public sendRpcCommand(gatewayId: string, rpcCommand: RpcCommandVO) {
        const url = "http://Satellite-" + gatewayId.substring(gatewayId.length - 6) + ".local/rpc";
        let body = "{";
        
        if (rpcCommand.id != null) body += "\"id\": " + rpcCommand.id + ", ";
        body += "\"method\": \"" + rpcCommand.method + "\"";
        if (rpcCommand.params != null) body += ", \"params\": " + JSON.stringify(rpcCommand.params);
        body += " }";

        return this.http.post(url, body);
    }
}