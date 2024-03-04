import {Injectable, OnDestroy} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Subject} from "rxjs";

export interface ThingCategory {
  key: string,
  image: string,
  title: string
}

@Injectable({
  providedIn: "root"
})
export class ThingCategoryService implements OnDestroy {

  private destroy$: Subject<any> = new Subject<any>();
  private categories = new Map<string, ThingCategory>();

  constructor(private http: HttpClient) {
    const request = this.http.get<ThingCategory[]>("assets/thing-categories/categories.json");
    request.subscribe((resp)=>{
      resp.forEach(next => {
        this.categories.set(next.key, next);
      });
    });
  }

  public getCategory(key : string) : ThingCategory {
    return <ThingCategory>this.categories.get(key);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
}
