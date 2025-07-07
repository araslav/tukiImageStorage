package com.tuki.aws.image.storage.service.impl;

import com.tuki.aws.image.storage.service.RecognizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AwsRecognizerServiceImpl implements RecognizerService {
    private final RekognitionClient rekognitionClient;

    @Override
    public List<String> detectLabels(String key, String bucketName) {
        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucketName)
                                .name(key)
                                .build())
                        .build())
                .maxLabels(10)
                .build();

        try {
            return rekognitionClient.detectLabels(request)
                    .labels()
                    .stream()
                    .map(Label::name)
                    .collect(Collectors.toList());
        } catch (RekognitionException e) {
            throw new RuntimeException("Error analyzing image with Rekognition", e);
        }
    }
}
