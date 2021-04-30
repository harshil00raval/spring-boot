package com.example.mmt.springboot.utility.flightsutils;

import com.example.mmt.springboot.domain.AirportFlightNetwork;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    //ASSUMPTION : Same flight no between same source and destination will take same amount of time
    // 5319,IXR,BOM,855,1135
    // 5319,IXR,BOM,930,1210
    // todo : check if this assumption is wrong and fix the code
    public static Comparator<String> directFlightsComparator = new Comparator<String>() {

        @Override
        public int compare(String flightNo1, String flightNo2) {

            Integer fl1Time = TimeUtils.timeInMinutes(
                    AirportFlightNetwork.flightMap.get(flightNo1).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flightNo1).get(0).getEndTime());
            Integer fl2Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flightNo2).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flightNo2).get(0).getEndTime());

            return (fl1Time - fl2Time);
        }
    };

    public static Comparator<String> inDirectFlightsComparator = new Comparator<String>() {

        @Override
        public int compare(String flightNo1, String flightNo2) {

            List<String> flight1List = FlightsUtils.stringSplit(flightNo1);
            List<String> flight2List = FlightsUtils.stringSplit(flightNo2);

            int fl1Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(0)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(0)).get(0).getEndTime()) ;
            for(int i = 1 ; i < flight1List.size() ; ++i){
                fl1Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight1List.get(i-1)).get(0).getEndTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getStartTime());
            }
            int fl2Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight2List.get(0)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight2List.get(0)).get(0).getEndTime()) ;
            for(int i = 1 ; i < flight2List.size()  ; ++i){
                fl2Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight2List.get(i)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight2List.get(i)).get(0).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight2List.get(i-1)).get(0).getEndTime(), AirportFlightNetwork.flightMap.get(flight2List.get(i)).get(0).getStartTime());
            }
            return (fl1Time - fl2Time);
        }
    };

    public static void print(List<String> indirectFlights){

        for(String str : indirectFlights){
            List<String> flight1List = FlightsUtils.stringSplit(str);
            int fl1Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(0)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(0)).get(0).getEndTime()) ;
            for(int i = 1 ; i < flight1List.size() ; ++i){
                fl1Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight1List.get(i-1)).get(0).getEndTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).get(0).getStartTime());
            }
            System.out.println(flight1List +" time : "+ fl1Time);
        }

    }

}
