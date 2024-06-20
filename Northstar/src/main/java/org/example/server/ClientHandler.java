package org.example.server;

import org.example.shared.Configuration;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client handler class
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ConnectionManager connectionManager;

    /**
     * Client handler constructor
     * @param socket Socket
     * @param connectionManager Connection manager
     */
    public ClientHandler(Socket socket, ConnectionManager connectionManager) {
        this.socket = socket;
        this.connectionManager = connectionManager;
    }

    /**
     * Handles client connection
     */
    @Override
    public void run() {
        System.out.println("Client connected IP=" + this.socket.getRemoteSocketAddress().toString());

        // Get the input stream
        try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream())) {
            byte[] buffer = new byte[Configuration.SERVER_BUFFER_SIZE];
            int bytesRead;

            // Main loop
            // Read the data from the stream, broadcast it to all clients
            while ((bytesRead = bis.read(buffer)) != -1) {
                connectionManager.broadcast(buffer, bytesRead, socket);
            }
        } catch (IOException e) {
            System.err.println("Connection error with client: " + socket.getRemoteSocketAddress().toString() + " - " + e.getMessage());
        } finally {
            connectionManager.removeClient(socket);
            System.out.println("Client disconnected IP=" + this.socket.getRemoteSocketAddress().toString());
        }
    }
}
