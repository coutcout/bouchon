package me.vcouturier.bouchon.services.impl;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.services.ConfigService;
import me.vcouturier.bouchon.services.MessageService;
import me.vcouturier.bouchon.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ConfigServiceImpl implements ConfigService {

    public static final String FILENAME_TEMPLATE = "{0}_{1}_config{2}.yaml";
    private final List<String> YAML_EXTENSION = Arrays.asList("yml", "yaml");

    @Autowired
    private MessageService messageService;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Value("${bouchon.folder.config}")
    private String uploadDir;

    @Override
    public List<String> verifyUploadedFile(MultipartFile file){
        log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING_VERIFICATION, file.getOriginalFilename()));
        List<String> errors = new ArrayList<>();

        // Verification of the extension
        if(!CollectionUtils.containsAny(YAML_EXTENSION, FilenameUtils.getExtension(file.getOriginalFilename()))){
            log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING_VERIFICATION_ERROR, "extension"));
            errors.add(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_FILE_UPLOAD_EXTENSION_ERROR, StringUtils.join(YAML_EXTENSION)));
        }

        return errors;
    }

    @Override
    public File uploadFile(MultipartFile file, String configName) throws ApplicationException {
        log.info(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING, file.getOriginalFilename(), configName));
        List<String> errors = this.verifyUploadedFile(file);
        if(CollectionUtils.isEmpty(errors)){
            File uploadDirFolder = new File(this.uploadDir);
            if(uploadDirFolder.exists() && uploadDirFolder.isDirectory()){
                File newFile = getFirstFilenameAvailable(configName);
                try{
                    file.transferTo(newFile);
                } catch (IOException exception){
                    throw applicationExceptionFactory.createApplicationException(exception, MessageEnum.CONFIG_ENDPOINT_UPLOADING_ERROR, file.getOriginalFilename());
                }

                return newFile;
            } else {
                throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_FOLDER_NOT_EXISTS, uploadDir);
            }
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_UPLOADING_VERIFICATION_FAILED, StringUtils.join(errors));
        }
    }

    private File getFirstFilenameAvailable(String configName) {
        // Endpoint filename generation
        LocalDate date = LocalDate.now();
        String filename = MessageFormat.format(FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);
        File newFile = Path.of(uploadDir, filename).toFile();
        int i = 1;
        while(newFile.exists()) {
            log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING_FILENAME_ALREADY_TAKEN, newFile.getName()));
            String fileIncrement = String.format("_%03d", i++);
            filename = MessageFormat.format(FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, fileIncrement);
            newFile = Path.of(uploadDir, filename).toFile();
        }
        return newFile;
    }
}
