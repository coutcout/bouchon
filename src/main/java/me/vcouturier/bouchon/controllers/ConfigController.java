package me.vcouturier.bouchon.controllers;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.services.ConfigService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path = "/config")
public class ConfigController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ConfigService configService;

    @RequestMapping(path="/endpoint", method = RequestMethod.POST)
    public String importEndpoint(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "name", required = false, defaultValue = "default") String destName
            ) throws ApplicationException {
        for(MultipartFile file : files){
            configService.uploadFile(file, destName);
        }

        return "ok";
    }
}
