package com.example.mmt.springboot.mapper;

import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.dto.FlightInfo;
import com.example.mmt.springboot.dto.PossibleRoutesList;
import com.example.mmt.springboot.dto.Route;
import com.example.mmt.springboot.utility.flightsutils.FlightsUtils;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PossibleRouteListResponseMapper {

    public static PossibleRoutesList mapToPossibleRouteList(List<List<ImmutablePair<List<Flight>, Integer>>> routes, Integer kNoOfRoutes){
        List<ImmutablePair<List<Flight>, Integer>> directlist=null;
        List<ImmutablePair<List<Flight>, Integer>> inDirectlist=null;

        List<Route> directRoutes = new ArrayList<>();
        List<Route> inDirectRoutes = new ArrayList<>();
        PossibleRoutesList possibleRoutesList = new PossibleRoutesList(directRoutes, inDirectRoutes);
        Integer counter = 0;
        if(Objects.nonNull(routes) && Objects.nonNull(routes.get(0))) {
            directlist = routes.get(0);

                for (ImmutablePair<List<Flight>, Integer> flightPair : directlist) {
                    List<Flight> flights = flightPair.getLeft();
                    for(Flight flight : flights) {
                        if (counter >= kNoOfRoutes) {
                            break;
                        }
                        FlightInfo fi = new FlightInfo(flight.getFlightNo(), TimeUtils
                                .timeInMinutes(flight.getStartTime(), flight.getEndTime()));
                        Route route = new Route(String.join("_",
                                Arrays.asList(flight.getSource(), flight.getDestination())),
                                fi);
                        directRoutes.add(route);
                        ++counter;
                    }
                }

        }
        if(counter <= kNoOfRoutes && Objects.nonNull(routes) && routes.size() >1) {
            inDirectlist = routes.get(1);
            for (ImmutablePair<List<Flight>, Integer> flightPath : inDirectlist){
                if(counter >= kNoOfRoutes) {
                    break;
                }

                FlightInfo fi = new FlightInfo(
                        String.join("_",flightPath.getLeft()
                                .stream().map(s->s.getFlightNo()).collect(
                                Collectors.toList())) ,
                        flightPath.getRight()
                );
                Route route = new Route(FlightsUtils.getAirportString(flightPath.getLeft()),
                        fi
                        );
                inDirectRoutes.add(route);
                ++counter;
            }

        }
        return possibleRoutesList;
    }
}
