package me.vcouturier.bouchon.controllers;

import lombok.extern.slf4j.Slf4j;
import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.services.EndPointService;
import me.vcouturier.bouchon.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
public class BouchonController {

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EndPointService endPointService;

    @RequestMapping(value = "/bouchon/{endpoint}/**", method = RequestMethod.GET)
    public String endPoint(@PathVariable("endpoint") String endpoint, HttpServletRequest request) throws ApplicationException {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);

        String finalEndpoint;
        if (!arguments.isEmpty()) {
            finalEndpoint = endpoint + '/' + arguments;
        } else {
            finalEndpoint = endpoint;
        }

        EndPoint e = endPointService.getEndPointCalled(finalEndpoint)
                                        .orElseThrow(() -> applicationExceptionFactory.createEndPointNotFoundException(MessageEnum.ERR_INVALID_ENDPOINT));

        return endPointService.runEndpoint(e, finalEndpoint);
    }

}
