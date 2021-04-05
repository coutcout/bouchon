package me.vcouturier.bouchon.services;

import me.vcouturier.bouchon.exceptions.ApplicationException;
import me.vcouturier.bouchon.model.EndPoint;

import java.util.Optional;

public interface EndPointService {

    Optional<EndPoint> getEndPointCalled(String endpoint) throws ApplicationException;
}
