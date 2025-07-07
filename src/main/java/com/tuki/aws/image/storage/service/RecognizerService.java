package com.tuki.aws.image.storage.service;

import java.util.List;

public interface RecognizerService {
    List<String> detectLabels(String key, String bucketName);
}
