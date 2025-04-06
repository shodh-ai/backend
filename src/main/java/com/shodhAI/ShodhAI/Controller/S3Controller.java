package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Service.S3StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3StorageService s3Service;

    public S3Controller(S3StorageService s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);

        String fileUrl = s3Service.uploadFile(tempFile, file.getOriginalFilename());
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<URL> getPresignedUrl(@RequestParam("key") String key) {
        URL presignedUrl = s3Service.getPresignedUrl(key);
        return ResponseEntity.ok(presignedUrl);
    }
}
