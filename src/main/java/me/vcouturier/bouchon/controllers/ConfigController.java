package me.vcouturier.bouchon.controllers;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.services.ConfigService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/config")
public class ConfigController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConfigService configService;

    @RequestMapping(path="/endpoint", method = RequestMethod.POST)
    public void importEndpointConfiguration(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "name", required = false, defaultValue = "default") String destName
            ) throws ApplicationException {
        for(MultipartFile file : files){
            configService.uploadEndpointConfigurationFile(file, destName);
        }
    }

    @RequestMapping(path="/endpoint/{configFileName}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteEndpointConfiguration(
            @PathVariable("configFileName") String configFileName
    ) throws ApplicationException {
        if(configService.deleteEndpointConfigurationFile(configFileName)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path="/endpoint", method = RequestMethod.GET)
    public List<String> getListConfigurationFile(){
        return configService.getAllConfigurationFiles();
    }

}
