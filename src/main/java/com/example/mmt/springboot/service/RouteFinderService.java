package com.example.mmt.springboot.service;

import com.example.mmt.springboot.dao.filereader.AirportCountryFileReaderDao;
import com.example.mmt.springboot.domain.AirportFlightNetwork;
import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.utility.stringutils.StringUtils;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;

import java.util.*;
import java.util.stream.Collectors;

// this will take 2 airports and find all routes between them
// we are using modified adjacency List to represent our graph
public class RouteFinderService {

//    List<List<String>> routeList = new ArrayList<>();
//    List<String> route;

    public Map<String, List<List<String>>> getAllRoutesFromSourceToDestination(Set<String> airportSet)
    {
        Set<String> visitedSet = new HashSet<>();
        Map<String, List<List<String>>> sourceToDestinationRoutes= new HashMap<>();

        List<String[]> airportCombinations = StringUtils.combination(airportSet);
        for(String[] airportCombination : airportCombinations){
            String source = airportCombination[0];
            String destination = airportCombination[1];

            List<String> route = new ArrayList<>();
            route.add(source);

            List<List<String>> routeList = new ArrayList<>();
            getAllRoutesUtil(source, destination, visitedSet, 0, route, routeList);
            routeList.sort(Comparator.comparingInt(List::size));
            List<List<String>> feasiblePaths = createListOfFeasibleFlightPaths(routeList);
            sourceToDestinationRoutes.put(source+"_"+destination, feasiblePaths);

        }

        return sourceToDestinationRoutes;
    }

    private void getAllRoutesUtil(String source, String destination, Set<String> visitedSet, Integer hops, List<String> route, List<List<String>> routeList)
    {
        //unable to find a use case for more than 9 hops
        //takes too much time when no 6 or more
        if(hops>3)
            return;
        if (source.equals(destination)) {
//            for(String s: route)
//                System.out.print(s+" -> ");
//            System.out.println();
            if(filterUnfeasibleRoute(route)){
                routeList.add(new ArrayList<>(route));
            }
            return;
        }
        visitedSet.add(source);

        // for all the airports adjacent to current airport
        for(String intermediateAirport : AirportFlightNetwork.adjListOfAirports.get(source).keySet()){
            if(!visitedSet.contains(intermediateAirport)){
                route.add(intermediateAirport);
                getAllRoutesUtil(intermediateAirport, destination, visitedSet, ++hops, route, routeList);
                route.remove(intermediateAirport);
                --hops;
            }
        }
        visitedSet.remove(source);
    }

    private static Boolean filterUnfeasibleRoute(List<String> routePossibility){
        return sameCountryFilter(routePossibility);
    }

    /*
     * filter out all the flights where
     * source and dest are in same country
     * but intermediate airports are in different country
     *
     * */

