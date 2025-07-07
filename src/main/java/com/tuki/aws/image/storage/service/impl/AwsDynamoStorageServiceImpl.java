package com.tuki.aws.image.storage.service.impl;

import com.tuki.aws.image.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AwsDynamoStorageServiceImpl implements StorageService {
    private final DynamoDbClient dynamoDbClient;
    private final S3Presigner s3Presigner;

    @Override
    public void saveMetadataToDB(String imageId, String key, List<String> labels,
                                       String bucketName, String tableName) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("imageId", AttributeValue.fromS(imageId));
        item.put("s3Path", AttributeValue.fromS("s3://" + bucketName + "/" + key));
        item.put("labels", AttributeValue.fromL(
                labels.stream()
                        .map(AttributeValue::fromS)
                        .collect(Collectors.toList())
        ));

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }


    @Override
    public List<String> searchMetadata(String key, String bucketName, String tableName) {
        List<Map<String, AttributeValue>> items = dynamoDbClient.scan(ScanRequest.builder()
                .tableName(tableName)
                .build()).items();

        List<String> signedUrls = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            List<AttributeValue> labels = item.get("labels").l();

            boolean match = labels.stream()
                    .anyMatch(label -> label.s().equalsIgnoreCase(key));

            if (match) {
                String s3Path = item.get("s3Path").s();
                String signedUrl = generateSignedUrl(s3Path, bucketName);
                signedUrls.add(signedUrl);
            }
        }

        return signedUrls;
    }

    private String generateSignedUrl(String s3Path, String bucketName) {
        String key = s3Path.replace("s3://" + bucketName + "/", "");

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest signedRequest = s3Presigner.presignGetObject(presignRequest);
        return signedRequest.url().toString();
    }
}
