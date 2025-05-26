import { Component, ViewChild, ElementRef, AfterViewInit, OnDestroy, HostListener } from "@angular/core";
import { GatewaysService, GatewayVO } from "@inoa/api";
import { interval, Subscription, switchMap } from "rxjs";
import * as THREE from "three";

@Component({
    selector: "gc-satellite-view-3d",
    templateUrl: "./satellite-view-3d.component.html",
    styleUrls: ["./satellite-view-3d.component.css"]
})
export class SatelliteView3dComponent implements AfterViewInit, OnDestroy {
    @ViewChild("rendererContainer") rendererContainer!: ElementRef;
    private isFullscreen = false;
    private originalParent!: HTMLElement;
    private scene: THREE.Scene = new THREE.Scene();
    private camera: THREE.PerspectiveCamera = new THREE.PerspectiveCamera(45, 1, 0.1, 1000); // Narrower FOV
    private renderer: THREE.WebGLRenderer = new THREE.WebGLRenderer();
    private resizeObserver!: ResizeObserver;
    private sphere!: THREE.Mesh;
    private satellites: { mesh: THREE.Mesh; gateway: GatewayVO }[] = [];
    private autoDataRefresher!: Subscription;
    private earthOffset: number = 3;

    public satelliteCount: number = 0;

    constructor(private gatewaysService: GatewaysService) {}

    private setRendererSize(): void {
        const container = this.rendererContainer.nativeElement;
        const width = this.isFullscreen ? window.innerWidth : container.clientWidth;
        const height = this.isFullscreen ? window.innerHeight : container.clientHeight;
        this.renderer.setSize(width, height);
        this.camera.aspect = width / height;
        this.camera.updateProjectionMatrix();
    }

    toggleFullscreen(): void {
        if (!this.isFullscreen) {
            this.originalParent = this.rendererContainer.nativeElement.parentNode;
            document.body.appendChild(this.rendererContainer.nativeElement);
            this.rendererContainer.nativeElement.style.position = "fixed";
            this.rendererContainer.nativeElement.style.top = "0";
            this.rendererContainer.nativeElement.style.left = "0";
            this.rendererContainer.nativeElement.style.width = "100vw";
            this.rendererContainer.nativeElement.style.height = "100vh";
            this.rendererContainer.nativeElement.style.zIndex = "10000";
            this.isFullscreen = true;
        } else {
            this.originalParent.appendChild(this.rendererContainer.nativeElement);
            this.rendererContainer.nativeElement.style.position = "";
            this.rendererContainer.nativeElement.style.top = "";
            this.rendererContainer.nativeElement.style.left = "";
            this.rendererContainer.nativeElement.style.width = "";
            this.rendererContainer.nativeElement.style.height = "";
            this.rendererContainer.nativeElement.style.zIndex = "";
            this.isFullscreen = false;
        }
        this.setRendererSize();
    }

    @HostListener("document:fullscreenchange")
    onFullscreenChange(): void {
        this.setRendererSize();
    }

    private setupRenderer(): void {
        this.setRendererSize();
        this.renderer.setClearColor(0x000000, 1);
        this.rendererContainer.nativeElement.appendChild(this.renderer.domElement);
        this.resizeObserver = new ResizeObserver(() => this.setRendererSize());
        this.resizeObserver.observe(this.rendererContainer.nativeElement);
        this.camera.position.set(-2, 0, 15);
        this.camera.lookAt(0, 0, 0);
    }

    private setupBackground(): void {
        const backgroundTextureLoader = new THREE.TextureLoader();
        const bgTextureWidth = 2500;
        const bgTextureHeight = 1250;
        const backgroundZoom = 0.1;
        const backgroundWidth = bgTextureWidth * backgroundZoom;
        const backgroundHeight = bgTextureHeight * backgroundZoom;

        backgroundTextureLoader.load(
            "assets/images/space-bg.jpg",
            (backgroundTexture) => {
                backgroundTexture.colorSpace = THREE.NoColorSpace;
                const backgroundMaterial = new THREE.MeshBasicMaterial({
                    map: backgroundTexture,
                    color: 0x444444
                });
                const backgroundGeometry = new THREE.PlaneGeometry(backgroundWidth, backgroundHeight);
                const background = new THREE.Mesh(backgroundGeometry, backgroundMaterial);
                background.position.z = -50;
                this.scene.add(background);
            },
            undefined,
            (error) => console.error("Error loading background:", error)
        );
    }

