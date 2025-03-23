package com.chase.portfolio.controllers;
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

import com.chase.portfolio.services.OCIStorageService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This was supposed to work natively, but I don't know why it doesn't. So had to add this manully
 */
@Controller
public class StaticDirectoryController {
	
	/*
	 * private static final String OBJECT_STORAGE_URL = "https://objectstorage.YOUR_REGION.oraclecloud.com/n/YOUR_NAMESPACE/b/YOUR_BUCKET/o/";

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Void> getFont(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        // Simulate generating a signed URL (replace with actual Oracle API call)
        String preAuthUrl = OBJECT_STORAGE_URL + fileName + "?par=" + generateTemporaryToken();

        // Redirect CSS request to the actual font file
        response.sendRedirect(preAuthUrl);
        return ResponseEntity.ok().build();
    }

    private String generateTemporaryToken() {
        return "generated-temp-token-" + Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond();
    }
	 */
	
	//private static final OCIStorageAPI API = ;
	
	private final OCIStorageService storage_service;
	
	// Constructor-based injection (Recommended)
    public StaticDirectoryController(OCIStorageService storage_service) {
        this.storage_service = storage_service;
    }

	/**
	 * at some point need to change this so that it gets the file from the storage bucket instead
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/fonts/{fileName:.+}")
	public ResponseEntity<byte[]> getFontFile(@PathVariable("fileName") String fileName) throws IOException {
	    // Allow only alphanumeric, underscores, hyphens, and dots to prevent path traversal
	    if (!fileName.matches("^[a-zA-Z0-9._-]+$")) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    }

	    // Load the font file from classpath
	    Resource resource = new ClassPathResource("static/fonts/" + fileName);

	    if (!resource.exists()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	    
	    return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, storage_service.getPreAuthURL("fonts/" +fileName))
                .build();

	    // Read file content
//	    Path path = resource.getFile().toPath();
//	    byte[] fileContent = Files.readAllBytes(path);
//
//	    // Set correct content type based on file extension
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
//	    headers.add("X-Content-Type-Options", "nosniff");
//
//	    return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
	}

    
//    @GetMapping("/css/{fileName:.+\\.css}")
//    public ResponseEntity<byte[]> getCssFile(@PathVariable("fileName") String fileName) throws IOException {
//        // Prevent path traversal
//    	if (!fileName.matches("^[a-zA-Z0-9._-]+\\.css$")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        // Load the CSS file from classpath
//        Resource resource = new ClassPathResource("static/css/" + fileName);
//
//        if (!resource.exists()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        // Read file content
//        Path path = resource.getFile().toPath();
//        byte[] cssContent = Files.readAllBytes(path);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/css");
//        headers.add("X-Content-Type-Options", "nosniff");
//       
//        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
//    }
    
    @GetMapping("/css/**")
    public ResponseEntity<byte[]> getCssFile(HttpServletRequest request) throws IOException {
        // Extract the CSS file path after "/css/"
        String filePath = request.getRequestURI().replaceFirst("/css/", "");

        // Prevent path traversal attacks
        if (!filePath.matches("^[a-zA-Z0-9/_\\.-]+\\.css$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Load the CSS file from the static directory
        Resource resource = new ClassPathResource("static/css/" + filePath);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Read file content
        byte[] cssContent = Files.readAllBytes(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/css");
        headers.add("X-Content-Type-Options", "nosniff");

        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }

    
    @GetMapping("/texts/{fileName:.+\\.txt}")
    public ResponseEntity<byte[]> getTextFile(@PathVariable("fileName") String fileName) throws IOException {
        // Prevent path traversal
    	if (!fileName.matches("^[a-zA-Z0-9._-]+\\.txt$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Load the CSS file from classpath
        Resource resource = new ClassPathResource("static/texts/" + fileName);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Read file content
        Path path = resource.getFile().toPath();
        byte[] cssContent = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/css");
        headers.add("X-Content-Type-Options", "nosniff");
       
        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }
    
    @GetMapping("/sounds/{fileName:.+\\.mp3}")
    public ResponseEntity<byte[]> getSoundFile(@PathVariable("fileName") String fileName) throws IOException {
        // Prevent path traversal
    	if (!fileName.matches("^[a-zA-Z0-9._-]+\\.mp3$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Resource resource = new ClassPathResource("static/sounds/" + fileName);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Read file content
        Path path = resource.getFile().toPath();
        byte[] cssContent = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
        headers.add("X-Content-Type-Options", "nosniff");
        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }
    
    @GetMapping("/images/{fileName:.+\\.png}")
    public ResponseEntity<byte[]> getPngFile(@PathVariable("fileName") String fileName) throws IOException {
        // Prevent path traversal
    	if (!fileName.matches("^[a-zA-Z0-9._-]+\\.png$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Resource resource = new ClassPathResource("static/images/" + fileName);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, storage_service.getPreAuthURL("images/" + fileName))
                .build();

        // Read file content
//        Path path = resource.getFile().toPath();
//        byte[] cssContent = Files.readAllBytes(path);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
//        headers.add("X-Content-Type-Options", "nosniff");
//        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
    }
    
    @GetMapping("/js/{fileName:.+\\.js}")
    public ResponseEntity<byte[]> getJsFile(@PathVariable("fileName") String fileName) throws IOException {
        // Prevent path traversal

        if (!fileName.matches("^[a-zA-Z0-9._-]+\\.js$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Load the JS file from classpath
        Resource resource = new ClassPathResource("static/js/" + fileName);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Read file content
        Path path = resource.getFile().toPath();
        byte[] jsContent = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/javascript");
        headers.add("X-Content-Type-Options", "nosniff");
        //headers.add(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        
//        String origin = request.getHeader("Origin");
//        if ("http://localhost:8080".equals(origin)) {  // Change this to your website's domain
//            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        return new ResponseEntity<>(jsContent, headers, HttpStatus.OK);
    }
    
//    @GetMapping("/badges")
//    public String getBadges(Model model) {
//        List<Badge> badges = List.of(
//            new Badge("googlecybersecurity.png", "Google Cybersecurity", "Description for Badge 1"),
//            new Badge("poweredbyOracle.png", "Powered By Oracle", "Description for Badge 2")
//        );
//
//        model.addAttribute("badges", badges);
//        return "badges"; // Your Thymeleaf template name (badges.html)
//    }

}
