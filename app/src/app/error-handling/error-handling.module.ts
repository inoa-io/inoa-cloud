import { NgModule } from "@angular/core";
import { ErrorDialogComponent } from "./error-dialog/error-dialog.component";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { ErrorDialogService } from "./error-dialog.service";
import { MaterialModule } from "../material.module";
import { HTTP_INTERCEPTORS } from "@angular/common/http";
import { ErrorHandler } from "@angular/core";
import { GlobalErrorHandler } from "./error-handler";
import { HttpLoadingInterceptor } from "./http-loading.interceptor";

@NgModule({
	declarations: [ErrorDialogComponent],
	imports: [CommonModule, RouterModule, MaterialModule],
	exports: [ErrorDialogComponent],
	providers: [
		ErrorDialogService,
		{
			provide: ErrorHandler,
			useClass: GlobalErrorHandler
		},
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpLoadingInterceptor,
			multi: true
		}
	]
})
export class ErrorHandlingModule {}
