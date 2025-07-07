package com.tuki.aws.image.storage.controller;

import com.tuki.aws.image.storage.service.ImageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ImageController {
    private final ImageHandler imageHandler;

    @PostMapping("/uploadImage")
    public void uploadImage(@RequestParam MultipartFile file) {
        imageHandler.uploadImage(file);
    }

    @GetMapping("/search")
    public List<String> searchImages(@RequestParam String keyword) {
        return imageHandler.searchImages(keyword);
    }


}
