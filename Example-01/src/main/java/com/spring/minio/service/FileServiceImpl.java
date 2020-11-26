package com.spring.minio.service;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.spring.minio.resource.FileListResponse;
import com.spring.minio.resource.FileListResponseResource;
import com.spring.minio.resource.FileResponse;
import com.spring.minio.resource.FileResponseResource;
import com.spring.minio.util.FileUtil;
import com.spring.minio.util.PathUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final MinioService minioService;
    private final MinioFileService minioFileService;

    public FileServiceImpl(MinioService minioService, MinioFileService minioFileService) {
        this.minioService = minioService;
        this.minioFileService = minioFileService;
    }

    @Override
    public FileListResponseResource getUserFiles(String userId, Integer max, Integer next) {
        log.debug("Handling get user files for user {}", userId);
        String resourceUrl = PathUtil.generateResourceUrl(userId);
        List<FileResponse> fileResponseList = minioFileService.getUserFiles(userId);
        int start = Math.min(next * max, fileResponseList.size());
        int end = Math.min((next + 1) * max, fileResponseList.size());
        FileListResponse fileListResponse = new FileListResponse(fileResponseList.subList(start, end), end - start,
                PathUtil.generateNext(resourceUrl, max, next), resourceUrl);
        return new FileListResponseResource(fileListResponse);
    }

    @Override
    public FileResponseResource uploadFile(String userId, MultipartFile file) {
        checkFile(file);
        String fileName = file.getOriginalFilename();
        log.debug("Handling file upload for user {} fileName {} ", userId, fileName);
        String fileId = FileUtil.generateFileId(userId, fileName);

        uploadFile(file, fileId, userId);

        String fileFormat = FileUtil.getFileFormat(fileName);
        String downloadURL = PathUtil.generateDownloadUrl(userId, fileId);
        String resourceUrl = PathUtil.generateResourceUrlSingleFile(userId, fileId);

        checkFileId(fileId);
        FileResponse fileResponse =
                new FileResponse(fileId, fileName, fileFormat, (int) file.getSize(), downloadURL, resourceUrl);
        return new FileResponseResource(fileResponse);
    }

    @Override
    public FileResponseResource getUserFileByFileId(String fileId, String userId) {
        log.debug("Handling get user file by fileId for user {} fileId {}", userId, fileId);
        checkUserId(fileId, userId);
        List<FileResponse> fileResponseList = minioFileService.getUserFiles(userId).stream()
                .filter(fileResponse -> fileResponse.getFileId().equals(fileId)).collect(Collectors.toList());
        checkFileResponseList(fileResponseList, fileId);
        return new FileResponseResource(fileResponseList.get(0));
    }

    @Override
    public void deleteFile(String fileId) {
        log.debug("Handling delete user file with fileId {}", fileId);
        try {
            minioService.remove(Paths.get(fileId));
        } catch (MinioException e) {
            log.debug("Failed to remove file with fileId : {}", fileId, e);
            throw new RuntimeException("Failed to remove file");
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        log.debug("Starting to download file for with fileId : {}", fileId);
        return minioFileService.downloadFile(fileId);
    }

    private void uploadFile(MultipartFile file, String fileId, String userId) {
        log.debug("Starting to upload file for user {} with fileId : {}", userId, fileId);
        try {
            minioService.upload(Paths.get(fileId), file.getInputStream(), file.getContentType());
        } catch (MinioException e) {
            log.debug("The file cannot be upload on the internal storage. Please retry later", e);
            throw new RuntimeException("The file cannot be upload on the internal storage. Please retry later");
        } catch (IOException e) {
            log.debug("The file cannot be read", e);
            throw new RuntimeException("The file cannot be read");
        }
    }

    private void checkFile(MultipartFile file) {
        if (Objects.isNull(file)) {
            log.debug("File is null");
            throw new RuntimeException("File is null");
        }

        try {
            if (file.getBytes().length <= 0) {
                log.debug("File is empty");
                throw new RuntimeException("File is empty");
            }
        } catch (IOException e) {
            log.debug("Unable to read the file");
            e.printStackTrace();
            throw new RuntimeException("Unable to read the file {}");
        }
    }

    private void checkFileId(String fileId) {
        if (minioFileService.getObjectUrl(fileId) == null) {
            log.debug("Failed to upload file to Minio");
            throw new RuntimeException("Failed to upload file to Minio");
        }
    }

    private void checkUserId(String fileId, String userId) {
        if (!StringUtils.substringBefore(fileId, "-").equals(userId)) {
            log.debug("UserId cannot be different");
            throw new RuntimeException("UserId cannot be different");
        }
    }

    private void checkFileResponseList(List<FileResponse> fileResponseList, String fileId) {
        if (fileResponseList.isEmpty()) {
            log.debug("User does not have file with fileId {}", fileId);
            throw new IllegalArgumentException("User does not have file");
        }
    }
}
