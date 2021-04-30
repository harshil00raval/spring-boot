package com.example.mmt.springboot.service;

import com.example.mmt.springboot.dao.filereader.AirportCountryFileReaderDao;
import com.example.mmt.springboot.domain.AirportFlightNetwork;
import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.utility.flightsutils.FlightsUtils;
import com.example.mmt.springboot.utility.stringutils.StringUtils;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

// this will take 2 airports and find all routes between them
// we are using modified adjacency List to represent our graph
@Service
public class RouteFinderService {

    static Map<String, List<List<ImmutablePair<List<Flight>, Integer>>>> allRoutes ;

    public Map<String, List<List<ImmutablePair<List<Flight>, Integer>>>> getAllRoutesFromSourceToDestination(Set<String> airportSet)
    {
        Set<String> visitedSet = new HashSet<>();
        Map<String, List<List<ImmutablePair<List<Flight>, Integer>>>> sourceToDestinationRoutes= new HashMap<>();

        List<String[]> airportCombinations = StringUtils.combination(airportSet);
        for(String[] airportCombination : airportCombinations){
            String source = airportCombination[0];
            String destination = airportCombination[1];

            List<String> route = new ArrayList<>();
            route.add(source);

            List<List<String>> routeList = new ArrayList<>();
            getAllRoutesUtil(source, destination, visitedSet, 0, route,
                    routeList);
            routeList.sort(Comparator.comparingInt(List::size));
            List<List<ImmutablePair<List<Flight>, Integer>>> feasiblePaths = createListOfFeasibleFlightPathsV1(
                    routeList);
            sourceToDestinationRoutes
                    .put(source + "_" + destination, feasiblePaths);

        }

        allRoutes = sourceToDestinationRoutes;
        return sourceToDestinationRoutes;
    }

    private void getAllRoutesUtil(String source, String destination, Set<String> visitedSet, Integer hops, List<String> route, List<List<String>> routeList)
    {
        //unable to find a use case for more than 9 hops
        //takes too much time when no 6 or more
        if(hops>2)
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
        List<List<List<Flight>>> allFlightsForGivenSourceAndDestination1 = getFlightDetails(sourceToDestinationRoutes);
        List<List<String>> listOfDirectFlights = new ArrayList<>();
        List<List<String>> listOfIndirectFlights = new ArrayList<>();
        List<List<String>> listOfFeasibleFlights = new ArrayList<>();

        for(List<List<String>> flightCombinations : allFlightsForGivenSourceAndDestination){
            //direct flight
            if(flightCombinations.size() == 1) {
                listOfDirectFlights.add(new ArrayList<>(flightCombinations.get(0)));
                //create the
                Collections.sort(listOfDirectFlights.get(0), FlightsUtils.directFlightsComparator);
            }
            else{
                List<String> flightPaths = new ArrayList<>();
                generateFlight(flightCombinations, flightPaths, 0, "");
                listOfIndirectFlights.add(new ArrayList<>(flightPaths));
            }
        }
        List<String> indirectFlights = listOfIndirectFlights.stream().flatMap(List::stream).collect(
                Collectors.toList());
        Collections.sort(indirectFlights, FlightsUtils.inDirectFlightsComparator);

        if (listOfDirectFlights.size() >= 1) {
            listOfFeasibleFlights
                    .add(new ArrayList<>(listOfDirectFlights.get(0)));
        } else {
            listOfFeasibleFlights.add(new ArrayList<>());
        }
        listOfFeasibleFlights.add(new ArrayList<>(indirectFlights));
        return listOfFeasibleFlights;
    }

    private static List<List<ImmutablePair<List<Flight>, Integer>>> createListOfFeasibleFlightPathsV1(List<List<String>> sourceToDestinationRoutes){
        List<List<List<Flight>>> allFlightsForGivenSourceAndDestination = getFlightDetails(sourceToDestinationRoutes);
        List<List<Flight>> listOfDirectFlights = new ArrayList<>();
        List<List<Flight>> listOfIndirectFlights = new ArrayList<>();
        List<ImmutablePair<List<Flight>, Integer>> directFlightsPairs = new ArrayList<>();
        List<ImmutablePair<List<Flight>, Integer>> inDirectFlightsPairs = new ArrayList<>();
        List<List<ImmutablePair<List<Flight>, Integer>>> listOfFeasibleFlights = new ArrayList<>();

        for(List<List<Flight>> flightCombinations : allFlightsForGivenSourceAndDestination){
            //direct flight
            if(flightCombinations.size() == 1) {
                listOfDirectFlights.add(new ArrayList<>(flightCombinations.get(0)));
                listOfDirectFlights.get(0)
                        .sort(FlightsUtils.directFlightsComparatorV1);
                for(List<Flight> flights : listOfDirectFlights){
                    directFlightsPairs.add(new ImmutablePair<>(flights,0));
                }
            }
            //indirect flight
            else{
                List<List<Flight>> flightPaths = new ArrayList<>();
                generateFlightV1(flightCombinations, flightPaths, 0, null);
                List<Flight> fastestFlightRoute = removeDuplicatePaths(flightPaths);
                listOfIndirectFlights.add(new ArrayList<>(fastestFlightRoute));
            }
        }
        Collections.sort(listOfIndirectFlights, FlightsUtils.inDirectFlightsComparatorV1);
        for(List<Flight> flights : listOfIndirectFlights){
            Integer time = FlightsUtils.getTimeForFlights(flights);
            inDirectFlightsPairs.add(new ImmutablePair<>(flights,time));
        }

        if (listOfDirectFlights.size() >= 1) {
            listOfFeasibleFlights
                    .add(new ArrayList<>(directFlightsPairs));
        } else {
            listOfFeasibleFlights.add(new ArrayList<>());
        }
        listOfFeasibleFlights.add(inDirectFlightsPairs);
        return listOfFeasibleFlights;
    }

