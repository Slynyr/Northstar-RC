package org.example.controller;

import nu.pattern.OpenCV;
import org.example.car.Connection;
import org.example.shared.PacketId;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Frame processing class
 */
public class FrameProcessor implements Runnable {
    private final Connection connection;

    public FrameProcessor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Handles received frames from rc car
     */
    @Override
    public void run() {
        OpenCV.loadLocally();
        Socket socket = connection.getSocket();

        if (socket != null && socket.isConnected()) {
            try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
                while (!socket.isClosed() && connection.isOpen()) {
                    byte packetId = inputStream.readByte();
                    int packetSize = inputStream.readInt();
                    byte[] packetData = new byte[packetSize];
                    inputStream.readFully(packetData);

                    if (packetId == PacketId.FRAME) {
                        // Process frame
                        Mat img = Imgcodecs.imdecode(new MatOfByte(packetData), Imgcodecs.IMREAD_COLOR);
                        HighGui.imshow("Received Frame", img);
                        HighGui.waitKey(1); // Refresh the window
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
                connection.close();
            }
        }
    }
}
