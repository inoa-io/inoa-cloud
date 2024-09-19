import { ErrorHandler, Injectable, NgZone } from "@angular/core";
import { ErrorDialogService } from "./error-dialog.service";
import { HttpErrorResponse } from "@angular/common/http";
import { InternalCommunicationService } from "../services/internal-communication-service";

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
    constructor(private errorDialogService: ErrorDialogService, private zone: NgZone, private intercomService: InternalCommunicationService) {}

    handleError(error: Error | HttpErrorResponse) {
        this.zone.run(() => {
            if (error instanceof HttpErrorResponse) {
                const conflictMessage = error.error._embedded.errors[0].message;
                const displayMessage = "Unknown error: " + conflictMessage;

                //if error is 401 error, just reload (after 2 second, so it does not go epileptic) and pray
                if (error instanceof HttpErrorResponse && error.status === 401) {
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                    return;
                }

                //otherwise show error window if user has a grayc.de or example.org email adress or just put it into the console otherwise
                const mailAdress = this.intercomService.userEmail;

                //if email is grayc adress
                if (mailAdress.endsWith("grayc.de") || mailAdress.endsWith("example.org")) {
                    //   //open error modal
                    this.errorDialogService.openDialog(displayMessage, error instanceof HttpErrorResponse ? error?.status : undefined, false);

                    return;
                }
            }

            //output the error to the console if nothing else took care of it yet
            console.error("Error from global error handler", error);
        });
    }
}
