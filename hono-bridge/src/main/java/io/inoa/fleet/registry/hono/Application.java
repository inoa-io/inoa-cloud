package io.inoa.fleet.registry.hono;

import java.util.List;

import org.eclipse.hono.service.AbstractServiceBase;
import org.eclipse.hono.service.HealthCheckProvider;
import org.eclipse.hono.service.spring.AbstractApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import io.vertx.core.Future;
import io.vertx.core.Verticle;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@ComponentScan(
	basePackages = { "io.inoa.fleet.registry.hono", "org.eclipse.hono.service.auth" },
	includeFilters = @ComponentScan.Filter(Deprecated.class))
@Import(ApplicationConfig.class)
@RequiredArgsConstructor
public class Application extends AbstractApplication {

	private final List<Verticle> verticles;
	private final List<HealthCheckProvider> healthCheckProviders;

	@Override
	protected Future<Void> deployRequiredVerticles(int maxInstances) {
		verticles.forEach(getVertx()::deployVerticle);
		return Future.succeededFuture();
	}

	@Override
	protected void postDeploy(AbstractServiceBase<?> serviceInstance) {
		healthCheckProviders.forEach(this::registerHealthchecks);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
