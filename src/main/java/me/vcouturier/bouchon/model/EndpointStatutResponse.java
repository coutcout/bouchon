package me.vcouturier.bouchon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.vcouturier.bouchon.enums.EndpointStatut;

@Getter
@AllArgsConstructor
public class EndpointStatutResponse {
    private String filename;
    private String endpointName;
    private EndpointStatut statut;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;
}
