package io.inoa.controller.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.test.AbstractUnitTest;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Checks if all angular resources are available.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("app: static files")
@MicronautTest(environments = "app")
@Property(name = "micronaut.router.static-resources.app.paths", value = "classpath:static")
public class AppFilesTest extends AbstractUnitTest {

	@Inject @Client("/") HttpClient client;

	@DisplayName("/")
	@Test
	void index() {
		call("/", "index.html", "text/html");
	}

	@DisplayName("/index.html")
	@Test
	void indexHtml() {
		call("/index.html", "index.html", "text/html");
	}

	@DisplayName("/main.573869017767e6ae.js")
	@Test
	void pathMainJs() {
		call("/main.573869017767e6ae.js", "main.573869017767e6ae.js", "application/javascript");
	}

	@DisplayName("/styles.274ad411a0e81426.css")
	@Test
	void pathStylesCss() {
		call("/styles.274ad411a0e81426.css", "styles.274ad411a0e81426.css", "text/css");
	}

	@DisplayName("/favicon.ico")
	@Test
	void pathFaviconIco() {
		call("/favicon.ico", "favicon.ico", "image/x-icon");
	}

	@DisplayName("/assets/images/test.svg")
	@Test
	void pathAssetsTestSvg() {
		call("/assets/images/test.svg", "assets/images/test.svg", "image/svg+xml");
	}

	@DisplayName("/assets/test.txt")
	@Test
	void pathAssetsTestTxt() {
		call("/assets/test.txt", "assets/test.txt", "text/plain");
	}

	private void call(String path, String body, String type) {
		var response = client.toBlocking().exchange(path, String.class);
		assertEquals(HttpStatus.OK, response.getStatus());
		assertEquals(body, response.body(), "body");
		assertEquals(type, response.getHeaders().get(HttpHeaders.CONTENT_TYPE), "type");
	}
}
