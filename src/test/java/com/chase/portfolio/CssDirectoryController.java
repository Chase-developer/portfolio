package com.chase.portfolio;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This was supposed to work natively, but I don't know why it doesn't. So had to add this manully
 */
@Controller
public class CssDirectoryController {

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
        //headers.add(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        
//        String origin = request.getHeader("Origin");
//        if ("http://localhost:8080".equals(origin)) {  // Change this to your website's domain
//            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }
}
