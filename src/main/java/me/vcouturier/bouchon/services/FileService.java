package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;

public interface FileService {

    boolean createFolder(String folderPath) throws ApplicationException;

}
