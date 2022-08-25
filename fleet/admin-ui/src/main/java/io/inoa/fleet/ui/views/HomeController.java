package io.inoa.fleet.ui.views;

import java.net.URI;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
public class HomeController {

	@Get(uri = "/", produces = MediaType.TEXT_HTML)
	public HttpResponse<?> home() {
		return HttpResponse.temporaryRedirect(URI.create("/gateway"));
	}
}
