package com.example.mmt.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class PossibleRoutesList {

//    @JsonProperty("directRouteList")
    private final List<Route> directRouteList;

//    @JsonProperty("inDirectRouteList")
    private final List<Route> inDirectRouteList;

    public String toString(){
        return "[ { "
                +String.join(
                        ", ",
                        directRouteList.stream().map(s->s.toString()).collect(Collectors.toList()))
                +" } , { "
                +String.join(
                        ", ",
                        inDirectRouteList.stream().map(s->s.toString()).collect(Collectors.toList()))
                +" } ] ";
    }
}
