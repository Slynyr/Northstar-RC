package org.example.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server connection manager class
 */
public class ConnectionManager {
    private final List<Socket> clients = new CopyOnWriteArrayList<>();

    /**
     * Adds client to list of clients
     * @param clientSocket Client to add
     */
    public void addClient(Socket clientSocket) {
        clients.add(clientSocket);
    }

    /**
     * Remove client from list of clients
     * @param clientSocket Client to remove
     */
    public void removeClient(Socket clientSocket) {
        clients.remove(clientSocket);
        closeConnection(clientSocket);
    }

    /**
     * Broadcast data to all clients in client list
     * @param data Data to broadcast
     * @param length Data length
     * @param senderSocket Sender socket
     */
    public void broadcast(byte[] data, int length, Socket senderSocket) {
        for (Socket client : clients) {
            if (client != senderSocket) {
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
                    bos.write(data, 0, length);
                    bos.flush();
                } catch (IOException e) {
                    System.err.println("Error broadcasting to client: " + e.getMessage());
                    removeClient(client);
                }
            }
        }
    }

    /**
     * Iterate over all clients, and close all connections
     */
    public void closeAllConnections() {
        for (Socket client : clients) {
            closeConnection(client);
        }
        clients.clear();
    }

    /**
     * Close connection of specified client
     * @param clientSocket Client to close connection
     */
    private void closeConnection(Socket clientSocket) {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}
