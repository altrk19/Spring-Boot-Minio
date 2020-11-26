package com.spring.minio.service;

import com.spring.minio.resource.FileResponse;

import java.util.List;

public interface MinioFileService {
    List<FileResponse> getUserFiles(String userId);
    String getObjectUrl(String fileId);
    byte[] downloadFile(String fileId);

}
