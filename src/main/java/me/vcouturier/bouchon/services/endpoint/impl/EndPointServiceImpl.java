package me.vcouturier.bouchon.services.endpoint.impl;

import me.vcouturier.bouchon.model.EndPoint;
import me.vcouturier.bouchon.properties.BouchonProperties;
import me.vcouturier.bouchon.services.endpoint.EndPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class EndPointServiceImpl implements EndPointService {

    @Autowired
    private BouchonProperties properties;


    @Override
    public Optional<EndPoint> getEndPointCalled(String endpoint) {
        return Optional.ofNullable(properties.getMapEndpoint().get(endpoint));
    }
}
