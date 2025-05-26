package io.inoa.app;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class StaticFilesController {

	private final Path basePath;

	StaticFilesController(@Value("${static.path:/app/static}") Path basePath) {
		this.basePath = basePath;
	}

	@Get(uris = { "/", "{path:^(?!api|endpoints\\/).*}" }, processes = MediaType.ALL)
	HttpResponse<File> call(HttpRequest<?> request) {

		// get file from filesystem

		var fsPath = basePath.resolve(request.getPath().substring(1));
		if (!Files.isRegularFile(fsPath)) {
			var mediaTypes = request.getHeaders().accept();
			if (mediaTypes.isEmpty() || mediaTypes.contains(MediaType.TEXT_HTML_TYPE)) {
				fsPath = basePath.resolve("index.html");
			} else {
				log.error("Path {} not found in file system: {}", request.getPath(), fsPath);
				return HttpResponse.notFound();
			}
		}

		// check if file can be cached

		var filename = fsPath.getFileName().toString();
		var mediaType = MediaType.forFilename(filename);
		var cache = HttpHeaderValues.CACHE_PRIVATE + "," + HttpHeaderValues.CACHE_MAX_AGE + "=3600";
		if ("index.html".equals(filename) || filename.matches("^[a-z0-9]+\\.(js|css)$")) {
			// disable caching for index.html and js/css without hash
			cache = HttpHeaderValues.CACHE_NO_STORE;
		} else if (filename.matches("^[0-9a-z]+\\.[0-9a-f]+\\.(js|css)$")) {
			// enable long caching for js/css with hash
			cache = HttpHeaderValues.CACHE_PRIVATE + "," + HttpHeaderValues.CACHE_MAX_AGE + "=86400";
		}

		return HttpResponse.ok(fsPath.toFile())
				.contentType(mediaType)
				.header(HttpHeaders.CACHE_CONTROL, cache);
	}
}