    private static Boolean sameCountryFilter(List<String> routePossibility) {

        String source = routePossibility.get(0);
        String destination = routePossibility.get(routePossibility.size()-1);

        String sourceCountry = AirportCountryFileReaderDao.airportCountryMap.get(source);
        String destinationCountry = AirportCountryFileReaderDao.airportCountryMap.get(destination);

        if(sourceCountry.equalsIgnoreCase(destinationCountry)){
            for(String airport : routePossibility){
                if(!sourceCountry.equalsIgnoreCase(AirportCountryFileReaderDao.airportCountryMap.get(airport))){
//                    System.out.println("filter : "+ routePossibility);
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    private static List<List<String>> createListOfFeasibleFlightPaths(List<List<String>> sourceToDestinationRoutes){
        List<List<List<String>>> allFlightsForGivenSourceAndDestination = getFlightDetailsV1(sourceToDestinationRoutes);
        List<List<String>> listOfDirectFlights = new ArrayList<>();
        List<List<String>> listOfIndirectFlights = new ArrayList<>();
        List<List<String>> listOfFeasibleFlights = new ArrayList<>();

        for(List<List<String>> flightCombinations : allFlightsForGivenSourceAndDestination){
            //direct flight
            if(flightCombinations.size() == 1) {
                listOfDirectFlights.add(new ArrayList<>(flightCombinations.get(0)));
                Collections.sort(listOfDirectFlights.get(0), directFlightsComparator);
            }
            else{
                List<String> flightPaths = new ArrayList<>();
                generateFlight(flightCombinations, flightPaths, 0, "");
                listOfIndirectFlights.add(new ArrayList<>(flightPaths));
            }
        }
        List<String> indirectFlights = listOfIndirectFlights.stream().flatMap(List::stream).collect(
                Collectors.toList());
        Collections.sort(indirectFlights, inDirectFlightsComparator);

        if (listOfFeasibleFlights.size() >= 1) {
            listOfFeasibleFlights
                    .add(new ArrayList<>(listOfDirectFlights.get(0)));
        } else {
            listOfFeasibleFlights.add(new ArrayList<>());
        }
        listOfFeasibleFlights.add(new ArrayList<>(indirectFlights));
        return listOfFeasibleFlights;
    }

    private static void print(List<String> indirectFlights){

        for(String str : indirectFlights){
            List<String> flight1List = stringSplit(str);
            int fl1Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(0)).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(0)).getEndTime()) ;
            for(int i = 1 ; i < flight1List.size() ; ++i){
                fl1Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(i)).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight1List.get(i-1)).getEndTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).getStartTime());
            }
            System.out.println(flight1List +" time : "+ fl1Time);
        }

    }

    private static Comparator<String> directFlightsComparator = new Comparator<String>() {

        @Override
        public int compare(String flightNo1, String flightNo2) {

            Integer fl1Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flightNo1).getStartTime(), AirportFlightNetwork.flightMap.get(flightNo1).getEndTime());
            Integer fl2Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flightNo2).getStartTime(), AirportFlightNetwork.flightMap.get(flightNo2).getEndTime());

            return (int) (fl1Time - fl2Time);
        }
    };



    private static Comparator<String> inDirectFlightsComparator = new Comparator<String>() {

        @Override
        public int compare(String flightNo1, String flightNo2) {

            List<String> flight1List = stringSplit(flightNo1);
            List<String> flight2List = stringSplit(flightNo2);

            int fl1Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(0)).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(0)).getEndTime()) ;
            for(int i = 1 ; i < flight1List.size() ; ++i){
                fl1Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight1List.get(i)).getStartTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight1List.get(i-1)).getEndTime(), AirportFlightNetwork.flightMap.get(flight1List.get(i)).getStartTime());
            }
            int fl2Time = TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight2List.get(0)).getStartTime(), AirportFlightNetwork.flightMap.get(flight2List.get(0)).getEndTime()) ;
            for(int i = 1 ; i < flight2List.size()  ; ++i){
                fl2Time += TimeUtils.timeInMinutes(AirportFlightNetwork.flightMap.get(flight2List.get(i)).getStartTime(), AirportFlightNetwork.flightMap.get(flight2List.get(i)).getEndTime())
                        + TimeUtils.timeDifference(AirportFlightNetwork.flightMap.get(flight2List.get(i-1)).getEndTime(), AirportFlightNetwork.flightMap.get(flight2List.get(i)).getStartTime());
            }

//            System.out.println(fl1Time +" - "+ fl2Time+ "="+ (fl1Time - fl2Time));
            return (int) (fl1Time - fl2Time);
        }
    };

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

    private static  List<List<List<Flight>>> getFlightDetails( List<List<String>> sourceToDestinationRoutes){

        List<List<List<Flight>>> listOfListOfListOfFlights = new ArrayList<>();
        for(List<String> sourceToDestinationRoute : sourceToDestinationRoutes){
            List<List<Flight>> listOfListOfFlights = new ArrayList<>();
            List<Flight> listOfFlights = new ArrayList<>();
            for(int i=0; i< sourceToDestinationRoute.size()-1;++i){
                listOfFlights = AirportFlightNetwork.adjListOfAirports
                                    .get(sourceToDestinationRoute.get(i))
                                    .get(sourceToDestinationRoute.get(i+1));
                listOfListOfFlights.add(new ArrayList<>(listOfFlights));
            }

            listOfListOfListOfFlights.add(listOfListOfFlights);
        }
        return listOfListOfListOfFlights;
    }

    private static  List<List<List<String>>> getFlightDetailsV1( List<List<String>> sourceToDestinationRoutes){

        List<List<List<String>>> listOfListOfListOfFlights = new ArrayList<>();
        for(List<String> sourceToDestinationRoute : sourceToDestinationRoutes){
            List<List<String>> listOfListOfFlights = new ArrayList<>();
            List<String> listOfFlights = new ArrayList<>();
            for(int i=0; i< sourceToDestinationRoute.size()-1;++i){
                String src = sourceToDestinationRoute.get(i);
                String dest = sourceToDestinationRoute.get(i+1);
                String keyAppander = "_"+src+"_"+dest;
                listOfFlights = AirportFlightNetwork.adjListOfAirports
                        .get(src)
                        .get(dest).stream().map(s ->s.getFlightNo()).collect(
                                Collectors.toList());

                listOfListOfFlights.add(new ArrayList<>(listOfFlights).stream().map(flightkey -> flightkey+keyAppander).collect(
                        Collectors.toList()));
            }

            listOfListOfListOfFlights.add(listOfListOfFlights);
        }
        return listOfListOfListOfFlights;
    }

    private static void generateFlight(List<List<String>> flightPathsList, List<String> flightPaths, int depth, String current) {
        if (depth == flightPathsList.size()) {
            flightPaths.add(current);
            return;
        }
        String separator = (current.length()>0) ? "_":"";
        for (int i = 0; i < flightPathsList.get(depth).size(); i++) {
            generateFlight(flightPathsList, flightPaths, depth + 1, current + separator+ flightPathsList.get(depth).get(i));
        }
    }
}
