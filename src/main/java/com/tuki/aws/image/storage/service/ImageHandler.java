package com.tuki.aws.image.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageHandler {
    private final BucketService bucketService;
    private final RecognizerService recognizerService;
    private final StorageService storageService;

    @Value("${aws.s3Bucket.name}")
    private String bucketName;
    @Value("${aws.dynamoDB.name}")
    private String tableName;

    public void uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String imageId = UUID.randomUUID().toString();
        String key = imageId + ".jpg";

        bucketService.uploadFile(file, key, bucketName);

        List<String> labels = recognizerService.detectLabels(key, bucketName);

        storageService.saveMetadataToDB(imageId, key, labels,  bucketName, tableName);
    }

    public List<String> searchImages(String keyword) {
        return storageService.searchMetadata(keyword, bucketName, tableName);
    }
}
