package org.example.controller;

import nu.pattern.OpenCV;
import org.example.car.Connection;
import org.example.shared.Configuration;
import org.opencv.core.Core;

import java.io.IOException;

/**
 * Controller class
 */
public class Controller {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private final Connection connection;
    private final CommandSender commandSender;
    private final FrameProcessor frameProcessor;

    private volatile boolean running = true;

    /**
     * Controller constructor
     * @param serverAddress Target server address
     * @throws Exception Exception
     */
    public Controller(String serverAddress) throws Exception {
        OpenCV.loadLocally();
        connection = new Connection(serverAddress, Configuration.SERVER_PORT);
        commandSender = new CommandSender(connection);
        frameProcessor = new FrameProcessor(connection);
    }

    /**
     * Handles controller connection
     */
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            connection.close();
            System.out.println("Controller stopped.");
        }));

        while (running) {
            if (connection.isClosed()) {
                try {
                    if (!connection.open()) {
                        Thread.sleep(Configuration.RECONNECT_TIME);
                    } else {
                        new Thread(frameProcessor).start();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // NOTE
    // All the commands below are functional placeholders that demonstrate Northstar's capability for sending control data

    /**
     * Sends accelerate command
     * @throws IOException Exception
     */
    public void accelerate() throws IOException {
        commandSender.SendAccelerateCommand();
    }

    /**
     * Sends decelerate command
     * @throws IOException Exception
     */
    public void decelerate() throws IOException {
        commandSender.SendBrakeCommand();
    }

    /**
     * Sends steer left command
     * @throws IOException Exception
     */
    public void steerLeft() throws IOException {
        commandSender.SendSteerLeftCommand();
    }

    /**
     * Sends steer right command
     * @throws IOException Exception
     */
    public void steerRight() throws IOException {
        commandSender.SendSteerRightCommand();
    }

}
