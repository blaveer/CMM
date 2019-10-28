package com.example.two.demo.Demo;

import org.springframework.web.bind.annotation.RequestMapping;

public class hello {
    @RequestMapping("/hello")
    public String hello(){
        return "hello";
    }
}
