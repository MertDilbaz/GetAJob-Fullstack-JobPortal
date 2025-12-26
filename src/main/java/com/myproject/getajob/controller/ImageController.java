package com.myproject.getajob.controller;

import com.myproject.getajob.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.saveImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error uploading image");
        }
    }

    @GetMapping(value = "/{filename}")
    @SuppressWarnings("null")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        System.out.println("Requested image: " + filename);
        try {
            byte[] image = imageService.getImage(filename);
            @SuppressWarnings("null")
            MediaType mediaType = org.springframework.http.MediaTypeFactory
                    .getMediaType(filename)
                    .orElse(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok().contentType(mediaType).body(image);
        } catch (IOException e) {
            System.err.println("Error reading image: " + filename + " - " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
