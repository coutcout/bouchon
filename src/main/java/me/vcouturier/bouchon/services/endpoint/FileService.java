package me.vcouturier.bouchon.services.endpoint;

import me.vcouturier.bouchon.exceptions.ApplicationException;

public interface FileService {

    boolean createFolder(String folderPath) throws ApplicationException;

}
