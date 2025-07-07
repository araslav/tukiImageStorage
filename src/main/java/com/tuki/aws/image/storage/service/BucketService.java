package com.tuki.aws.image.storage.service;

import org.springframework.web.multipart.MultipartFile;

public interface BucketService {
    void uploadFile(MultipartFile file, String key, String bucketName);
}
