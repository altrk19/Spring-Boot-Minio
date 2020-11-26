package com.spring.minio.controller;

import com.spring.minio.resource.FileListResponseResource;
import com.spring.minio.resource.FileResponseResource;
import com.spring.minio.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String CONTENT_DISPOSITION = "Content-disposition";
    private static final String ATTACHMENT_FILE_NAME = "attachment; filename=\"";
    private static final String QUOTE = "\"";

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("{userId}/files")
    public ResponseEntity<FileListResponseResource> getUserFiles(@PathVariable String userId,
                                                                 @RequestParam(value = "max", required = false,
                                                                         defaultValue = "20") Integer max,
                                                                 @RequestParam(value = "next", required = false,
                                                                         defaultValue = "0") Integer next) {
        log.info("Received get files request for user {}", userId);
        FileListResponseResource fileListResponse = fileService.getUserFiles(userId, max, next);
        log.info("Request completed to get files request for user {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(fileListResponse);
    }

    @PostMapping("{userId}/files")
    public ResponseEntity<FileResponseResource> uploadFile(@PathVariable String userId,
                                                           @RequestParam(value = "file") MultipartFile file) {
        log.info("Received upload event file request for user {} with fileName {}", userId,
                file.getOriginalFilename());
        FileResponseResource fileResponse = fileService.uploadFile(userId, file);
        log.info("Request completed to upload event file request for user {} with fileName {}", userId,
                file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponse);
    }

    @GetMapping("{userId}/files/{fileId}")
    public ResponseEntity<?> getUserFileByFileId(@PathVariable String userId,
                                                 @PathVariable String fileId) {
        log.info("Received get file by fileId request for user {} with fileId {}", userId, fileId);
        FileResponseResource fileResponse;
        try {
            fileResponse = fileService.getUserFileByFileId(fileId, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
        log.info("Request completed to get file by fileId request for user {} with fileId {}", userId, fileId);
        return ResponseEntity.status(HttpStatus.OK).body(fileResponse);
    }

    @DeleteMapping("{userId}/files/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable String userId,
                                             @PathVariable String fileId) {
        log.info("Received delete file request for user {} with fileId {}", userId, fileId);
        fileService.deleteFile(fileId);
        log.info("Received delete file request for user {} with fileId {}", userId, fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}/files/{fileId}/download")
    public ResponseEntity<ByteArrayResource> downloadUserFile(@PathVariable String userId,
                                                              @PathVariable String fileId) {
        log.info("Received download file request for user {} with fileId {}", userId, fileId);
        byte[] data = fileService.downloadFile(fileId);
        ByteArrayResource resource = new ByteArrayResource(data);
        log.info("Request completed to download file request for user {} with fileId {}", userId, fileId);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM)
                .header(CONTENT_DISPOSITION, ATTACHMENT_FILE_NAME + fileId + QUOTE)
                .body(resource);
    }
}
