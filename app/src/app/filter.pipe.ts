import { Injectable, Pipe, PipeTransform } from "@angular/core";

@Pipe({
	name: "filter"
})
@Injectable()
export class FilterPipe implements PipeTransform {
	/* eslint-disable @typescript-eslint/no-explicit-any */
	transform(items: any[], field: string, value: string): any[] {
		if (!items) return [];
		if (!value || value.length == 0) return items;

		return items.filter((it) => it[field].toLowerCase().indexOf(value.toLowerCase()) != -1);
	}
}
