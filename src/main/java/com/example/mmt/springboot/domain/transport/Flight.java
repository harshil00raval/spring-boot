package com.example.mmt.springboot.domain.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Flight implements Transportation{
    private final String flightNo;
    private final String source;
    private final String destination;
    private final String startTime;
    private final String endTime;
}
