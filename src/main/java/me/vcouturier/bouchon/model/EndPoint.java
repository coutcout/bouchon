package me.vcouturier.bouchon.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndPoint {
    private String name;
    private String folder;
    private String urlTemplate;
    private String fileTemplate;
}
