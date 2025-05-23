import { Component } from "@angular/core";
import { FieldArrayType } from "@ngx-formly/core";

@Component({
	selector: "gc-formly-array-type",
	template: `
		<div class="mb-3">
			<legend *ngIf="props.label">{{ props.label }}</legend>
			<p *ngIf="props.description">{{ props.description }}</p>
			<div class="d-flex flex-row-reverse">
				<button class="btn btn-primary" type="button" (click)="add()">+</button>
			</div>

			<div class="alert alert-danger" role="alert" *ngIf="showError && formControl.errors">
				<formly-validation-message [field]="field"></formly-validation-message>
			</div>

			<div *ngFor="let field of field.fieldGroup; let i = index" class="row align-items-start">
				<formly-field class="col" [field]="field"></formly-field>
				<!-- *ngIf="field.props?.removable !== false" -->
				<div class="col-2 text-right">
					<button class="btn btn-danger" type="button" (click)="remove(i)">-</button>
				</div>
			</div>
		</div>
	`
})
export class ArrayTypeComponent extends FieldArrayType {}
