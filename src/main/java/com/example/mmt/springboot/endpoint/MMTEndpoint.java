package com.example.mmt.springboot.endpoint;

import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.resourcebean.HelloWorldResourceBean;
import com.example.mmt.springboot.service.RouteFinderService;
import com.example.mmt.springboot.utility.timeutils.TimeUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MMTEndpoint {

    private final RouteFinderService routeFinderService;

    public MMTEndpoint(RouteFinderService routeFinderService) {
        this.routeFinderService = routeFinderService;
    }

    @RequestMapping(method = RequestMethod.GET , path = "/hello-world-resource-bean")
    public HelloWorldResourceBean helloWorldResourceBean(){
        System.out.println(routeFinderService.getRoutes("ATQ", "BLR"));
        return new HelloWorldResourceBean("Hello, World");

    }

    @RequestMapping(method = RequestMethod.GET , path = "/get-flights")
    public void getFlights(@RequestParam String source, @RequestParam String destination, @RequestParam Integer kNoOfRoutes){
        List<List<ImmutablePair<List<Flight>, Integer>>> routes = routeFinderService.getRoutes(source, destination);
        for(List<ImmutablePair<List<Flight>, Integer>> listOfFlight : routes){
            for(ImmutablePair<List<Flight>, Integer> flights : listOfFlight){
                List<Flight> fls = flights.getLeft();
                Integer time = flights.getRight();
                for(Flight fl : fls) {

                    System.out.println(
                            fl.getFlightNo() + " " + fl.getSource() + " " + fl.getDestination()
                                    + " " + fl.getStartTime() + " " + fl.getEndTime()
                                    + " " + TimeUtils
                                    .timeInMinutes(fl.getStartTime(), fl.getEndTime()));
                }
                System.out.println(time);
            }
        }
    }
}
