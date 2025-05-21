import { Component, ViewChild, ElementRef, AfterViewInit } from "@angular/core";
import * as THREE from "three";

@Component({
    selector: "gc-satellite-view-3d",
    templateUrl: "./satellite-view-3d.component.html",
    styleUrls: ["./satellite-view-3d.component.css"]
})
export class SatelliteView3dComponent implements AfterViewInit {
    @ViewChild("rendererContainer") rendererContainer!: ElementRef;
    private scene: THREE.Scene = new THREE.Scene();
    private camera: THREE.PerspectiveCamera = new THREE.PerspectiveCamera(45, 1, 0.1, 1000); // Narrower FOV
    private renderer: THREE.WebGLRenderer = new THREE.WebGLRenderer();
    private sphere!: THREE.Mesh;
    private satellites: THREE.Mesh[] = [];

    ngAfterViewInit() {
        // Set renderer size based on container
        const containerWidth = this.rendererContainer.nativeElement.clientWidth;
        const containerHeight = this.rendererContainer.nativeElement.clientHeight;
        this.renderer.setSize(containerWidth, containerHeight);
        this.renderer.setClearColor(0x000000, 1);
        this.rendererContainer.nativeElement.appendChild(this.renderer.domElement);

        // Update camera aspect ratio
        this.camera.aspect = containerWidth / containerHeight;
        this.camera.updateProjectionMatrix();
        this.camera.position.set(-2, 0, 15); // Shift camera left slightly

        // Add space background
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

        // Add lighting
        const directionalLight = new THREE.DirectionalLight(0xffffff, 2.5);
        directionalLight.position.set(20, 10, 10);
        this.scene.add(directionalLight);
        const ambientLight = new THREE.AmbientLight(0x202020, 0.2);
        this.scene.add(ambientLight);

        // Add the Earth with offset to the right
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

        // Add satellites with offset to orbit around the shifted Earth
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

        this.camera.lookAt(0, 0, 0); // Look at the scene center

        // Animation loop
        const animate = () => {
            requestAnimationFrame(animate);
            if (this.sphere) this.sphere.rotation.y += 0.001;

            // Animate satellites around the shifted Earth
            this.satellites.forEach((satellite, index) => {
                const angle = (Date.now() * 0.001 + index * 2) % (2 * Math.PI);
                satellite.position.x = 4 * Math.cos(angle) + 5;
                satellite.position.z = 4 * Math.sin(angle);
            });

            this.renderer.render(this.scene, this.camera);
        };
        animate();
    }
}
