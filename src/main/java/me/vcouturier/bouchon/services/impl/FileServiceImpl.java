package me.vcouturier.bouchon.services.impl;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.services.FileService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${bouchon.folder.data}")
    private String dataFolderPath;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private MessageService messageService;

    @Override
    public boolean createFolder(String folderName) throws ApplicationException {
        if(Files.isDirectory(Path.of(dataFolderPath))){
            Path folderPath = Path.of(dataFolderPath, folderName);
            File file = folderPath.toFile();

            return file.mkdir();
        } else {
            throw applicationExceptionFactory.createApplicationException(MessageEnum.ERR_DATAFOLDER_MISSING);
        }

    }

    @Override
    public String getFileNameFromTemplate(String template, Map<String, String> params) {
        String res = template;
        for(Map.Entry<String, String> param : params.entrySet()){
            String toReplace = "\\{" + param.getKey() + "}";
            String replaceWith = param.getValue();
            log.debug(messageService.formatMessage(MessageEnum.DEBUG_REGEX_REPLACING_VALUE, toReplace, replaceWith, template));
            res = res.replaceAll(toReplace, replaceWith);
        }

        return res;
    }
}
