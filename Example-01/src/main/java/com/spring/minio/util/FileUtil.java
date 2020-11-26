package com.spring.minio.util;

import org.apache.commons.lang3.StringUtils;

public final class FileUtil {
    private static final String DASH = "-";
    private static final String DOT = ".";
    private FileUtil() {
    }

    public static String generateFileId(String userId, String fileName) {
        return new StringBuilder().append(userId).append(DASH).append(System.currentTimeMillis()).append(DASH)
                .append(fileName).toString();
    }

    public static String generateFileIdWithFileType(String userId, String fileName, String fileType) {
        return new StringBuilder().append(userId).append(DASH).append(fileName).append(DOT).append(fileType).toString();
    }

    public static String getFileFormat(String fileName) {
        return StringUtils.substringAfterLast(fileName, DOT);
    }

    public static String getFileName(String fileId) {
        return StringUtils.substringAfterLast(fileId, DASH);
    }
}
