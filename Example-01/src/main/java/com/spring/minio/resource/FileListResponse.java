package com.spring.minio.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileListResponse {
    private List<FileResponse> file;
    private Integer total;
    private String next;
    private String resourceURL;

    public FileListResponse(List<FileResponse> file, Integer total, String next, String resourceURL) {
        this.file = file;
        this.total = total;
        this.next = next;
        this.resourceURL = resourceURL;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public List<FileResponse> getFile() {
        return file;
    }

    public void setFile(List<FileResponse> file) {
        this.file = file;
    }
}