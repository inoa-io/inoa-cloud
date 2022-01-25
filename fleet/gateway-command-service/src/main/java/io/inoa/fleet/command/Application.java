package io.inoa.fleet.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(InoaConfig.class)
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
