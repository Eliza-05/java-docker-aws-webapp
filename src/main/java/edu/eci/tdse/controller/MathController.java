package edu.eci.tdse.controller;

import edu.eci.tdse.annotation.GetMapping;
import edu.eci.tdse.annotation.RequestParam;
import edu.eci.tdse.annotation.RestController;


@RestController
public class MathController {

    @GetMapping("/square")
    public String square(@RequestParam(value = "num", defaultValue = "1") String num) {
        try {
            double n = Double.parseDouble(num);
            return "Square of " + num + " = " + (n * n);
        } catch (NumberFormatException e) {
            return "Invalid number: " + num;
        }
    }

    @GetMapping("/euler")
    public String euler() {
        return "e = " + Math.E;
    }

    @GetMapping("/pi")
    public String pi() {
        return "Pi = " + Math.PI;
    }
}
