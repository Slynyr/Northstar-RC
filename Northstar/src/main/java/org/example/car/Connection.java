package org.example.car;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Connection handler class
 */
public class Connection {
    private final InetAddress serverAddress;
    private final int serverPort;
    private Socket socket;
    private volatile boolean connected = false;

    /**
     * Connection handler constructor
     * @param serverAddress Target server address
     * @param serverPort Target server port
     * @throws IOException Exception
     */
    public Connection(String serverAddress, int serverPort) throws IOException {
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
    }

    /**
     * Opens connection to server
     * @return Is connected
     */
    public boolean open() {
        try {
            socket = new Socket(serverAddress, serverPort);
            connected = true;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Closes connectio to server
     */
    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        } finally {
            connected = false;
        }
    }

    /**
     * Returns server state
     * @return Is server open
     */
    public boolean isOpen() {
        return connected;
    }

    /**
     * Returns server state
     * @return Is server closed
     */
    public boolean isClosed() {
        // TODO depricate as isOpen() already provides obj
        return !connected;
    }

    /**
     * Returns socket
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }
}
