package com.tetris.tetrisburger_backend.domain.port.out;


import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {

   ImageUploadResult uploadUserImage(MultipartFile file) throws Exception;
  void deleteImage(String imageKey);
  String getImageUrl(String imageKey);
}