    private setupLighting(): void {
        const directionalLight = new THREE.DirectionalLight(0xffffff, 2.5);
        directionalLight.position.set(20, 10, 10);
        this.scene.add(directionalLight);
        const ambientLight = new THREE.AmbientLight(0x202020, 0.2);
        this.scene.add(ambientLight);
    }

    private setupEarth(): void {
        const earthGeometry = new THREE.SphereGeometry(3, 32, 32);
        const textureLoader = new THREE.TextureLoader();
        textureLoader.load(
            "assets/images/earth_2048.jpg",
            (earthTexture) => {
                const material = new THREE.MeshPhongMaterial({
                    map: earthTexture,
                    specular: 0x333333,
                    shininess: 25
                });
                this.sphere = new THREE.Mesh(earthGeometry, material);
                this.sphere.position.x = this.earthOffset;
                this.scene.add(this.sphere);
            },
            undefined,
            (error) => console.error("Error loading Earth texture:", error)
        );
    }

    private setupSatellites(gateways: GatewayVO[] = []): void {
        const satelliteGeometry = new THREE.SphereGeometry(0.1, 16, 16);
        const satelliteMaterial = new THREE.MeshPhongMaterial({
            color: 0xffffff,
            specular: 0x555555,
            shininess: 30
        });

        // clear satellites
        this.scene.remove(...this.satellites.map((s) => s.mesh));
        this.satellites = [];

        // create satellites
        gateways.forEach((gateway, i) => {
            const satellite = new THREE.Mesh(satelliteGeometry, satelliteMaterial);
            this.satellites.push({ mesh: satellite, gateway });
            this.scene.add(satellite);
        });
    }

    private startAnimation(): void {
        const animate = () => {
            requestAnimationFrame(animate);
            if (this.sphere) this.sphere.rotation.y += 0.001;

            this.satellites.forEach(({ mesh, gateway }, index) => {
                // Create unique orbit based on gateway properties
                const orbitSeed = gateway.gateway_id.charCodeAt(0) + gateway.gateway_id.charCodeAt(gateway.gateway_id.length - 1);
                const orbitRadius = 4 + (orbitSeed % 100) / 100; // Slightly different radius
                const orbitSpeed = -0.001 + (orbitSeed % 50) / 50000; // Slightly different speed
                const orbitOffset = ((orbitSeed % 100) / 50) * Math.PI; // Different starting position

                const angle = (Date.now() * orbitSpeed + orbitOffset) % (2 * Math.PI);
                mesh.position.x = orbitRadius * Math.cos(angle) + this.earthOffset;
                mesh.position.z = orbitRadius * Math.sin(angle);
                mesh.position.y = (orbitSeed % 100) / 50 - 1; // Slightly different height
            });

            this.renderer.render(this.scene, this.camera);
        };
        animate();
    }

    ngAfterViewInit(): void {
        this.startAutoRefresh();
        this.setupRenderer();
        this.setupBackground();
        this.setupLighting();
        this.setupEarth();
        this.setupSatellites();
        this.startAnimation();
        this.toggleFullscreen();
    }

    ngOnDestroy(): void {
        if (this.resizeObserver) {
            this.resizeObserver.disconnect();
        }

        this.autoDataRefresher.unsubscribe();
    }

    startAutoRefresh() {
        // Cancel existing auto-refresh subscription if it exists
        if (this.autoDataRefresher) {
            this.autoDataRefresher.unsubscribe();
        }

        // Always make an initial API call
        // this.gatewaysService.findGateways().subscribe((data) => {
        //     this.satelliteCount = data.content.length;
        //     this.setupSatellites(data.content);
        //     this.startAnimation();
        // });

        // Parse autoRefreshInterval
        const autoRefreshInterval = 10000;

        // otherwise do set up an interval
        this.autoDataRefresher = interval(autoRefreshInterval)
            .pipe(switchMap(() => this.gatewaysService.findGateways()))
            .subscribe((data) => {
                this.satelliteCount = data.content.length;

                // Update existing satellites with new data or create new ones
                if (this.satellites.length > 0) {
                    // Map new data to existing satellites by matching IDs
                    data.content.forEach((newGateway) => {
                        const existingSat = this.satellites.find((s) => s.gateway.gateway_id === newGateway.gateway_id);

                        if (existingSat) {
                            existingSat.gateway = newGateway;
                        }
                    });
                } else {
                    this.setupSatellites(data.content);
                }
                this.startAnimation();
            });
    }
}
