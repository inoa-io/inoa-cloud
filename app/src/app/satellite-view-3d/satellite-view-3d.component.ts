import { Component, ViewChild, ElementRef, AfterViewInit, OnDestroy, HostListener } from "@angular/core";
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
    private satellites: THREE.Mesh[] = [];

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
                this.sphere.position.x = 5;
                this.scene.add(this.sphere);
            },
            undefined,
            (error) => console.error("Error loading Earth texture:", error)
        );
    }

    private setupSatellites(): void {
        const satelliteGeometry = new THREE.SphereGeometry(0.1, 16, 16);
        const satelliteMaterial = new THREE.MeshPhongMaterial({
            color: 0xffffff,
            specular: 0x555555,
            shininess: 30
        });
        for (let i = 0; i < 3; i++) {
            const satellite = new THREE.Mesh(satelliteGeometry, satelliteMaterial);
            this.satellites.push(satellite);
            this.scene.add(satellite);
        }
    }

    private startAnimation(): void {
        const animate = () => {
            requestAnimationFrame(animate);
            if (this.sphere) this.sphere.rotation.y += 0.001;

            this.satellites.forEach((satellite, index) => {
                const angle = (Date.now() * 0.001 + index * 2) % (2 * Math.PI);
                satellite.position.x = 4 * Math.cos(angle) + 5;
                satellite.position.z = 4 * Math.sin(angle);
            });

            this.renderer.render(this.scene, this.camera);
        };
        animate();
    }

    ngAfterViewInit(): void {
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
    }
}
