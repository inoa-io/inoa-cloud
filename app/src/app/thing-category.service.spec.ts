import { TestBed } from "@angular/core/testing";

import { ThingCategoryService } from "./thing-category.service";

describe("ThingCategoryService", () => {
  let service: ThingCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ThingCategoryService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
