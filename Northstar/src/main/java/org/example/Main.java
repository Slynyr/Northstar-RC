package org.example;

import org.example.car.Car;
import org.example.controller.Controller;
import org.example.server.Server;

import java.io.IOException;

/**
 * Main Class
 */
public class Main {
    /**
     * Handles desired Nortstar component based on selected mode
     * @param args Mode
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java MainApp <mode> [options]");
            System.out.println("Modes: server, client");
            return;
        }

        String mode = args[0];

        switch (mode) {
            case "car":
                System.out.println("Running in CAR mode");
                try {
                    Car car = new Car(args[1]);
                    car.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "server":
                System.out.println("Running in SERVER mode");
                try {
                    Server server = new Server();
                    server.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "controller":
                System.out.println("Running in CONTROLLER mode");
                try {
                    Controller controller = new Controller(args[1]);
                    controller.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Invalid mode specified");
                break;
        }



    }
}