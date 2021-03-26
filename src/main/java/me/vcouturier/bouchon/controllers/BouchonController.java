package me.vcouturier.bouchon.controllers;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.exceptions.enums.ErrorsEnum;
import me.vcouturier.bouchon.exceptions.factory.ApplicationExceptionFactory;
import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.properties.BouchonProperties;
import me.vcouturier.bouchon.services.endpoint.EndPointService;
import me.vcouturier.bouchon.services.endpoint.impl.EndPointServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BouchonController {

    @Autowired
    private ApplicationExceptionFactory applicationExceptionFactory;

    @Autowired
    private EndPointService endPointService;

    @RequestMapping(value = "/{endPoint}", method = RequestMethod.GET)
    public String endPoint(@PathVariable String endPoint) throws ApplicationException {
        return endPointService.getEndPointCalled(endPoint)
                .map(ep -> ep.getName())
                .orElseThrow(() -> applicationExceptionFactory.createApplicationException(ErrorsEnum.INVALID_ENDPOINT));
    }
}
