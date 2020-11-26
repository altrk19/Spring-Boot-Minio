package com.spring.minio.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileListResponseResource {
    private FileListResponse fileList;

    public FileListResponseResource() {
    }

    public FileListResponseResource(FileListResponse fileList) {
        this.fileList = fileList;
    }

    public FileListResponse getFileList() {
        return fileList;
    }

    public void setFileList(FileListResponse fileList) {
        this.fileList = fileList;
    }
}
