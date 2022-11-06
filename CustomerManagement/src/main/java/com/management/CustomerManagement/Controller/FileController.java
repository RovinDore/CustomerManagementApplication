package com.management.CustomerManagement.Controller;

import com.management.CustomerManagement.Exception.StorageException;
import com.management.CustomerManagement.Service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Autowired
    private StorageService storageService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/uploadFile")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws StorageException {
        Path destinationFile = storageService.storeGetDestination(file);

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return destinationFile.getFileName().toString();
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        String fileExt = FilenameUtils.getExtension(file.getFilename());

        if(fileExt == null) throw new StorageException("Something went wrong retrieving file");

        String contentType = switch (fileExt) {
            case "pdf" -> "application/pdf";
            case "jpg" -> "image/jpeg";
            default -> "multipart/form-data";
        };

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
    }

}
