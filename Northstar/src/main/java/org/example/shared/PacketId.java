package org.example.shared;

public class PacketId {
    private PacketId() {}

    public static final byte FRAME = 1;

    public static final byte ACCELERATE_COMMAND = 2;
    public static final byte BRAKE_COMMAND = 3;

    public static final byte STEER_LEFT_COMMAND = 4;

    public static final byte STEER_RIGHT_COMMAND = 5;
}