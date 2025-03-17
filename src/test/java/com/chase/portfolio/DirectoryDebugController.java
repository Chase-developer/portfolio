package com.chase.portfolio;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DirectoryDebugController {
	
	@GetMapping("/list-static-web")
    public List<String> listStaticWebPaths() {
        String webPath = "/"; // Web path starts from root
        String realPath = "src/main/resources/static";
        List<String> files = new ArrayList<>();
        listFilesRecursive(new File(realPath), webPath, files);
        return files;
    }

	@GetMapping("/list-static")
    public List<String> listStaticFiles() {
        String path = new File("src/main/resources/static").getAbsolutePath();
        List<String> files = new ArrayList<>();
        listFilesRecursive(new File(path), "", files);
        return files;
    }

    @GetMapping("/list-templates")
    public List<String> listTemplateFiles() {
        String path = new File("src/main/resources/templates").getAbsolutePath();
        List<String> files = new ArrayList<>();
        listFilesRecursive(new File(path), "", files);
        return files;
    }

    private void listFilesRecursive(File directory, String parentPath, List<String> fileList) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                String filePath = parentPath + "/" + file.getName();
                fileList.add(filePath);
                if (file.isDirectory()) {
                    listFilesRecursive(file, filePath, fileList);
                }
            }
        }
    }
}
