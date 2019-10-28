package com.example.three.demo.Hello;

import org.springframework.web.bind.annotation.RequestMapping;

public class Hello {
    @RequestMapping("/h")
    String hello(){
        return "Hello";
    }
}
