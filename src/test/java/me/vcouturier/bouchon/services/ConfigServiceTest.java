package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.enums.EndpointStatut;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.ApplicationRuntimeException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.model.EndpointStatutResponse;
import me.vcouturier.bouchon.services.impl.ConfigServiceImpl;
import me.vcouturier.bouchon.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {

    private ConfigServiceImpl configService;

    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Mock
    private MessageService messageService;

    @Mock
    private EndPointService endPointService;

    @TempDir
    public File configFolder;

    @BeforeEach
    public void init() {
        // ConfigService basic settings
        configService = new ConfigServiceImpl();
        ReflectionTestUtils.setField(configService, "uploadDir", configFolder.getAbsolutePath());
        ReflectionTestUtils.setField(configService, "uploadDirFile", configFolder);
        ReflectionTestUtils.setField(configService, "applicationExceptionFactory", applicationExceptionFactory);
        ReflectionTestUtils.setField(configService, "messageService", messageService);
        ReflectionTestUtils.setField(configService, "endPointService", endPointService);
    }

    @Test
    public void init_test_nominal() throws IOException {
        // Arrange
        ReflectionTestUtils.setField(configService, "uploadDirFile", null);

        // Act
        configService.init();

        // Assert
        File file = (File) ReflectionTestUtils.getField(configService, "uploadDirFile");

        assertNotNull(file, "Config folder should be set");
        assertEquals(configFolder, file, "Config folder should be tempDir");
    }

    @Test
    public void init_test_folder_not_exists() {
        // Arrange
        ReflectionTestUtils.setField(configService, "uploadDir", "/folder/not/exists");
        ReflectionTestUtils.setField(configService, "uploadDirFile", null);

        // ApplicationExceptionFactory mock
        doReturn(new ApplicationRuntimeException("code", "message")).when(applicationExceptionFactory).createApplicationRuntimeException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        Exception exception = assertThrows(ApplicationRuntimeException.class,
                () -> configService.init()
        );

        // Assert
        assertNotNull(exception, "ApplicationRuntimeException should be thrown");
    }

    @Test
    public void init_test_file_not_folder() {
        // Arrange
        File fakeDir = new File(configFolder, "fakeDir");
        ReflectionTestUtils.setField(configService, "uploadDir", fakeDir.getAbsolutePath());
        ReflectionTestUtils.setField(configService, "uploadDirFile", null);

        // ApplicationExceptionFactory mock
        doReturn(new ApplicationRuntimeException("code", "message")).when(applicationExceptionFactory).createApplicationRuntimeException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        Exception exception = assertThrows(ApplicationRuntimeException.class,
                () -> configService.init()
        );

        // Assert
        assertNotNull(exception, "ApplicationRuntimeException should be thrown");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test.yml",
            "test.yaml"
    })
    public void verifyUploadedFile_test_valid(String originalFilename) {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test",
                originalFilename,
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        // Act
        List<String> errors = configService.verifyUploadedFile(file);

        // Assert
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test",
            "test.a",
            "test.",
            StringUtils.SPACE
    })
    @NullSource
    @EmptySource
    public void verifyUploadedFile_test_error(String originalFilename) {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "test",
                originalFilename,
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        // Act
        List<String> errors = configService.verifyUploadedFile(file);

        // Assert
        assertThat(errors).isNotEmpty();
    }

    @Test
    public void uploadEndpointConfigurationFile_firstconfig_ok() throws ApplicationException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.yml",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        String configName = "configName";

        // Act
        File resultFile = configService.uploadEndpointConfigurationFile(uploadedFile, configName);

        // Assert
        LocalDate date = LocalDate.now();
        String expectedConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);

        assertThat(resultFile).isNotNull();
        assertThat(resultFile).exists();
        assertThat(resultFile).isFile();
        assertThat(resultFile.getName()).isEqualTo(expectedConfigName);
    }

    @Test
    public void uploadEndpointConfigurationFile_secondConfigActivated_ok() throws ApplicationException, IOException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.yml",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );


        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // Act
        File resultFile = configService.uploadEndpointConfigurationFile(uploadedFile, configName);

        // Assert
        String expectedConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, "_001");

        assertThat(resultFile).isNotNull();
        assertThat(resultFile).exists();
        assertThat(resultFile).isFile();
        assertThat(resultFile.getName()).isEqualTo(expectedConfigName);
    }

    @Test
    public void uploadEndpointConfigurationFile_secondConfigDeactivated_ok() throws ApplicationException, IOException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.yml",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );


        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY) + ".deactivated";

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // Act
        File resultFile = configService.uploadEndpointConfigurationFile(uploadedFile, configName);

        // Assert
        String expectedConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, "_001");

        assertThat(resultFile).isNotNull();
        assertThat(resultFile).exists();
        assertThat(resultFile).isFile();
        assertThat(resultFile.getName()).isEqualTo(expectedConfigName);
    }

    @Test
    public void uploadEndpointConfigurationFile_secondConfigDeactivated_wrongDeactivatedKeyWord() throws ApplicationException, IOException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.yml",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );


        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY) + ".activated";

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // Act
        File resultFile = configService.uploadEndpointConfigurationFile(uploadedFile, configName);

        // Assert
        String expectedConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);

        assertThat(resultFile).isNotNull();
        assertThat(resultFile).exists();
        assertThat(resultFile).isFile();
        assertThat(resultFile.getName()).isEqualTo(expectedConfigName);
    }

    @Test
    public void uploadEndpointConfigurationFile_differentConfigName() throws ApplicationException, IOException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.yml",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );


        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName + "1", StringUtils.EMPTY);

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // Act
        File resultFile = configService.uploadEndpointConfigurationFile(uploadedFile, configName);

        // Assert
        String expectedConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName, StringUtils.EMPTY);

        assertThat(resultFile).isNotNull();
        assertThat(resultFile).exists();
        assertThat(resultFile).isFile();
        assertThat(resultFile.getName()).isEqualTo(expectedConfigName);
    }

    @Test
    public void uploadEndpointConfigurationFile_throwsException() throws IOException {
        // Arrange
        MultipartFile uploadedFile = new MockMultipartFile(
                "test",
                "test.aze",
                MediaType.TEXT_PLAIN_VALUE,
                "".getBytes()
        );

        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName + "1", StringUtils.EMPTY);

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // ApplicationExceptionFactory mock
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        Exception e = assertThrows(ApplicationException.class, () -> configService.uploadEndpointConfigurationFile(uploadedFile, configName));

        // Assert
        assertThat(e).isNotNull();
    }

    @Test
    public void uploadEndpointConfigurationFile_multipartNull_throwsException() throws IOException {
        // Arrange
        MultipartFile uploadedFile = null;

        String configName = "configName";

        LocalDate date = LocalDate.now();
        String nominalConfigName = MessageFormat.format(ConfigServiceImpl.FILENAME_TEMPLATE, date.format(DateUtils.Formats.DATE_FORMAT), configName + "1", StringUtils.EMPTY);

        Files.createFile(new File(configFolder, nominalConfigName).toPath());

        // ApplicationExceptionFactory mock
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        Exception e = assertThrows(ApplicationException.class, () -> configService.uploadEndpointConfigurationFile(uploadedFile, configName));

        // Assert
        assertThat(e).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file.yml",
            "file.yaml"
    })
    public void deleteEndpointConfigurationFile_withExtension(String filename) throws IOException, ApplicationException {
        // Arrange
        Path fileToDelete = Files.createFile(new File(configFolder, filename).toPath());

        // Act
        assertThat(fileToDelete.toFile().exists()).isTrue();
        Boolean isDeleted = configService.deleteEndpointConfigurationFile(filename);

        // Assert
        assertThat(isDeleted).isTrue();
        assertThat(fileToDelete.toFile().exists()).isFalse();
    }

    @Test
    public void deleteEndpointConfigurationFile_withoutExtension() throws IOException, ApplicationException {
        // Arrange
        String filenameToDelete = "file";

        Path fileToDelete = Files.createFile(new File(configFolder, filenameToDelete + ".yaml").toPath());

        // Act
        assertThat(fileToDelete.toFile().exists()).isTrue();
        Boolean isDeleted = configService.deleteEndpointConfigurationFile(filenameToDelete);

        // Assert
        assertThat(isDeleted).isTrue();
        assertThat(fileToDelete.toFile().exists()).isFalse();
    }

    @Test
    public void deleteEndpointConfigurationFile_fileNotExists() {
        // Arrange
        String filenameToDelete = "file.yaml";

        File fileToDelete = new File(configFolder, filenameToDelete);

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());
        // Act
        assertThat(fileToDelete.exists()).isFalse();
        ApplicationException exception = assertThrows(ApplicationException.class, () -> configService.deleteEndpointConfigurationFile(filenameToDelete));

        // Assert
        assertThat(exception).isNotNull();
    }

    @Test
    public void deleteEndpointConfigurationFile_elementIsNotFile() throws IOException {
        // Arrange
        String filenameToDelete = "file.yaml";

        Path fileToDelete = Files.createDirectory(new File(configFolder, filenameToDelete).toPath());

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());
        // Act
        assertThat(fileToDelete.toFile().exists()).isTrue();
        assertThat(fileToDelete.toFile().isFile()).isFalse();

        ApplicationException exception = assertThrows(ApplicationException.class, () -> configService.deleteEndpointConfigurationFile(filenameToDelete));

        // Assert
        assertThat(exception).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void deleteEndpointConfigurationFile_invalidFilename(String filename) throws IOException {
        // Arrange
        Path fileToDelete = Files.createDirectory(new File(configFolder, "file.yaml").toPath());

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));
        // Act
        assertThat(fileToDelete.toFile().exists()).isTrue();
        assertThat(fileToDelete.toFile().isFile()).isFalse();

        ApplicationException exception = assertThrows(ApplicationException.class, () -> configService.deleteEndpointConfigurationFile(filename));

        // Assert
        assertThat(exception).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {
            0,
            1,
            10,
            100
    })
    public void getAllConfigurationFiles_nominal(int nbFiles) throws IOException {
        // Arrange
        String baseName = "file_";
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < nbFiles; i++) {
            String filename = baseName + i + ".yaml";
            fileList.add(filename);
            Files.createDirectory(new File(configFolder, filename).toPath());
        }

        // Act
        List<String> existingFiles = configService.getAllConfigurationFiles();

        // Assert
        if (nbFiles == 0) {
            assertThat(existingFiles).isEmpty();
        } else {
            assertThat(existingFiles).isNotEmpty();
            assertThat(existingFiles.size()).isEqualTo(nbFiles);
            assertThat(existingFiles).containsExactlyInAnyOrderElementsOf(fileList);
        }
    }

    @Test
    public void reloadAllConfigurationFiles_extensionFilter() throws IOException {
        // Arrange
        Path yamlFile = Files.createFile(new File(configFolder, "file.yaml").toPath());
        Path ymlFile = Files.createFile(new File(configFolder, "file.yml").toPath());
        Files.createFile(new File(configFolder, "file").toPath());
        Files.createFile(new File(configFolder, "file.yml.deactivated").toPath());
        Files.createFile(new File(configFolder, "file.abc").toPath());

        // Mocking endpointService methods
        EndPoint yamlEndpoint = new EndPoint();
        yamlEndpoint.setName("yaml");
        Map<EndPoint, Optional<String>> mapYaml = Map.of(yamlEndpoint, Optional.empty());
        doReturn(mapYaml).when(endPointService).loadEndpointsFromFile(yamlFile.toFile());

        EndPoint ymlEndpoint = new EndPoint();
        ymlEndpoint.setName("yml");
        Map<EndPoint, Optional<String>> mapYml = Map.of(ymlEndpoint, Optional.empty());
        doReturn(mapYml).when(endPointService).loadEndpointsFromFile(ymlFile.toFile());

        // Act
        List<EndpointStatutResponse> endpointsStatut = configService.reloadAllConfigurationFiles();

        // Assert
        verify(endPointService, times(1)).reinitializeEndpoints();
        verify(endPointService, times(1)).loadEndpointsFromFile(yamlFile.toFile());
        verify(endPointService, times(1)).loadEndpointsFromFile(ymlFile.toFile());
        verifyNoMoreInteractions(endPointService);

        List<EndpointStatutResponse> yamlEndpointStatut = endpointsStatut.stream().filter(statut -> "yaml".equals(statut.getEndpointName())).collect(Collectors.toList());
        assertThat(yamlEndpointStatut.size()).isEqualTo(1);
        assertThat(yamlEndpointStatut.get(0).getStatut()).isEqualTo(EndpointStatut.OK);

        List<EndpointStatutResponse> ymlEndpointStatut = endpointsStatut.stream().filter(statut -> "yml".equals(statut.getEndpointName())).collect(Collectors.toList());
        assertThat(ymlEndpointStatut.size()).isEqualTo(1);
        assertThat(ymlEndpointStatut.get(0).getStatut()).isEqualTo(EndpointStatut.OK);
    }

    @Test
    public void reloadAllConfigurationFiles_endpointKo() throws IOException {
        // Arrange
        Path yamlFile = Files.createFile(new File(configFolder, "file.yaml").toPath());

        // Mocking endpointService methods
        EndPoint yamlEndpoint = new EndPoint();
        yamlEndpoint.setName("yaml");
        Map<EndPoint, Optional<String>> mapYaml = Map.of(yamlEndpoint, Optional.of("error"));
        doReturn(mapYaml).when(endPointService).loadEndpointsFromFile(yamlFile.toFile());

        // Act
        List<EndpointStatutResponse> endpointsStatut = configService.reloadAllConfigurationFiles();

        // Assert
        verify(endPointService, times(1)).reinitializeEndpoints();
        verify(endPointService, times(1)).loadEndpointsFromFile(yamlFile.toFile());
        verifyNoMoreInteractions(endPointService);

        List<EndpointStatutResponse> yamlEndpointStatut = endpointsStatut.stream().filter(statut -> "yaml".equals(statut.getEndpointName())).collect(Collectors.toList());
        assertThat(yamlEndpointStatut.size()).isEqualTo(1);
        assertThat(yamlEndpointStatut.get(0).getStatut()).isEqualTo(EndpointStatut.KO);
    }

    @Test
    public void reloadAllConfigurationFiles_noFileAvailable() throws IOException {
        // Arrange

        // Act
        List<EndpointStatutResponse> endpointsStatut = configService.reloadAllConfigurationFiles();

        // Assert
        verify(endPointService, times(1)).reinitializeEndpoints();
        verifyNoMoreInteractions(endPointService);

        assertThat(endpointsStatut).isEmpty();
    }

    @Test
    public void reloadAllConfigurationFiles_directory() throws IOException {
        // Arrange
        Files.createDirectory(new File(configFolder, "file.yaml").toPath());

        // Act
        List<EndpointStatutResponse> endpointsStatut = configService.reloadAllConfigurationFiles();

        // Assert
        verify(endPointService, times(1)).reinitializeEndpoints();
        verifyNoMoreInteractions(endPointService);

        assertThat(endpointsStatut).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file",
            "file.yaml"
    })
    public void activationEndpointConfigurationFile_deactivate_ok(String filenameToDeactivate) throws IOException, ApplicationException {
        // Arrange
        String filename = "file.yaml";
        Files.createFile(new File(configFolder, filename).toPath());

        assertThat(new File(configFolder, filename).exists()).isTrue();
        assertThat(new File(configFolder, filename).isFile()).isTrue();
        assertThat(new File(configFolder, filename + ".deactivated").exists()).isFalse();

        // Act
        boolean res = configService.activationEndpointConfigurationFile(filenameToDeactivate, false);

        // Assert
        assertThat(new File(configFolder, filename).exists()).isFalse();
        assertThat(new File(configFolder, filename + ".deactivated").exists()).isTrue();
        assertThat(new File(configFolder, filename + ".deactivated").isFile()).isTrue();

        assertThat(res).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {
            true,
            false
    })
    public void activationEndpointConfigurationFile_fileNotExists(boolean activate) {
        // Arrange
        String filename = "file";

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> configService.activationEndpointConfigurationFile(filename, activate));

        // Assert
        assertThat(exception).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(booleans = {
            true,
            false
    })
    public void activationEndpointConfigurationFile_fileIsDirectory(boolean activate) throws IOException {
        // Arrange
        String filename = "file";
        Files.createDirectory(new File(configFolder, filename + ".yaml" + (activate ? ".deactivated" : StringUtils.EMPTY)).toPath());

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> configService.activationEndpointConfigurationFile(filename, activate));

        // Assert
        assertThat(exception).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file",
            "file.yaml.deactivated"
    })
    public void activationEndpointConfigurationFile_activate_ok(String filenameToDeactivate) throws IOException, ApplicationException {
        // Arrange
        String filename = "file.yaml";
        Files.createFile(new File(configFolder, filename + ".deactivated").toPath());

        assertThat(new File(configFolder, filename + ".deactivated").exists()).isTrue();
        assertThat(new File(configFolder, filename + ".deactivated").isFile()).isTrue();
        assertThat(new File(configFolder, filename).exists()).isFalse();

        // Act
        boolean res = configService.activationEndpointConfigurationFile(filenameToDeactivate, true);

        // Assert
        assertThat(new File(configFolder, filename + ".deactivated").exists()).isFalse();
        assertThat(new File(configFolder, filename).exists()).isTrue();
        assertThat(new File(configFolder, filename).isFile()).isTrue();

        assertThat(res).isTrue();
    }
}
