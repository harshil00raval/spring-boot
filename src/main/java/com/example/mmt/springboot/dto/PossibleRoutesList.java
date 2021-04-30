package com.example.mmt.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PossibleRoutesList {

    @JsonProperty("directRouteList")
    private final List<Route> directRouteList;

    @JsonProperty("inDirectRouteList")
    private final List<Route> inDirectRouteList;
}
