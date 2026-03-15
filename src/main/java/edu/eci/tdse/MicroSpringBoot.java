package edu.eci.tdse;

import edu.eci.tdse.ioc.ComponentScanner;
import edu.eci.tdse.server.HttpServer;


public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {
        System.out.println("=== MicroSpringBoot IoC Framework ===");

        if (args.length > 0) {
            // Mode 1: explicit class name from command line
            System.out.println("Mode: Explicit class registration");
            for (String className : args) {
                ComponentScanner.registerByName(className);
            }
        } else {
            // Mode 2: auto-scan the classpath
            System.out.println("Mode: Auto-scan classpath for @RestController components");
            ComponentScanner.scanAndRegister();
        }

        HttpServer.port(8080);
        HttpServer.start();
    }
}
