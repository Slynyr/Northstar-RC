package org.example.car;

import org.example.shared.Configuration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main car class
 */
public class Car {
    private Socket socket;
    private volatile boolean running = true;
    private ExecutorService videoExecutor;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final Camera camera;
    private final FrameStreamer frameStreamer;
    private final CommandProcessor commandProcessor;

    private final Connection connection;

    /**
     * Car constructor
     * @param serverAddress Target server address
     * @throws Exception Exception
     */
    public Car(String serverAddress) throws Exception {
        // Open a connection to the server
        connection = new Connection(serverAddress, Configuration.SERVER_PORT);

        // Create a new instance of the Camera class
        camera = new Camera();

        // Create a new instance of the Command Processor class
        commandProcessor = new CommandProcessor();

        // Create a new instance of the Frame Streamer class
        frameStreamer = new FrameStreamer(connection);

        // Attempt to start the camera
        if (!camera.openCamera()) {
            throw new Exception("Fatal error: Could not acquire the camera");
        }
    }

    public void run() {
        // Add a shutdown hook. This is used to gracefully stop the everything
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            connection.close();
            camera.closeCamera();
            if (videoExecutor != null) {
                videoExecutor.shutdownNow();
                try {
                    videoExecutor.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Client stopped.");
        }));


        // Main loop
        while (running) {
            // If the connection is closed, attempt to open it. Try to reconnect each RECONNECT_TIME
            if (connection.isClosed()) {
                System.out.println("Connecting to server...");
                if (!connection.open()) {
                    System.out.println("Failed to connect. Retrying in " + Configuration.RECONNECT_TIME + "ms...");
                    try {
                        Thread.sleep(Configuration.RECONNECT_TIME);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        running = false;
                    }
                } else {
                    // Successfully opened the connection. We can not begin to stream the frames
                    System.out.println("Connected to server.");
                    System.out.println("Starting video streaming task...");

                    // Start the frame streamer on a separate thread
                    videoExecutor = Executors.newSingleThreadExecutor();
                    videoExecutor.submit(() -> {
                        System.out.println("Inside submitted task.");
                        frameStreamer.start(camera);
                    });
                    System.out.println("Video streaming task started...");
                }
            }

            // Connect is open
            while (running && connection.isOpen()) {
                // Try to read in the bytes from the input stream. Pass the command received to the Command Process class
                try (DataInputStream inputStream = new DataInputStream(connection.getSocket().getInputStream())) {
                    byte packetId = inputStream.readByte();
                    int packetSize = inputStream.readInt();
                    byte[] packetData = new byte[packetSize];
                    inputStream.readFully(packetData);
                    commandProcessor.processCommand(packetId);
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                    connection.close();
                    camera.closeCamera();
                }
            }

            // Connection is closed (lost)
            // Stop the frame streamer
            if (videoExecutor != null) {
                videoExecutor.shutdownNow();
                try {
                    videoExecutor.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Check if we should continue to run, stop otherwise
            if(!running){
                break;
            }

            // Sleep for RECONNECT time
            try {
                Thread.sleep(Configuration.RECONNECT_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }

            // Attempt to reopen the camera
            if (!camera.openCamera()) {
                System.out.println("Fatal error: Could not acquire the camera");
                running = false;
            }
        }
    }

    /**
     * Car handler
     * @param args args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Car <server-address>");
            return;
        }
        try {
            Car car = new Car(args[0]);
            car.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
