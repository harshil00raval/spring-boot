package com.example.mmt.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FlightInfo {
    @JsonProperty("flightNumbers")
    private String flightNumbers;

    @JsonProperty("totalTime")
    private Integer totalTIme;

    public String toString(){
        return " { "+"\'"+flightNumbers+"\'"+ " : " + totalTIme+" } ";
    }
}
