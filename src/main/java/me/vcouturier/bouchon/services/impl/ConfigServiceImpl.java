package me.vcouturier.bouchon.services.impl;

import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.services.ConfigService;
import me.vcouturier.bouchon.services.MessageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigServiceImpl implements ConfigService {

    private final List<String> YAML_EXTENSION = Arrays.asList("yml", "yaml");

    @Autowired
    private MessageService messageService;

    @Value("${bouchon.folder.config}")
    private String uploadDir;

    private File uploadDirFile;

    @Override
    public List<String> verifyUploadedFile(MultipartFile file){
        List<String> errors = new ArrayList<>();

        // Verification of the extension
        if(!CollectionUtils.containsAny(YAML_EXTENSION, FilenameUtils.getExtension(file.getOriginalFilename()))){
            errors.add(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_FILE_UPLOAD_EXTENSION_ERROR, StringUtils.join(YAML_EXTENSION)));
        }

        return errors;
    }
}
