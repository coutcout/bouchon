package me.vcouturier.bouchon.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConfigService {

    List<String> verifyUploadedFile(MultipartFile file);
}
