package com.example.mmt.springboot.endpoint;

import com.example.mmt.springboot.domain.transport.Flight;
import com.example.mmt.springboot.mapper.PossibleRouteListResponseMapper;
import com.example.mmt.springboot.resourcebean.HelloWorldResourceBean;
import com.example.mmt.springboot.service.RouteFinderService;
import org.apache.commons.lang3.tuple.ImmutablePair;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public String getFlights(@RequestParam String source, @RequestParam String destination, @RequestParam Integer kNoOfRoutes){
        List<List<ImmutablePair<List<Flight>, Integer>>> routes = routeFinderService.getRoutes(source, destination);
        return PossibleRouteListResponseMapper.mapToPossibleRouteList(routes, kNoOfRoutes).toString();
    }
}
