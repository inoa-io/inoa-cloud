import { Component, ViewChild, ElementRef, AfterViewInit, OnDestroy, HostListener } from "@angular/core";
import { GatewaysService, GatewayVO, GatewayPageVO } from "@inoa/api";
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
    private planet!: THREE.Mesh;
    private satellites: { mesh: THREE.Mesh; gateway: GatewayVO; trace: THREE.Line; tracePoints: THREE.Vector3[] }[] = [];
    private autoDataRefresher!: Subscription;
    private earthOffset: number = 3;

    private hashString(str: string): number {
        // MurmurHash3 variant for excellent distribution of similar strings
        let hash = 0xdeadbeef; // Seed value
        for (let i = 0; i < str.length; i++) {
            hash ^= str.charCodeAt(i);
            hash = (hash << 13) | (hash >>> 19);
            hash = hash * 5 + 0xe6546b64;
        }
        hash ^= str.length;
        hash ^= hash >>> 16;
        hash *= 0x85ebca6b;
        hash ^= hash >>> 13;
        hash *= 0xc2b2ae35;
        hash ^= hash >>> 16;
        return hash >>> 0; // Ensure unsigned 32-bit integer
    }

    public satelliteCount: number = 0;

    constructor(private gatewaysService: GatewaysService) {}

    private solveKeplersEquation(M: number, e: number, iterations = 5): number {
        // Newton-Raphson method to solve Kepler's equation: E - e*sin(E) = M
        let E = M;
        for (let i = 0; i < iterations; i++) {
            const delta = E - e * Math.sin(E) - M;
            const deltaE = delta / (1 - e * Math.cos(E));
            E -= deltaE;
        }
        return E;
    }

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
                this.planet = new THREE.Mesh(earthGeometry, material);
                this.planet.position.x = this.earthOffset;
                this.scene.add(this.planet);
            },
            undefined,
            (error) => console.error("Error loading Earth texture:", error)
        );
    }

    private setupSatellites(gateways: GatewayVO[] = []): void {
        const satelliteGeometry = new THREE.SphereGeometry(0.025, 16, 16);
        const satelliteMaterial = new THREE.MeshPhongMaterial({
            color: 0xffffff,
            specular: 0x555555,
            shininess: 30
        });

        // clear satellites and traces
        this.satellites.forEach((s) => {
            this.scene.remove(s.mesh);
            this.scene.remove(s.trace);
        });
        this.satellites = [];

        // create satellites with traces
        gateways.forEach((gateway, i) => {
            const satellite = new THREE.Mesh(satelliteGeometry, satelliteMaterial);

            // Create trace line with fading effect
            const traceMaterial = new THREE.LineBasicMaterial({
                vertexColors: true,
                transparent: true,
                linewidth: 3,
                depthTest: true,
                depthWrite: false
            });
            const traceGeometry = new THREE.BufferGeometry();
            const tracePoints: THREE.Vector3[] = [];
            const traceColors: THREE.Color[] = [];
            const trace = new THREE.Line(traceGeometry, traceMaterial);

            this.satellites.push({
                mesh: satellite,
                gateway,
                trace,
                tracePoints
            });
            this.scene.add(satellite);
            this.scene.add(trace);
        });
    }

    private handleGatewayData(data: GatewayPageVO): void {
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
    }

    private degToRad(deg: number): number {
        return deg * (Math.PI / 180);
    }

    private startAnimation(): void {
        const animate = () => {
            requestAnimationFrame(animate);
            if (this.planet) this.planet.rotation.y += 0.001;

            this.satellites.forEach(({ mesh, gateway, trace, tracePoints }) => {
                // Generate orbit seed from gateway ID
                const orbitSeed = this.hashString(gateway.gateway_id);

                // Orbital parameters
                const semiMajorAxis = 4 + (orbitSeed % 300) / 100; // 4.0 to 6.99
                const eccentricity = 0.1 + (orbitSeed % 80) / 400; // 0.1 to 0.3
                const inclination = this.degToRad((orbitSeed % 90) - 45);

                // Calculate orbital period using Kepler's third law (simplified)
                const period = (2 * Math.PI * Math.sqrt(Math.pow(semiMajorAxis, 3))) / 10;

                // Current position in orbit (negative for counter-clockwise)
                const meanAnomaly = -(Date.now() / 1000) * ((2 * Math.PI) / period);
                const eccentricAnomaly = this.solveKeplersEquation(meanAnomaly, eccentricity);
                const trueAnomaly = 2 * Math.atan2(Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2), Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2));

                // Current distance from Earth
                const distance = semiMajorAxis * (1 - eccentricity * Math.cos(eccentricAnomaly));

                // 3D position calculation
                mesh.position.x = distance * (Math.cos(trueAnomaly) * Math.cos(inclination)) + this.earthOffset;
                mesh.position.z = distance * Math.sin(trueAnomaly);
                mesh.position.y = distance * (Math.cos(trueAnomaly) * Math.sin(inclination));

                // Update trace
                tracePoints.push(new THREE.Vector3(mesh.position.x, mesh.position.y, mesh.position.z));

                // Limit trace length
                if (tracePoints.length > 100) {
                    tracePoints.shift();
                }

                // Update trace geometry with fading colors
                if (tracePoints.length > 1) {
                    trace.geometry.dispose();
                    const geometry = new THREE.BufferGeometry().setFromPoints(tracePoints);

                    // Create color array with fading effect
                    const colors = [];
                    const totalPoints = tracePoints.length;
                    for (let i = 0; i < totalPoints; i++) {
                        const ratio = i / totalPoints;
                        const color = new THREE.Color(0x55ffff);
                        color.multiplyScalar(ratio); // Fade from bright to dim
                        colors.push(color.r, color.g, color.b);
                    }

                    geometry.setAttribute("color", new THREE.Float32BufferAttribute(colors, 3));
                    trace.geometry = geometry;
                }
            });

            this.renderer.render(this.scene, this.camera);
        };
        animate();
    }

    ngAfterViewInit(): void {
        this.startAutoRefresh();
        this.gatewaysService.findGateways().subscribe((data) => {
            this.satelliteCount = data.content.length;
            this.handleGatewayData(data);
        });
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

        // Parse autoRefreshInterval
        const autoRefreshInterval = 10000;

        // otherwise do set up an interval
        this.autoDataRefresher = interval(autoRefreshInterval)
            .pipe(switchMap(() => this.gatewaysService.findGateways()))
            .subscribe((data) => {
                this.satelliteCount = data.content.length;
                this.handleGatewayData(data);
            });
    }
}
