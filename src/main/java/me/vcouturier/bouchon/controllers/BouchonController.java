package me.vcouturier.bouchon.controllers;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.logs.enums.MessageEnum;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.services.EndPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BouchonController {

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private EndPointService endPointService;

    @RequestMapping(value = "/{endPoint}", method = RequestMethod.GET)
    public String endPoint(@PathVariable String endPoint) throws ApplicationException {
        return endPointService.getEndPointCalled(endPoint)
                .map(EndPoint::getName)
                .orElseThrow(() -> applicationExceptionFactory.createApplicationException(MessageEnum.ERR_INVALID_ENDPOINT));
    }
}
