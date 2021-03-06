package com.example.mmt.springboot.utility.flightsutils;

import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.service.AirportNetworkCreatorService;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;

import java.util.*;

public class FlightsUtils {

    public static List<String> stringSplit(String flightPath){

        String flightsData[] = flightPath.split("_");
        List<String> flightKey = new ArrayList<>();
        for(int i=0; i< flightsData.length-2 ; ){
            String key = flightsData[i]+"_"+flightsData[i+1]+"_"+flightsData[i+2];
            flightKey.add(key);
            i = i+3;
        }
        return flightKey;
    }

    public static Comparator<Flight> directFlightsComparator = new Comparator<Flight>() {

        @Override
        public int compare(Flight flightNo1, Flight flightNo2) {

            Integer fl1Time = TimeUtils.timeInMinutes(flightNo1.getStartTime(), flightNo1.getEndTime());
            Integer fl2Time = TimeUtils.timeInMinutes(flightNo2.getStartTime(), flightNo2.getEndTime());

            return (fl1Time - fl2Time);
        }
    };
    public static Comparator<List<Flight>> inDirectFlightsComparator = new Comparator<List<Flight>>() {

        @Override
        public int compare(List<Flight> flightNo1, List<Flight> flightNo2) {

            int fl1Time = getTimeForFlights(flightNo1);
            int fl2Time = getTimeForFlights(flightNo2);
            return (fl1Time - fl2Time);
        }
    };

    public static Integer getTimeForFlights(List<Flight> flights){
        int flTime = TimeUtils.timeInMinutes(flights.get(0).getStartTime(), flights.get(0).getEndTime()) ;
        for(int i = 1 ; i < flights.size() ; ++i){
            flTime += TimeUtils.timeInMinutes(flights.get(i).getStartTime(), flights.get(i).getEndTime())
                    + TimeUtils.timeDifference(flights.get(i-1).getEndTime(), flights.get(i).getStartTime());
        }
        return flTime;
    }

    public static String getAirportString(List<Flight> flights){

        List<String> airports = new ArrayList<>();
        for(Flight flight : flights){
            airports.add(flight.getSource());
        }
        airports.add(flights.get(flights.size()-1).getDestination());
        return String.join("_",airports);

    }

    public static void print(List<String> indirectFlights){

        for(String str : indirectFlights){
            List<String> flight1List = FlightsUtils.stringSplit(str);
            int fl1Time = TimeUtils.timeInMinutes(AirportNetworkCreatorService.flightMap.get(flight1List.get(0)).get(0).getStartTime(), AirportNetworkCreatorService.flightMap.get(flight1List.get(0)).get(0).getEndTime()) ;
            for(int i = 1 ; i < flight1List.size() ; ++i){
                fl1Time += TimeUtils.timeInMinutes(AirportNetworkCreatorService.flightMap.get(flight1List.get(i)).get(0).getStartTime(), AirportNetworkCreatorService.flightMap.get(flight1List.get(i)).get(0).getEndTime())
                        + TimeUtils.timeDifference(AirportNetworkCreatorService.flightMap.get(flight1List.get(i-1)).get(0).getEndTime(), AirportNetworkCreatorService.flightMap.get(flight1List.get(i)).get(0).getStartTime());
            }
            System.out.println(flight1List +" time : "+ fl1Time);
        }

    }

}
