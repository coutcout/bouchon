package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ConfigService {

    List<String> verifyUploadedFile(MultipartFile file);

    File uploadEndpointConfigurationFile(MultipartFile file, String configName) throws ApplicationException;

    void deleteEndpointConfigurationFile(String filename) throws ApplicationException;
}
