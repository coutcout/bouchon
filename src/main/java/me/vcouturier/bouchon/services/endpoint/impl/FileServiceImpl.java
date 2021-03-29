package me.vcouturier.bouchon.services.endpoint.impl;

import lombok.extern.log4j.Log4j2;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.enums.ErrorsEnum;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.services.endpoint.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Service
public class FileServiceImpl implements FileService {
    @Value("${bouchon.folder.data}")
    private String dataFolderPath;

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Override
    public boolean createFolder(String folderName) throws ApplicationException {
        if(Files.isDirectory(Path.of(dataFolderPath))){
            Path folderPath = Path.of(dataFolderPath, folderName);
            File file = folderPath.toFile();

            return file.mkdir();
        } else {
            throw applicationExceptionFactory.createApplicationException(ErrorsEnum.DATAFOLDER_MISSING);
        }

    }
}