    private static List<Flight> removeDuplicatePaths(List<List<Flight>> flightsOnSameRoute){
        Integer travelTime = Integer.MAX_VALUE;
        List<Flight> fastestFlightsOnTheRoute = new ArrayList<>();
        for(List<Flight> flights : flightsOnSameRoute){
            Integer flTime = TimeUtils.timeInMinutes(flights.get(0).getStartTime(), flights.get(0).getEndTime()) ;
            for(int i = 1 ; i < flights.size() ; ++i){
                flTime += TimeUtils.timeInMinutes((flights.get(i)).getStartTime(), (flights.get(i)).getEndTime())
                        + TimeUtils.timeDifference((flights.get(i-1)).getEndTime(), (flights.get(i)).getStartTime());
            }
            if(flTime < travelTime){
                travelTime = flTime;
                fastestFlightsOnTheRoute.clear();
                fastestFlightsOnTheRoute.addAll(flights);
            }
        }
        return fastestFlightsOnTheRoute;
    }

    private static  List<List<List<Flight>>> getFlightDetails( List<List<String>> sourceToDestinationRoutes){

        List<List<List<Flight>>> listOfListOfListOfFlights = new ArrayList<>();
        for(List<String> sourceToDestinationRoute : sourceToDestinationRoutes){
            List<List<Flight>> listOfListOfFlights = new ArrayList<>();
            List<Flight> listOfFlights = new ArrayList<>();
            for(int i=0; i< sourceToDestinationRoute.size()-1;++i){
                String src = sourceToDestinationRoute.get(i);
                String dest = sourceToDestinationRoute.get(i+1);
                String keyAppender = "_"+src+"_"+dest;
                listOfFlights = new ArrayList<>(
                        AirportFlightNetwork.adjListOfAirports.get(src)
                                .get(dest));
                listOfListOfFlights.add(new ArrayList<>(listOfFlights).stream().collect(
                        Collectors.toList()));
            }
            listOfListOfListOfFlights.add(listOfListOfFlights);
        }
        return listOfListOfListOfFlights;
    }

    private static  List<List<List<String>>> getFlightDetailsV1( List<List<String>> sourceToDestinationRoutes){

        List<List<List<String>>> listOfListOfListOfFlights = new ArrayList<>();
        for(List<String> sourceToDestinationRoute : sourceToDestinationRoutes){
            List<List<String>> listOfListOfFlights = new ArrayList<>();
            List<String> listOfFlights;
            for(int i=0; i< sourceToDestinationRoute.size()-1;++i){
                String src = sourceToDestinationRoute.get(i);
                String dest = sourceToDestinationRoute.get(i+1);
                String keyAppender = "_"+src+"_"+dest;
                listOfFlights = AirportFlightNetwork.adjListOfAirports
                        .get(src)
                        .get(dest).stream().map(s ->s.getFlightNo()).collect(
                                Collectors.toList());

//                Flight flight = AirportFlightNetwork.flightMap.get(src);
//                int fl1Time = TimeUtils.timeInMinutes(flight.getStartTime(), flight.getEndTime());
                listOfListOfFlights.add(new ArrayList<>(listOfFlights).stream().map(flightkey -> flightkey+keyAppender).collect(
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

    private static void generateFlightV1(List<List<Flight>> flightPathsList, List<List<Flight>> flightPaths, int depth, List<Flight> current) {
        if (depth == flightPathsList.size()) {
            flightPaths.add(new ArrayList<>(current));
            return;
        }
        if(!Objects.nonNull(current)){
            current = new ArrayList<>();
        }
        for (int i = 0; i < flightPathsList.get(depth).size(); i++) {
            current.add(flightPathsList.get(depth).get(i));
            generateFlightV1(flightPathsList, flightPaths, depth + 1, current);
            current.remove(current.size()-1);
        }
    }

    @PostConstruct
    private void init(){
        AirportNetworkCreatorService airportNetworkCreatorService = new AirportNetworkCreatorService();
        airportNetworkCreatorService.triggerAirportNetworkCreation();
        airportNetworkCreatorService.routes();
    }

    public static List<List<ImmutablePair<List<Flight>, Integer>>> getListOfRoute(String source, String destination){
        return allRoutes.get(source+"_"+destination);
    }

    public List<List<ImmutablePair<List<Flight>, Integer>>> getRoutes(String source, String destination){
        return getListOfRoute(source,destination);
    }
}
