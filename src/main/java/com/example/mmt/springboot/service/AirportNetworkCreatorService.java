package com.example.mmt.springboot.service;

import com.example.mmt.springboot.dao.filereader.FlightsFileReaderDao;
import com.example.mmt.springboot.domain.transport.Flight;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

//this service will create the network of Airports and populate AirportNetwork object/s
@Component
@Getter
public class AirportNetworkCreatorService {

    private final Set<String> airportSet;

    public static Map<String, Map<String, List<Flight>>> adjListOfAirports;

    public static Map<String, List<Flight>> flightMap;

    public AirportNetworkCreatorService() {
        this.airportSet = new HashSet<>();
        this.flightMap = new HashMap<>();
    }

    public void triggerAirportNetworkCreation(){
        FlightsFileReaderDao flightsFileReader = new FlightsFileReaderDao();
        List<Flight> flightList = flightsFileReader.processInputFile("src/main/resources/static/ivtest-sched.csv");

        for(Flight flight: flightList){
            airportSet.add(flight.getSource());
        }
        adjListOfAirports();
        for(Flight flight: flightList){
            addFlight(flight);
        }
    }

    private void adjListOfAirports()
    {
        adjListOfAirports = new HashMap<>();
        for (String airport : airportSet) {
            adjListOfAirports.put(airport, new HashMap<>());
        }
    }

    public void addFlight(Flight flight)
    {
        /*
         * check if adjListOfAirports contains the source
         * false
         *   create the value map
         *   create the list
         * true
         *   get the value map
         *   check if map contains the destination
         *   false
         *       create new list
         *
         * get the list and add the flight entry
         * */
        Map<String, List<Flight>> destinationMap;
        List<Flight> flightList;

        /*
         * this if is redundant as we are initialising destination map in adjListOfAirports() function
         * only else part will execute
         * keeping this if from the code completion perspective and
         * future extension : where flights can be added in the system on the fly and not just at the time of initialisation
         * todo : reverse if else to avoid evaluating if everytime
         * */
        if(!adjListOfAirports.containsKey(flight.getSource())){
            destinationMap = new HashMap<>();
            flightList = new ArrayList<>();
            destinationMap.put(flight.getDestination(), flightList);
        }
        else{
            destinationMap = adjListOfAirports.get(flight.getSource());
            if(!destinationMap.containsKey(flight.getDestination())){
                flightList = new ArrayList<>();
                destinationMap.put(flight.getDestination(), flightList);
            }
            else{
                flightList = destinationMap.get(flight.getDestination());
            }
        }
        flightList.add(flight);
        String key = flight.getFlightNo()+"_"+ flight.getSource()+"_"+flight.getDestination();
        List<Flight> fl;
        if(!flightMap.containsKey(key)){
            fl = new ArrayList<>();
        }else{
            fl = flightMap.get(key);
        }
        fl.add(flight);
        flightMap.put(key,fl);
    }

}
