package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.enums.RequestParameterPlace;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.regex.enums.TypeRegex;
import me.vcouturier.bouchon.regex.model.ITypeRegex;
import me.vcouturier.bouchon.regex.service.RegexService;
import me.vcouturier.bouchon.regex.service.TypeRegexService;
import me.vcouturier.bouchon.regex.service.impl.RegexServiceImpl;
import me.vcouturier.bouchon.regex.service.impl.TypeRegexServiceImpl;
import me.vcouturier.bouchon.services.impl.EndPointServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("ThrowableNotThrown")
@ExtendWith(MockitoExtension.class)
public class EndPointServiceTest {

    @Mock
    private FileService fileService;

    @Mock
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Mock
    private MessageService messageService;

    private EndPointServiceImpl endPointService;

    @BeforeEach
    public void init(){
        RegexService regexService = new RegexServiceImpl();
        ReflectionTestUtils.setField(regexService, "messageService", messageService);

        endPointService = new EndPointServiceImpl();
        ReflectionTestUtils.setField(endPointService, "fileService", fileService);
        ReflectionTestUtils.setField(endPointService, "regexService", regexService);
        ReflectionTestUtils.setField(endPointService, "applicationExceptionFactory", applicationExceptionFactory);
        ReflectionTestUtils.setField(endPointService, "messageService", messageService);

        initTypeRegex();

        Mockito.reset(applicationExceptionFactory, messageService, fileService);
    }

    private void initTypeRegex() {
        Map<String, ITypeRegex> map = Map.of(
                "string", TypeRegex.STRING,
                "number", TypeRegex.NUMBER,
                "boolean", TypeRegex.BOOLEAN
        );

        TypeRegexServiceImpl typeRegexService = new TypeRegexServiceImpl();
        ReflectionTestUtils.setField(typeRegexService, "mapTypeRegex", map);
        ReflectionTestUtils.setField(endPointService, "typeRegexService", typeRegexService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test01",
            "test\\d{2}"
    })
    public void getEndPointCalled_nominal(String regex) throws ApplicationException {
        // Arrange
        String requestedEndpoint = "test01";

        // Setting endpoints map
        Map<String, EndPoint> endPointMap = new HashMap<>();
        ReflectionTestUtils.setField(endPointService, "mapEndpoint", endPointMap);

        EndPoint endPoint1 = new EndPoint();
        endPoint1.setName("e1");
        endPoint1.setUrlRegex(regex);
        endPointMap.put(endPoint1.getName(), endPoint1);

        EndPoint endPoint2 = new EndPoint();
        endPoint2.setName("e2");
        endPoint2.setUrlRegex("toto");
        endPointMap.put(endPoint2.getName(), endPoint2);

        // Act
        Optional<EndPoint> endPoint = endPointService.getEndPointCalled(requestedEndpoint);

        // Assert
        assertThat(endPoint).isNotEmpty();
        assertThat(endPoint.get().getName()).isEqualTo("e1");
    }

    @Test
    public void getEndPointCalled_mapvide() throws ApplicationException {
        // Arrange
        String requestedEndpoint = "test01";

        // Setting endpoints map
        Map<String, EndPoint> endPointMap = new HashMap<>();
        ReflectionTestUtils.setField(endPointService, "mapEndpoint", endPointMap);

        // Act
        Optional<EndPoint> endPoint = endPointService.getEndPointCalled(requestedEndpoint);

        // Assert
        assertThat(endPoint).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void getEndPointCalled_invalidRequestedEndpoint(String requestedEndpoint){
        // Arrange

        // Setting endpoints map
        Map<String, EndPoint> endPointMap = new HashMap<>();
        ReflectionTestUtils.setField(endPointService, "mapEndpoint", endPointMap);

        EndPoint endPoint1 = new EndPoint();
        endPoint1.setName("e1");
        endPoint1.setUrlRegex("test\\d{2}");
        endPointMap.put(endPoint1.getName(), endPoint1);

        EndPoint endPoint2 = new EndPoint();
        endPoint2.setName("e2");
        endPoint2.setUrlRegex("toto");
        endPointMap.put(endPoint2.getName(), endPoint2);

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> endPointService.getEndPointCalled(requestedEndpoint));

        // Assert
        verify(applicationExceptionFactory).createApplicationException(MessageEnum.CONFIG_ENDPOINT_REQUESTED_URL_INVALID);
        assertThat(exception).isNotNull();
    }

