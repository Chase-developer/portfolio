package com.chase.portfolio;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/font")
public class FontController {
	
	@GetMapping("/css/{fileName:.+\\.css}")
    public ResponseEntity<byte[]> getCssFile(@PathVariable("fileName") String fileName) throws IOException {
        // Prevent path traversal
    	if (!fileName.matches("^[a-zA-Z0-9._-]+\\.css$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Load the CSS file from classpath
        Resource resource = new ClassPathResource("static/css/" + fileName);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Read file content
        Path path = resource.getFile().toPath();
        byte[] cssContent = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/css");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        
//        String origin = request.getHeader("Origin");
//        if ("http://localhost:8080".equals(origin)) {  // Change this to your website's domain
//            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }

    private final Path fontDirectory = Paths.get("src/main/resources/static/font");

    @GetMapping("/{fontName:.+}")
    public ResponseEntity<Resource> serveFont(@PathVariable String fontName) throws MalformedURLException {
        Path file = fontDirectory.resolve(fontName);
        Resource resource = new UrlResource(file.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
