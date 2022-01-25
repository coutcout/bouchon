package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.services.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;

    @TempDir
    public File tmpDataDir;

    private FileServiceImpl fileService;

    @BeforeEach
    public void init(){
        fileService = new FileServiceImpl();
        ReflectionTestUtils.setField(fileService, "applicationExceptionFactory", applicationExceptionFactory);
        ReflectionTestUtils.setField(fileService, "messageService", messageService);
        ReflectionTestUtils.setField(fileService, "dataFolderPath", tmpDataDir.getAbsolutePath());
    }

    @Test
    public void createFolder_nominal() throws ApplicationException {
        // Arrange
        String folderName = "dir";

        Path newDir = Path.of(tmpDataDir.getAbsolutePath(), folderName);
        assertThat(newDir.toFile()).doesNotExist();

        // Act
        boolean res = fileService.createFolder(folderName);

        // Assert
        assertThat(res).isTrue();
        assertThat(newDir.toFile()).exists();
        assertThat(Files.isDirectory(newDir)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void createFolder_invalidFolderName(String folderName) {
        // Arrange

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> fileService.createFolder(folderName));

        // Assert
        assertThat(exception).isNotNull();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.CONFIG_UPLOAD_INVALID_FOLDERNAME));
    }

    @Test
    public void createFolder_dataFolderNotExist() {
        // Arrange
        String folderName = "test";
        Path invalidDataFolder = Path.of(tmpDataDir.getAbsolutePath(), "err" );

        // Mocking
        ReflectionTestUtils.setField(fileService, "dataFolderPath", invalidDataFolder.toFile().getAbsolutePath());
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> fileService.createFolder(folderName));

        // Assert
        assertThat(exception).isNotNull();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_DATAFOLDER_MISSING));
    }

    @Test
    public void createFolder_dataFolderIsNotDir() throws IOException {
        // Arrange
        String folderName = "test";
        Path invalidFile = Files.createFile(new File(tmpDataDir, folderName).toPath());

        assertThat(invalidFile.toFile()).exists();
        assertThat(invalidFile.toFile()).isFile();

        // Mocking
        ReflectionTestUtils.setField(fileService, "dataFolderPath", invalidFile.toFile().getAbsolutePath());
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> fileService.createFolder(folderName));

        // Assert
        assertThat(exception).isNotNull();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_DATAFOLDER_MISSING));
    }

    @Test
    public void getFileNameFromTemplate_emptyTemplate() throws ApplicationException {
        // Arrange
        String template = "";

        Map<String, String> parameters = Map.of(
                "var1", "value1"
        );

        // Act
        String res = fileService.getFileNameFromTemplate(template, parameters);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo("");
    }

    @Test
    public void getFileNameFromTemplate_sameVariableMultipleTimes() throws ApplicationException {
        // Arrange
        String template = "tmpl_{var1}_{var1}_tmpl";

        Map<String, String> parameters = Map.of(
                "var1", "value1"
        );

        // Act
        String res = fileService.getFileNameFromTemplate(template, parameters);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo("tmpl_value1_value1_tmpl");
    }

    @Test
    public void getFileNameFromTemplate_multipleVariablesOneTime() throws ApplicationException {
        // Arrange
        String template = "tmpl_{var1}_{var2}_tmpl";

        Map<String, String> parameters = Map.of(
                "var1", "value1",
                "var2", "value2"
        );

        // Act
        String res = fileService.getFileNameFromTemplate(template, parameters);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo("tmpl_value1_value2_tmpl");
    }

    @Test
    public void getFileNameFromTemplate_multipleVariablesMultipleTime() throws ApplicationException {
        // Arrange
        String template = "tmpl_{var1}_{var2}_{var2}_{var1}_tmpl";

        Map<String, String> parameters = Map.of(
                "var1", "value1",
                "var2", "value2"
        );

        // Act
        String res = fileService.getFileNameFromTemplate(template, parameters);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo("tmpl_value1_value2_value2_value1_tmpl");
    }

    @Test
    public void getFileNameFromTemplate_unknownVariable() throws ApplicationException {
        // Arrange
        String template = "tmpl_{var3}_tmpl";

        Map<String, String> parameters = Map.of(
                "var1", "value1",
                "var2", "value2"
        );

        // Act
        String res = fileService.getFileNameFromTemplate(template, parameters);

        // Assert
        assertThat(res).isNotNull();
        assertThat(res).isEqualTo("tmpl_{var3}_tmpl");
    }

    @Test
    public void getFileNameFromTemplate_invalidTemplate() {
        // Arrange

        Map<String, String> parameters = Map.of(
                "var1", "value1",
                "var2", "value2"
        );

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> fileService.getFileNameFromTemplate(null, parameters));

        // Assert
        assertThat(exception).isNotNull();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.FILE_LOADING_INVALID_TEMPLATE));
    }

    @Test
    public void getFileNameFromTemplate_invalidParameters() {
        // Arrange
        String template = "tmpl";

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> fileService.getFileNameFromTemplate(template, null));

        // Assert
        assertThat(exception).isNotNull();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.FILE_LOADING_INVALID_PARAMETERS));
    }

    @Test
    void getFilePath_nominal() {
        // Arrange
        String filename = "test";

        EndPoint endPoint = new EndPoint();
        endPoint.setFolderName("testFolder");

        // Act
        Path res = fileService.getFilePath(endPoint, filename);

        // Assert
        assertThat(res.toFile().getAbsolutePath()).isEqualTo(tmpDataDir + "\\" + endPoint.getFolderName() + "\\" + filename);
    }

    @Test
    void getFileContentToString_nominal() throws IOException, ApplicationException {
        // Arrange
        // File creation
        String filename = "test";
        Path file = Files.createFile(new File(tmpDataDir, filename).toPath());
        Files.write(file, "contenuTest".getBytes(StandardCharsets.UTF_8));

        // Act
        String contenu = fileService.getFileContentToString(file);

        // Assert
        assertThat(contenu).isEqualTo("contenuTest");
    }

    @Test
    void getFileContentToString_readError() throws IOException{
        // Arrange
        // File creation
        String filename = "test";
        Path file = Files.createFile(new File(tmpDataDir, filename).toPath());
        Files.write(file, "contenuTest".getBytes(StandardCharsets.UTF_8));

        // Mock
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(any(MessageEnum.class), anyString());

        try(MockedStatic<Files> files = Mockito.mockStatic(Files.class)){
            files.when(() -> Files.readString(any(Path.class)))
                    .thenThrow(new IOException());

            // Act
            ApplicationException applicationException = assertThrows(ApplicationException.class, () -> fileService.getFileContentToString(file));

            // Assert
            assertThat(applicationException).isNotNull();
        }
    }
}
