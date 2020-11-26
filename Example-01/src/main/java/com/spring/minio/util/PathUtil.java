package com.spring.minio.util;

public final class PathUtil {
    private static final String FILES = "files";
    private static final String DOWNLOAD = "download";
    private static final String MAX = "max";
    private static final String NEXT = "next";
    private static final String API = "api";

    private PathUtil() {
    }

    public static String generateDownloadUrl(String userId, String fileId) {
        return new StringBuilder().append("/").append(API).append("/").append(userId).append("/").append(FILES)
                .append("/").append(fileId).append("/").append(DOWNLOAD).toString();
    }

    public static String generateResourceUrl(String userId) {
        return new StringBuilder().append("/").append(API).append("/").append(userId).append("/").append(FILES)
                .toString();
    }

    public static String generateResourceUrlSingleFile(String userId, String fileId) {
        return new StringBuilder().append("/").append(API).append("/").append(userId).append("/").append(FILES)
                .append("/").append(fileId).toString();
    }

    public static String generateNext(String resourceUrl, Integer max, Integer next) {
        return new StringBuilder().append(resourceUrl).append("?").append(MAX).append("=").append(max).append("&")
                .append(NEXT).append("=").append(next + 1).toString();
    }

}
