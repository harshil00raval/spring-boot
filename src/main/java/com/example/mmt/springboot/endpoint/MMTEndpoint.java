package com.example.mmt.springboot.endpoint;

import com.example.mmt.springboot.resourcebean.HelloWorldResourceBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MMTEndpoint {

    @RequestMapping(method = RequestMethod.GET , path = "/hello-world")
    public String helloWorld(){
        return "Hello, World";
    }

    @RequestMapping(method = RequestMethod.GET , path = "/hello-world-resource-bean")
    public HelloWorldResourceBean helloWorldResourceBean(){
        return new HelloWorldResourceBean("Hello, World");
    }
}