    @Test
    public void getEndPointCalled_notUniqueEndpoint(){
        // Arrange
        String requestedEndpoint = "test01";

        // Setting endpoints map
        Map<String, EndPoint> endPointMap = new HashMap<>();
        ReflectionTestUtils.setField(endPointService, "mapEndpoint", endPointMap);

        EndPoint endPoint1 = new EndPoint();
        endPoint1.setName("e1");
        endPoint1.setUrlRegex("test01");
        endPointMap.put(endPoint1.getName(), endPoint1);

        EndPoint endPoint2 = new EndPoint();
        endPoint2.setName("e2");
        endPoint2.setUrlRegex("test\\d{2}");
        endPointMap.put(endPoint2.getName(), endPoint2);

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> endPointService.getEndPointCalled(requestedEndpoint));

        // Assert
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ENDPOINT_NOT_UNIQUE), Mockito.anyString());
        assertThat(exception).isNotNull();
    }

    @Test
    public void getEndPointCalled_endpointNotFound() throws ApplicationException {
        // Arrange
        String requestedEndpoint = "test01";

        // Setting endpoints map
        Map<String, EndPoint> endPointMap = new HashMap<>();
        ReflectionTestUtils.setField(endPointService, "mapEndpoint", endPointMap);

        EndPoint endPoint1 = new EndPoint();
        endPoint1.setName("e1");
        endPoint1.setUrlRegex("test02");
        endPointMap.put(endPoint1.getName(), endPoint1);

        EndPoint endPoint2 = new EndPoint();
        endPoint2.setName("e2");
        endPoint2.setUrlRegex("test\\d");
        endPointMap.put(endPoint2.getName(), endPoint2);

        // Act
        Optional<EndPoint> endPoint = endPointService.getEndPointCalled(requestedEndpoint);

        // Assert
        assertThat(endPoint).isEmpty();
    }

    @Test
    public void getRequestParametersGet_nominal() throws ApplicationException {
        // Arrange
        String request = "test_01_02";

        EndPoint endpoint = new EndPoint();
        endpoint.setUrlRegex("test_(?<a>\\d{2})_(?<b>\\d{2})");
        endpoint.setParamsAvailable(List.of("a", "b"));

        // Act
        Map<String, String> res = endPointService.getRequestParametersGet(request, endpoint);

        // Assert
        assertThat(res).isNotEmpty();
        assertThat(res.get("a")).isNotNull();
        assertThat(res.get("a")).isEqualTo("01");

        assertThat(res.get("b")).isNotNull();
        assertThat(res.get("b")).isEqualTo("02");
    }

    @Test
    public void getRequestParametersGet_invalidRegex() {
        // Arrange
        String request = "test_0a_02";

        EndPoint endpoint = new EndPoint();
        endpoint.setUrlRegex("test_(?<a>\\d{2})_(?<b>\\d{2})");
        endpoint.setParamsAvailable(List.of("a", "b"));

        // Mocking
        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString(), Mockito.anyString());

        // Act
        ApplicationException exception = assertThrows(ApplicationException.class, () -> endPointService.getRequestParametersGet(request, endpoint));

        // Assert
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.REQUEST_MISFORMATED), Mockito.anyString(), Mockito.anyString());
        assertThat(exception).isNotNull();

    }


    @Test
    public void runEndpointGet_nominal() throws ApplicationException {
        // No functional test is necessary here because everything is in already tested sub-methods
        // Arrange
        this.endPointService = Mockito.spy(this.endPointService);

        EndPoint endpoint = new EndPoint();
        String fileTemplate = "fileTemplate";
        String folderName = "folderName";
        endpoint.setFileTemplate(fileTemplate);
        endpoint.setFolderName(folderName);
        String request = "a";
        String filename = "filename";
        Path file = Path.of("/a/b");
        String fileContent = "fileContent";

        Map<String, String> mapRequestParameter = new HashMap<>();
        doReturn(mapRequestParameter).when(endPointService).getRequestParametersGet(Mockito.anyString(), Mockito.any(EndPoint.class));
        doReturn(filename).when(fileService).getFileNameFromTemplate(Mockito.anyString(), Mockito.anyMap());
        doReturn(file).when(fileService).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(fileContent).when(fileService).getFileContentToString(Mockito.any(Path.class));

        // Assert
        String res = endPointService.runEndpointGet(endpoint, request);

        // Act
        verify(endPointService).getRequestParametersGet(request, endpoint);
        verify(fileService).getFileNameFromTemplate(fileTemplate, mapRequestParameter);
        verify(fileService).getFilePath(folderName, filename);
        verify(fileService).getFileContentToString(file);
        verifyNoMoreInteractions(fileService);

        assertThat(res).isEqualTo(fileContent);
    }

    @Test
    public void runEndpointGet_request_misformated() throws ApplicationException {
        // Arrange
        this.endPointService = Mockito.spy(this.endPointService);

        EndPoint endpoint = new EndPoint();
        String fileTemplate = "fileTemplate";
        String folderName = "folderName";
        endpoint.setFileTemplate(fileTemplate);
        endpoint.setFolderName(folderName);
        String request = "a";

        ApplicationException applicationException = new ApplicationException("code", "message");
        doThrow(applicationException).when(endPointService).getRequestParametersGet(Mockito.anyString(), Mockito.any(EndPoint.class));
        // Assert
        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> endPointService.runEndpointGet(endpoint, request));

        // Act
        verify(endPointService).getRequestParametersGet(request, endpoint);
        verifyNoMoreInteractions(fileService);

        assertThat(exception).isNotNull();
        assertThat(exception).isEqualTo(applicationException);
    }

    @Test
    public void runEndpointGet_file_reading_error() throws ApplicationException {
        // Arrange
        this.endPointService = Mockito.spy(this.endPointService);

        EndPoint endpoint = new EndPoint();
        String fileTemplate = "fileTemplate";
        String folderName = "folderName";
        endpoint.setFileTemplate(fileTemplate);
        endpoint.setFolderName(folderName);
        String request = "a";
        String filename = "filename";
        Path file = Path.of("/a/b");

        Map<String, String> mapRequestParameter = new HashMap<>();
        doReturn(mapRequestParameter).when(endPointService).getRequestParametersGet(Mockito.anyString(), Mockito.any(EndPoint.class));
        doReturn(filename).when(fileService).getFileNameFromTemplate(Mockito.anyString(), Mockito.anyMap());
        doReturn(file).when(fileService).getFilePath(Mockito.anyString(), Mockito.anyString());

        ApplicationException applicationException = new ApplicationException("code", "message");
        doThrow(applicationException).when(fileService).getFileContentToString(Mockito.any(Path.class));
        // Assert
        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> endPointService.runEndpointGet(endpoint, request));

        // Act
        verify(endPointService).getRequestParametersGet(request, endpoint);
        verify(fileService).getFileNameFromTemplate(fileTemplate, mapRequestParameter);
        verify(fileService).getFilePath(folderName, filename);
        verify(fileService).getFileContentToString(file);
        verifyNoMoreInteractions(fileService);

        assertThat(exception).isNotNull();
        assertThat(exception).isEqualTo(applicationException);
    }

    @Test
    public void runEndpointPost_nominal() throws ApplicationException {
        // No functional test is necessary here because everything is in already tested sub-methods
        // Arrange
        this.endPointService = Mockito.spy(this.endPointService);

        EndPoint endpoint = new EndPoint();
        String fileTemplate = "fileTemplate";
        String folderName = "folderName";
        endpoint.setFileTemplate(fileTemplate);
        endpoint.setFolderName(folderName);
        String request = "a";
        String filename = "filename";
        Path file = Path.of("/a/b");
        String fileContent = "fileContent";
        Map<String, String> mapRequestParameter = new HashMap<>();

        doReturn(filename).when(fileService).getFileNameFromTemplate(Mockito.anyString(), Mockito.anyMap());
        doReturn(file).when(fileService).getFilePath(Mockito.anyString(), Mockito.anyString());
        doReturn(fileContent).when(fileService).getFileContentToString(Mockito.any(Path.class));

        // Assert
        String res = endPointService.runEndpointPost(endpoint, request, mapRequestParameter);

        // Act
        verify(fileService).getFileNameFromTemplate(fileTemplate, mapRequestParameter);
        verify(fileService).getFilePath(folderName, filename);
        verify(fileService).getFileContentToString(file);
        verifyNoMoreInteractions(fileService);

        assertThat(res).isEqualTo(fileContent);
    }

    @Test
    public void runEndpointPost_file_reading_error() throws ApplicationException {
        // Arrange
        this.endPointService = Mockito.spy(this.endPointService);

        EndPoint endpoint = new EndPoint();
        String fileTemplate = "fileTemplate";
        String folderName = "folderName";
        endpoint.setFileTemplate(fileTemplate);
        endpoint.setFolderName(folderName);
        String request = "a";
        String filename = "filename";
        Path file = Path.of("/a/b");
        Map<String, String> mapRequestParameter = new HashMap<>();

        doReturn(filename).when(fileService).getFileNameFromTemplate(Mockito.anyString(), Mockito.anyMap());
        doReturn(file).when(fileService).getFilePath(Mockito.anyString(), Mockito.anyString());

        ApplicationException applicationException = new ApplicationException("code", "message");
        doThrow(applicationException).when(fileService).getFileContentToString(Mockito.any(Path.class));
        // Assert
        ApplicationException exception = Assertions.assertThrows(ApplicationException.class, () -> endPointService.runEndpointPost(endpoint, request, mapRequestParameter));

        // Act
        verify(fileService).getFileNameFromTemplate(fileTemplate, mapRequestParameter);
        verify(fileService).getFilePath(folderName, filename);
        verify(fileService).getFileContentToString(file);
        verifyNoMoreInteractions(fileService);

        assertThat(exception).isNotNull();
        assertThat(exception).isEqualTo(applicationException);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void reinitializeEndpoints_nominal(){
        // Arrange
        Map<String, EndPoint> mapEndpoints = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        if (mapEndpoints != null) {
            mapEndpoints.put("a", new EndPoint());
        }

        assertThat(mapEndpoints).isNotEmpty();

        // Act
        endPointService.reinitializeEndpoints();

        // Assert
        assertThat(mapEndpoints).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_nominal() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_1.yaml");

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isNotEmpty();
        assertThat(mapEndpoint).containsKey(expected.getName());
        EndPoint endpointRes = mapEndpoint.get(expected.getName());
        assertThat(endpointRes).isEqualTo(expected);
        assertThat(endpointRes.getMapParameters()).isNotEmpty();
        assertThat(endpointRes.getMapParameters()).containsKeys("numberA", "title");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_duplicatedEndpoint() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_1.yaml");

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        EndPoint existing = new EndPoint();
        existing.setFolderName("folder1");
        existing.setName("test1");

        if (mapEndpoint != null) {
            mapEndpoint.put(existing.getName(), existing);
        }

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), Mockito.anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.INIT_ENDPOINT_ALREADY_EXISTS), Mockito.anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        assertThat(mapEndpoint).hasSize(1);
        assertThat(mapEndpoint).containsKey(existing.getName());
        if (mapEndpoint != null) {
            assertThat(mapEndpoint.get(existing.getFileTemplate())).isNull();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_fileRegexParametersNotEqualsURLRegexParameters() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_parameters_mismatching.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class));

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_REGEX_NOT_EQUALS));

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_urlTemplate_duplicatedParam() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_duplicated_regex_urlTemplate.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_URL_DUPLICATE_REGEX), anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_fileTemplate_duplicatedParam() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_duplicated_regex_fileTemplate.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_URL_DUPLICATE_REGEX), anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_urlTempalte_invalidRegexType() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_urlTemplate_invalidRegexType.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_URL_UNKOWN_REGEX), anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_fileTempalte_invalidRegexType() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_fileTemplate_invalidRegexType.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_URL_UNKOWN_REGEX), anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loadEndpointsFromFile_invalidRegexType() throws IOException {
        // Arrange
        File endpointFile = new File("src/test/resources/me/vcouturier/bouchon/services/loadEndpointTest_invalidRegexType.yaml");

        doReturn(new ApplicationException("code", "message")).when(applicationExceptionFactory).createApplicationException(Mockito.any(MessageEnum.class), anyString());

        // Act
        Map<EndPoint, Optional<String>> res = endPointService.loadEndpointsFromFile(endpointFile);

        // Assert
        assertThat(res).isNotEmpty();
        verify(applicationExceptionFactory).createApplicationException(eq(MessageEnum.ERR_UKNOWN_REGEX_TYPE), anyString());

        // Expected endpoint
        EndPoint expected = new EndPoint();
        expected.setFolderName("folder1");
        expected.setName("test1");

        assertThat(res).containsKey(expected);
        assertThat(res.get(expected)).isNotEmpty();

        Map<String, EndPoint> mapEndpoint = (Map<String, EndPoint>) ReflectionTestUtils.getField(endPointService, "mapEndpoint");
        assertThat(mapEndpoint).isEmpty();
    }
}
