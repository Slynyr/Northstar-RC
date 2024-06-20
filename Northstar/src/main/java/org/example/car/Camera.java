package org.example.car;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * OpenCV camera class
 */
public class Camera {

    private VideoCapture videoCamera;

    public Camera() {
        OpenCV.loadLocally();
    }

    /**
     * Initializes camera
     * @return Is camera initialized
     */
    public boolean openCamera() {
        videoCamera = new VideoCapture(0); // Open default camera
        if (!videoCamera.isOpened()) {
            System.out.println("Error: Could not open camera");
            return false;
        }
        videoCamera.set(Videoio.CAP_PROP_FPS, 45.0);
        videoCamera.set(Videoio.CAP_PROP_FRAME_WIDTH, 1024);
        videoCamera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 768);

        System.out.println("Camera opened with resolution: " +
                videoCamera.get(Videoio.CAP_PROP_FRAME_WIDTH) + "x" +
                videoCamera.get(Videoio.CAP_PROP_FRAME_HEIGHT) +
                " at " + videoCamera.get(Videoio.CAP_PROP_FPS) + " FPS");
        return true;
    }

    /**
     * Closes camera
     */
    public void closeCamera() {
        if (videoCamera != null && videoCamera.isOpened()) {
            videoCamera.release();
            System.out.println("Camera released.");
        }
    }

    /**
     * Returns latest frame from camera
     * @param frame frame
     * @return Camera frame
     */
    public boolean readFrame(Mat frame) {
        return videoCamera.read(frame);
    }
}
