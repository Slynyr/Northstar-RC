package org.example.server;

import org.example.shared.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main server class
 */
public class Server {
    private ServerSocket serverSocket;
    private volatile boolean running = true;
    private ConnectionManager connectionManager;

    /**
     * Server constructor that initializes server socket
     * @throws IOException Exception
     */
    public Server() throws IOException {
        serverSocket = new ServerSocket(Configuration.SERVER_PORT);
        connectionManager = new ConnectionManager();
    }

    /**
     * Main server loop
     */
    public void run() {
        // Add a shutdown hook so that we can gracefully shut down in case of CTRL-C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            connectionManager.closeAllConnections();
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
            }
            System.out.println("Server stopped.");
        }));

        System.out.println("Server started!");

        // Main loop
        while (running) {
            try {
                // Continuously wait for clients to connect. Once a client is connected,
                // create a Client instance class and schedule it to be executed on a new/separate thread
                Socket clientSocket = serverSocket.accept();
                connectionManager.addClient(clientSocket);
                new Thread(new ClientHandler(clientSocket, connectionManager)).start();
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
        System.out.println("Server stopped!");
    }

}
