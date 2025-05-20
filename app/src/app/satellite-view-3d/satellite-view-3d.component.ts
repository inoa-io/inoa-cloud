import { Component } from "@angular/core";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";

@Component({
  selector: "gc-satellite-view-3d",
  templateUrl: "./satellite-view-3d.component.html",
  styleUrl: "./satellite-view-3d.component.css"
})
export class SatelliteView3dComponent {
  safeUrl: SafeResourceUrl | null = null;
  constructor(private sanitizer: DomSanitizer) { }
  
  ngOnInit() {
  }
}
