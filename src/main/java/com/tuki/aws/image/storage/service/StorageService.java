package com.tuki.aws.image.storage.service;

import java.util.List;

public interface StorageService {
    void saveMetadataToDB(String imageId, String key, List<String> labels,
                                 String bucketName, String tableName);

    List<String> searchMetadata(String key, String bucketName, String tableName);
}
