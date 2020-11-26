package com.spring.minio.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileResponseResource {
    private FileResponse file;

    public FileResponseResource() {
    }

    public FileResponseResource(FileResponse file) {
        this.file = file;
    }

    public FileResponse getFile() {
        return file;
    }

    public void setFile(FileResponse file) {
        this.file = file;
    }
}
