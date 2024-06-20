package org.example.car;

import org.example.shared.PacketId;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Frame streamer class
 */
public class FrameStreamer {
    private final Connection connection;
    private volatile boolean running = true;

    public FrameStreamer(Connection connection) {
        this.connection = connection;
    }

    /**
     * Sends specified frame to server
     * @param frame Frame to send
     * @throws IOException Exception
     */
    public void sendFrame(Mat frame) throws IOException {
        MatOfByte frameBuffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, frameBuffer, new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 75));
        byte[] frameData = frameBuffer.toArray();

        DataOutputStream outputStream = new DataOutputStream(connection.getSocket().getOutputStream());

        // Write Frame Packet ID
        outputStream.write(PacketId.FRAME);
        // Write the frame size
        outputStream.write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(frameData.length).array());
        // Write the frame data
        outputStream.write(frameData);
        outputStream.flush();
    }

    /**
     * Sets running variable to false
     */
    public void stop() {
        running = false;
    }

    /**
     * Starts video streamer
     * @param camera Camera
     */
    public void start(Camera camera) {
        System.out.println("Video streaming task");
        running = true;
        new Thread(() -> {
            System.out.println("Video streaming executing");
            Mat frame = new Mat();
            while (running && connection.isOpen()) {
                if (!camera.readFrame(frame)) {
                    continue;
                }
                try {
                    sendFrame(frame);
                } catch (IOException e) {
                    running = false;
                }
            }
            System.out.println("Video streaming task completed");

        }).start();
        System.out.println("Video streaming task");

    }
}
