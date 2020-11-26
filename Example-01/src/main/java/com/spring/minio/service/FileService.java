package com.spring.minio.service;

import com.spring.minio.resource.FileListResponseResource;
import com.spring.minio.resource.FileResponseResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileListResponseResource getUserFiles(String userId, Integer max, Integer next);
    FileResponseResource uploadFile(String userId, MultipartFile file);
    FileResponseResource getUserFileByFileId(String fileId, String userId);
    void deleteFile(String fileId);
    byte[] downloadFile(String fileId);
}
