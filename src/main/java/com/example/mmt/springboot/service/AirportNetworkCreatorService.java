package com.example.mmt.springboot.service;

import com.example.mmt.springboot.dao.BaseDao;
import com.example.mmt.springboot.dao.filereader.FlightsFileReaderDao;
import com.example.mmt.springboot.domain.AirportFlightNetwork;
import com.example.mmt.springboot.domain.transport.Flight;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//this service will create the network of Airports and populate AirportNetwork object/s
@Component
public class AirportNetworkCreatorService {

    Set<String> airportSet;

    public void triggerAirportNetworkCreation(){
        BaseDao flightsFileReader = new FlightsFileReaderDao();
        List<Flight> flightList = ((FlightsFileReaderDao) flightsFileReader).processInputFile("src/main/resources/static/ivtest-sched.csv");

        airportSet = new HashSet<>();

        for(Flight flight: flightList){
            airportSet.add(flight.getSource());
        }

        AirportFlightNetwork airportFlightNetwork = new AirportFlightNetwork(airportSet);

        for(Flight flight: flightList){
            airportFlightNetwork.addFlight(flight);
        }
    }

    public void routes(){
        RouteFinderService routeFinderService = new RouteFinderService();
        routeFinderService.getAllRoutesFromSourceToDestination(airportSet);
    }
}
