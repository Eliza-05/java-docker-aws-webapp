package edu.eci.tdse.controller;

import edu.eci.tdse.annotation.GetMapping;
import edu.eci.tdse.annotation.RestController;


@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World! — served by HelloController via @GetMapping";
    }
}
