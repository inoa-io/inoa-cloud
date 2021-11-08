package io.inoa.fleet.registry.hono;

import org.eclipse.hono.config.ApplicationConfigProperties;
import org.eclipse.hono.config.ServerConfig;
import org.eclipse.hono.config.ServiceConfigProperties;
import org.eclipse.hono.config.VertxProperties;
import org.eclipse.hono.deviceregistry.server.DeviceRegistryAmqpServer;
import org.eclipse.hono.service.HealthCheckServer;
import org.eclipse.hono.service.VertxBasedHealthCheckServer;
import org.eclipse.hono.service.amqp.AmqpEndpoint;
import org.eclipse.hono.service.credentials.CredentialsService;
import org.eclipse.hono.service.credentials.DelegatingCredentialsAmqpEndpoint;
import org.eclipse.hono.service.metric.spring.PrometheusSupport;
import org.eclipse.hono.service.registration.DelegatingRegistrationAmqpEndpoint;
import org.eclipse.hono.service.registration.RegistrationService;
import org.eclipse.hono.service.tenant.DelegatingTenantAmqpEndpoint;
import org.eclipse.hono.service.tenant.TenantService;
import org.eclipse.hono.util.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

@Configuration
@Import(PrometheusSupport.class)
public class ApplicationConfig {

	// Vert.x

	@Bean
	@ConfigurationProperties("hono.vertx")
	public VertxProperties vertxProperties() {
		return new VertxProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "hono.app")
	public ApplicationConfigProperties applicationConfigProperties() {
		return new ApplicationConfigProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "hono.health-check")
	public ServerConfig healthCheckConfigProperties() {
		return new ServerConfig();
	}

	@Bean
	public Vertx vertx() {
		return Vertx.vertx(vertxProperties().configureVertx(new VertxOptions()));
	}

	@Bean
	public HealthCheckServer healthCheckServer() {
		return new VertxBasedHealthCheckServer(vertx(), healthCheckConfigProperties());
	}
	// AMQP

	@Qualifier(Constants.QUALIFIER_AMQP)
	@Bean
	@ConfigurationProperties(prefix = "hono.registry.amqp")
	public ServiceConfigProperties amqpServerProperties() {
		return new ServiceConfigProperties();
	}

	@Bean
	public DeviceRegistryAmqpServer amqpServer() {
		return new DeviceRegistryAmqpServer();
	}

	@Bean
	public ObjectFactoryCreatingFactoryBean amqpServerFactory() {
		ObjectFactoryCreatingFactoryBean factory = new ObjectFactoryCreatingFactoryBean();
		factory.setTargetBeanName("amqpServer");
		return factory;
	}

	@Bean
	public AmqpEndpoint tenantAmqpEndpoint(Vertx vertx, TenantService tenantService) {
		return new DelegatingTenantAmqpEndpoint<>(vertx, tenantService);
	}

	@Bean
	public AmqpEndpoint registrationAmqpEndpoint(Vertx vertx, RegistrationService registrationService) {
		return new DelegatingRegistrationAmqpEndpoint<>(vertx, registrationService);
	}

	@Bean
	public AmqpEndpoint credentialsAmqpEndpoint(Vertx vertx, CredentialsService credentialsService) {
		return new DelegatingCredentialsAmqpEndpoint<>(vertx, credentialsService);
	}
}
