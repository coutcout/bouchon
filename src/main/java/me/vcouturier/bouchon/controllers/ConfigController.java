package me.vcouturier.bouchon.controllers;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.services.EndPointService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(path = "/config")
public class ConfigController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private EndPointService endPointService;

    @RequestMapping(path="/endpoint", method = RequestMethod.POST)
    public String importEndpoint(
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {
        for(MultipartFile file : files){
            log.info(messageService.formatMessage(MessageEnum.CONFIG_ENDPOINT_UPLOAD, file.getName()));
            endPointService.loadEndpointsFromFile(file);
        }

        return "ok";
    }
}
