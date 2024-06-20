package org.example.car;

import org.example.shared.PacketId;

/**
 * Command processing class
 */
public class CommandProcessor {

    /**
     * Processes incomming commands
     * @param packetId Packet ID of command packet
     */
    public void processCommand(byte packetId) {
        switch (packetId) {
            case PacketId.ACCELERATE_COMMAND:
                handleAccelerate();
                break;
            case PacketId.BRAKE_COMMAND:
                handleBreak();
                break;
            case PacketId.STEER_LEFT_COMMAND:
                handleLeft();
                break;
            case PacketId.STEER_RIGHT_COMMAND:
                handleRight();
                break;
            default:
                System.out.println("Received unknown packet");
        }
    }

    /**
     * Run accelerate command
     */
    private void handleAccelerate() {
        System.out.println("Received ACCELERATE command");
    }

    /**
     * Run hand break command
     */
    private void handleBreak() {
        System.out.println("Received BREAK command");
    }

    /**
     * Run handle left command
     */
    private void handleLeft() {
        System.out.println("Received STEER LEFT command");
    }

    /**
     * Run handle right command
     */
    private void handleRight() {
        System.out.println("Received STEER RIGHT command");
    }
}
