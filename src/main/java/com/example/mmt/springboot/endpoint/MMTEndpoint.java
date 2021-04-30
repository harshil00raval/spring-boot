package com.example.mmt.springboot.endpoint;

import com.example.mmt.springboot.resourcebean.HelloWorldResourceBean;
import com.example.mmt.springboot.service.RouteFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MMTEndpoint {

    private final RouteFinderService routeFinderService;

    public MMTEndpoint(RouteFinderService routeFinderService) {
        this.routeFinderService = routeFinderService;
    }

    @RequestMapping(method = RequestMethod.GET , path = "/hello-world")
    public String helloWorld(){
        return "Hello, World";
    }

    @RequestMapping(method = RequestMethod.GET , path = "/hello-world-resource-bean")
    public HelloWorldResourceBean helloWorldResourceBean(){
        System.out.println(routeFinderService.getRoutes("ATQ", "BLR"));
        return new HelloWorldResourceBean("Hello, World");

    }

    @RequestMapping(method = RequestMethod.GET , path = "/get-flights")
    public void getFlights(@RequestParam String source, @RequestParam String destination, @RequestParam Integer kNoOfRoutes){
        System.out.println(routeFinderService.getRoutes(source, destination));
    }
}
