package com.example.mmt.springboot.resourcebean;

public class HelloWorldResourceBean {

    @Override public String toString() {
        return "HelloWorldResourceBean{" + "message='" + message + '\'' + '}';
    }

    public String getMessage() {
        return message;
    }

    private final String message;

    public HelloWorldResourceBean(String message){
        this.message = message;
    }


}
