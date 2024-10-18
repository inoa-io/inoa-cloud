import { HttpHandler, HttpRequest, HttpEvent, HttpInterceptor } from "@angular/common/http";
import { Observable } from "rxjs";
import { finalize } from "rxjs/operators";
import { Injectable } from "@angular/core";
import { InternalCommunicationService } from "../services/internal-communication-service";

@Injectable()
export class HttpLoadingInterceptor implements HttpInterceptor {
	constructor(private intercomService: InternalCommunicationService) {}

	intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
		this.intercomService.httpDataLoading = true;

		return next.handle(request).pipe(
			finalize(() => {
				this.intercomService.httpDataLoading = false;
			})
		) as Observable<HttpEvent<unknown>>;
	}
}
