package org.example.controller;

import org.example.car.Connection;
import org.example.shared.PacketId;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Command sender class
 */
public class CommandSender {
    private final Connection connection;

    public CommandSender(Connection connection) {
        this.connection = connection;
    }

    /**
     * Sends accelerate command
     * @throws IOException Exception
     */
    public void SendAccelerateCommand() throws IOException {
        sendCommand((byte)PacketId.ACCELERATE_COMMAND);
    }

    /**
     * Sends brake command
     * @throws IOException Exception
     */
    public void SendBrakeCommand() throws IOException {
        sendCommand((byte)PacketId.BRAKE_COMMAND);
    }

    /**
     * Sends steer left command
     * @throws IOException Exception
     */
    public void SendSteerLeftCommand() throws IOException {
        sendCommand((byte)PacketId.STEER_LEFT_COMMAND);
    }

    /**
     * Sends steer right command
     * @throws IOException Exception
     */
    public void SendSteerRightCommand() throws IOException {
        sendCommand((byte) PacketId.STEER_RIGHT_COMMAND);
    }

    /**
     * Sends specified command
     * @param commandId Command to send
     * @throws IOException Exception
     */
    public void sendCommand(byte commandId) throws IOException {
        Socket socket = connection.getSocket();
        if (socket != null && socket.isConnected()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.write(commandId);
            dataOutputStream.write(1);
            socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
        }
    }
}
