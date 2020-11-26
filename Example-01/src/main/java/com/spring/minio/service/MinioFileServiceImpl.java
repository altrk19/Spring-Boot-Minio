package com.spring.minio.service;

import com.jlefebure.spring.boot.minio.MinioConfigurationProperties;
import com.spring.minio.resource.FileResponse;
import com.spring.minio.util.FileUtil;
import com.spring.minio.util.PathUtil;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioFileServiceImpl implements MinioFileService {
    private static final Logger log = LoggerFactory.getLogger(MinioFileServiceImpl.class);

    private final MinioClient minioClient;
    private final MinioConfigurationProperties configurationProperties;

    public MinioFileServiceImpl(MinioClient minioClient,
                                MinioConfigurationProperties configurationProperties) {
        this.minioClient = minioClient;
        this.configurationProperties = configurationProperties;
    }

    @Override
    public List<FileResponse> getUserFiles(String userId) {
        log.debug("Handling get user files for user {}", userId);
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(configurationProperties.getBucket(), userId);
            ArrayList<FileResponse> fileResponseList = new ArrayList<>();
            for (Result<Item> resultItem : results) {
                Item item = resultItem.get();
                String fileId = item.objectName();
                String fileName = FileUtil.getFileName(fileId);
                String fileFormat = FileUtil.getFileFormat(fileId);
                String downloadUrl = PathUtil.generateDownloadUrl(userId, fileId);
                String resourceUrl = PathUtil.generateResourceUrlSingleFile(userId, fileId);
                fileResponseList
                        .add(new FileResponse(fileId, fileName, fileFormat, (int) item.objectSize(), downloadUrl,
                                resourceUrl));
            }
            return fileResponseList;
        } catch (Exception e) {
            log.debug("Failed to get user file with userId : {}", userId, e);
            throw new RuntimeException("Exception occurred while getting files from Minio");
        }
    }

    @Override
    public String getObjectUrl(String fileId) {
        log.debug("Handling get file url with fileId {}", fileId);
        try {
            return minioClient.getObjectUrl(configurationProperties.getBucket(), fileId);
        } catch (Exception e) {
            log.debug("Failed to get file url with fileId : {}", fileId, e);
            return null;
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        log.debug("Handling download file with fileId {}", fileId);
        try {
            InputStream obj = minioClient.getObject(configurationProperties.getBucket(), fileId);
            byte[] content = IOUtils.toByteArray(obj);
            obj.close();
            return content;
        } catch (Exception e) {
            log.debug("Failed to get file with fileId : {}", fileId, e);
            throw new RuntimeException("Failed to get file");
        }
    }
}
