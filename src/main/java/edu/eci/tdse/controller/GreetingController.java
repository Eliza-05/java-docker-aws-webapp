package edu.eci.tdse.controller;

import edu.eci.tdse.annotation.GetMapping;
import edu.eci.tdse.annotation.RequestParam;
import edu.eci.tdse.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;


@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/greeting/count")
    public String greetingWithCount(@RequestParam(value = "name", defaultValue = "World") String name) {
        long count = counter.incrementAndGet();
        return String.format("Request #%d — " + template, count, name);
    }
}
