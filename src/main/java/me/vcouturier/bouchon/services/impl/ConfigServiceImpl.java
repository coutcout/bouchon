package me.vcouturier.bouchon.services.impl;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.enums.EndpointStatut;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndpointStatutResponse;
import me.vcouturier.bouchon.services.ConfigService;
import me.vcouturier.bouchon.services.EndPointService;
import me.vcouturier.bouchon.services.MessageService;
import me.vcouturier.bouchon.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ConfigServiceImpl implements ConfigService {


    public static final String FILENAME_DEFAULT_EXT = "yaml";
    public static final String FILENAME_DEFAULT_DEACTIVATED_EXT = "deactivated";
    public static final String FILENAME_TEMPLATE = "{0}_{1}_config{2}." + FILENAME_DEFAULT_EXT;

    private final List<String> YAML_EXTENSION = Arrays.asList("yml", "yaml");

    @Autowired
    private MessageService messageService;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private EndPointService endPointService;

    @Value("${bouchon.folder.config}")
    private String uploadDir;

    private File uploadDirFile;

    @PostConstruct
    public void init() throws IOException {
        this.uploadDirFile = new File(this.uploadDir);
        if(!this.uploadDirFile.exists() || !this.uploadDirFile.isDirectory()){
            throw applicationExceptionFactory.createApplicationRuntimeException(MessageEnum.CONFIG_FOLDER_NOT_EXISTS, uploadDir);
        }

        this.reloadAllConfigurationFiles();
    }

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
    public File uploadEndpointConfigurationFile(MultipartFile file, String configName) throws ApplicationException {
        if(file == null){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_UPLOADING_FILE_INVALID);
        }

        log.info(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING, file.getOriginalFilename(), configName));
        List<String> errors = this.verifyUploadedFile(file);
        if(CollectionUtils.isEmpty(errors)){
            File newFile = getFirstFilenameAvailable(configName);
            try{
                file.transferTo(newFile);
            } catch (IOException exception){
                throw applicationExceptionFactory.createApplicationException(exception, MessageEnum.CONFIG_ENDPOINT_UPLOADING_ERROR, file.getOriginalFilename());
            }
            return newFile;
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_UPLOADING_VERIFICATION_FAILED, StringUtils.join(errors));
        }
    }

    private File getFirstFilenameAvailable(String configName) {
        // Endpoint filename generation
        LocalDate date = LocalDate.now();
        String filename = MessageFormat.format(FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);
        File newFile = Path.of(uploadDir, filename).toFile();
        File newFileDeactivated = Path.of(uploadDir, filename + "." + FILENAME_DEFAULT_DEACTIVATED_EXT).toFile();
        int i = 1;
        while(newFile.exists() || newFileDeactivated.exists()) {
            log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOADING_FILENAME_ALREADY_TAKEN, newFile.getName()));
            String fileIncrement = String.format("_%03d", i++);
            filename = MessageFormat.format(FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, fileIncrement);
            newFile = Path.of(uploadDir, filename).toFile();
            newFileDeactivated = Path.of(uploadDir, filename + "." + FILENAME_DEFAULT_DEACTIVATED_EXT).toFile();
        }
        return newFile;
    }

    @Override
    public Boolean deleteEndpointConfigurationFile(String filename) throws ApplicationException {
        log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_DELETE, filename));
        if(StringUtils.isEmpty(filename)){
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_INVALID_FILENAME);
        }

        File fileToDelete = getFileWithOrWithoutExtension(filename, FILENAME_DEFAULT_EXT);

        boolean fileDeleted;
        if(fileToDelete.exists() && fileToDelete.isFile()){
            fileDeleted = fileToDelete.delete();
            if(fileDeleted) {
                log.info(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_DELETE_FILE_DELETED, filename));
            } else {
                log.warn(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_DELETE_FILE_NOT_DELETED, filename));
            }
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_DELETE_FILE_NOT_FOUND, fileToDelete.getName());
        }

        return fileDeleted;
    }

    private File getFileWithOrWithoutExtension(String filename, String defaultExtension) {
        String extension = FilenameUtils.getExtension(filename);
        Path pathToDelete = StringUtils.isNotEmpty(extension) ? Path.of(uploadDir, filename) : Path.of(uploadDir, filename + "." + defaultExtension);
        return pathToDelete.toFile();
    }

    @Override
    public List<String> getAllConfigurationFiles() {
        String[] files = this.uploadDirFile.list();
        return files != null ? Arrays.asList(files) : new ArrayList<>();
    }

    @Override
    public List<EndpointStatutResponse> reloadAllConfigurationFiles() throws IOException {
        List<EndpointStatutResponse> endpoints = new ArrayList<>();
        log.info(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_COMPLETE_RELOAD));

        endPointService.reinitializeEndpoints();

        File[] filesArray = this.uploadDirFile.listFiles();
        List<File> files = ArrayUtils.isEmpty(filesArray) ? new ArrayList<>() : Arrays.stream(filesArray).filter(f -> CollectionUtils.containsAny(YAML_EXTENSION, FilenameUtils.getExtension(f.getName()))).collect(Collectors.toList());
        for(File file: files){
            if(file.exists()
                && file.isFile()
                && file.canRead()
            ){
                endpoints.addAll(
                        endPointService.loadEndpointsFromFile(file).entrySet().stream()
                            .map(entry -> new EndpointStatutResponse(
                                    file.getName(),
                                    entry.getKey().getName(),
                                    entry.getValue().isEmpty() ? EndpointStatut.OK : EndpointStatut.KO,
                                    entry.getValue().orElse(null)))
                            .sorted(Comparator.comparing(EndpointStatutResponse::getFilename)
                                            .thenComparing(EndpointStatutResponse::getEndpointName)
                                            .thenComparing(EndpointStatutResponse::getStatut))
                            .collect(Collectors.toList())
                        );
            }
        }

        return endpoints;
    }

    @Override
    public boolean activationEndpointConfigurationFile(String configFileName, boolean activate) throws ApplicationException {
        log.debug(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_ACTIVATION, configFileName));
        File fileToModify = getFileWithOrWithoutExtension(configFileName, activate ? FILENAME_DEFAULT_EXT + "." + FILENAME_DEFAULT_DEACTIVATED_EXT : FILENAME_DEFAULT_EXT);
        boolean success;

        if(fileToModify.exists() && fileToModify.isFile()){
            String filePath = fileToModify.getAbsolutePath();
            String newFilename = activate ?
                    filePath.substring(0, filePath.length() - (FILENAME_DEFAULT_DEACTIVATED_EXT.length() + 1)) // +1 because of the '.' to remove too
                    : filePath + "." + FILENAME_DEFAULT_DEACTIVATED_EXT;
            success = fileToModify.renameTo(Path.of(newFilename).toFile());
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.CONFIG_ENDPOINT_ACTIVATION_FILE_NOT_FOUND, fileToModify.getName());
        }

        return success;
    }
}
