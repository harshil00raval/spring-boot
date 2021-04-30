package com.example.mmt.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Route {
    @JsonProperty("route")
    private final String  route;

    @JsonProperty("flightInfo")
    private final FlightInfo flightInfo;

    public String toString(){
        return "\'"+route+"\'"+" : "+flightInfo.toString();
    }
}
