package me.vcouturier.bouchon.services.endpoint;

import me.vcouturier.bouchon.model.EndPoint;

import java.util.Optional;

public interface EndPointService {

    Optional<EndPoint> getEndPointCalled(String endpoint);
}
