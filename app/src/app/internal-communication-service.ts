import { Injectable } from "@angular/core";

@Injectable({
	providedIn: "root"
})
export class InternalCommunicationService
{
	//paginator parameters
	pageNumber = 0;
	pageSize = 10;
	pageSizes = [10, 25, 50, 100];

	// shows what routing page is selected in the sidebar
	pageSelect = "";
}
