package com.chase.portfolio.controllers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.chase.portfolio.services.OCIStorageService;

import jakarta.annotation.Nullable;
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
	
	private static boolean contentExists(String path)
	{
		return new ClassPathResource(path).exists();
	}
	
	private static byte[] getContent(String path) throws IOException
	{
		Resource resource = new ClassPathResource(path);
		if (!resource.exists()) {
            return null;
        }
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
	}
	
//	private static boolean isValidPath(String directory, String fileExtension, String filePath) throws IOException {
//	    // Prevent path traversal attacks (regex + normalized path check)
//	    String regex = fileExtension.isEmpty() ? "^[a-zA-Z0-9/_\\.\\-]+$" : "^[a-zA-Z0-9/_\\.\\-]+\\." + fileExtension + "$";
//	    if (!filePath.matches(regex))
//	        return false;
//
//	    String staticPath = "static/" + directory;
//	    Path basePath = Paths.get(staticPath).toAbsolutePath().normalize();
//	    Path requestedPath = basePath.resolve(filePath).normalize();
//	    
//	    // Ensure the file has the correct extension if specified
//	    if (!fileExtension.isEmpty() && !filePath.endsWith("." + fileExtension))
//	        return false;
//	    
//	    // Ensure the requested path is within the allowed directory
//	    if (!requestedPath.startsWith(basePath))
//	        return false;
//	    
//	    // Check for symbolic links in the full path
//	    Path currentPath = requestedPath;
//	    while (currentPath != null && !currentPath.equals(basePath)) {
//	        if (Files.isSymbolicLink(currentPath)) {
//	            return false; // Reject if any part of the path is a symlink
//	        }
//	        currentPath = currentPath.getParent();
//	    }
//	    
//	    return true;
//	}
	
	private static boolean isValidPath(String directory, String filePath) throws IOException {
		return isValidPath(directory, null, filePath);
	}
    
	private static boolean isValidPath(String directory, @Nullable String fileExtension, String filePath) throws IOException {
	    // Prevent path traversal attacks (regex + normalized path check)
	    String regex = fileExtension == null ? "^[a-zA-Z0-9/_\\.\\-]+$" : "^[a-zA-Z0-9/_\\.\\-]+\\." + fileExtension + "$";
	    if (!filePath.matches(regex))
	        return false;

	    String staticPath = "static/" + directory;
	    Path basePath = Paths.get(staticPath).toAbsolutePath().normalize();
	    Path requestedPath = basePath.resolve(filePath).normalize();
	    
	    // Ensure the file has the correct extension if specified
	    if (fileExtension != null && !filePath.endsWith("." + fileExtension))
	        return false;
	    
	    // Ensure the requested path is within the allowed directory
	    if (!requestedPath.startsWith(basePath))
	        return false;
	    
	    return true;
	}
	
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
	
	
	
//    @GetMapping("/css/**")
//    public ResponseEntity<byte[]> getCssFile(HttpServletRequest request) throws IOException {
//        
//        String filePath = request.getRequestURI().replaceFirst("/css/", "");
//
//        // Prevent path traversal attacks
//        if (!isValidPath("css", filePath))
//        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//
//        // Read the CSS file as bytes
//        byte[] cssContent = getContent("static/css/" + filePath);
//        if (cssContent == null)
//        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//
//        // Set response headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "text/css");
//        headers.add("X-Content-Type-Options", "nosniff");
//
//        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
//
//    }
    
    
    @GetMapping("/images/**")
    public ResponseEntity<byte[]> getPngFile(HttpServletRequest request) throws IOException {
        
        String filePath = request.getRequestURI().replaceFirst("/images/", "");

        // Prevent path traversal attacks
        if (!isValidPath("images", "png", filePath))
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // Read the CSS file as bytes
        if (!contentExists("static/images/" + filePath))
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, storage_service.getPreAuthURL("images/" + filePath))
                .build();

    }
    
    @GetMapping("/fonts/**")
    public ResponseEntity<byte[]> getFontFile(HttpServletRequest request) throws IOException {
        
        String filePath = request.getRequestURI().replaceFirst("/fonts/", "");

        // Prevent path traversal attacks
        if (!isValidPath("fonts", filePath))
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // Read the CSS file as bytes
        if (!contentExists("static/fonts/" + filePath))
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, storage_service.getPreAuthURL("fonts/" + filePath))
                .build();

    }
    
    @GetMapping("/texts/**")
    public ResponseEntity<byte[]> getTextFile(HttpServletRequest request) throws IOException {
        
        String filePath = request.getRequestURI().replaceFirst("/texts/", "");

        // Prevent path traversal attacks
        if (!isValidPath("texts", "txt", filePath))
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // Read the CSS file as bytes
        if (!contentExists("static/texts/" + filePath))
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, storage_service.getPreAuthURL("texts/" + filePath))
                .build();

    }
    
    
    
//    @GetMapping("/js/**")
//    public ResponseEntity<byte[]> getJsFile(HttpServletRequest request) throws IOException {
//        
//        String filePath = request.getRequestURI().replaceFirst("/js/", "");
//
//        // Prevent path traversal attacks
//        if (!isValidPath("js", filePath))
//        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//
//        // Read the CSS file as bytes
//        byte[] cssContent = getContent("static/js/" + filePath);
//        if (cssContent == null)
//        	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//
//        // Set response headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "application/javascript");
//        headers.add("X-Content-Type-Options", "nosniff");
//
//        return new ResponseEntity<>(cssContent, headers, HttpStatus.OK);
//
//    }

}
