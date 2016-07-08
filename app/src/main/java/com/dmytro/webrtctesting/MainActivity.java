package com.dmytro.webrtctesting;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;


/**
 * Playing with WebRTC on Android
 * using https://tech.appear.in/2015/05/25/Introduction-to-WebRTC-on-Android/
 */
public class MainActivity extends AppCompatActivity {
    private static final String VIDEO_TRACK_ID = "id001";
    private static final String AUDIO_TRACK_ID = VIDEO_TRACK_ID;
    private final boolean INITIALIZE_AUDIO = true;
    private final boolean INITIALIZE_VIDEO = true;
    private final boolean VIDEO_CODEC_HW_ACCELERATION = true;
    private GLSurfaceView videoView;
    private VideoTrack localVideoTrack;

    private final String D_TAG = "111MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (GLSurfaceView) findViewById(R.id.surfaceView);

        Log.d(D_TAG, "version 0.0.0.4");

        videoView.setEGLContextClientVersion(1);
        boolean peerConnection = PeerConnectionFactory.initializeAndroidGlobals(this, INITIALIZE_AUDIO, INITIALIZE_VIDEO, VIDEO_CODEC_HW_ACCELERATION, null);
        //todo: check peerConnection for success

        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();

        // Returns the number of camera devices
//        int numberOfCameras= VideoCapturerAndroid.getDeviceCount();
//        String name = VideoCapturerAndroid.getNameOfFrontFacingDevice();
//        String name = VideoCapturerAndroid.getNameOfBackFacingDevice();

        String name = VideoCapturerAndroid.getDeviceName(0);

        // Creates a VideoCapturerAndroid instance for the device name
        VideoCapturerAndroid capturer = VideoCapturerAndroid.create(name);

        MediaConstraints videoConstraints = new MediaConstraints();

        // First we create a VideoSource
        VideoSource videoSource = peerConnectionFactory.createVideoSource(capturer, videoConstraints);

        // Once we have that, we can create our VideoTrack
        // Note that VIDEO_TRACK_ID can be any string that uniquely
        // identifies that video track in your application
        localVideoTrack =
                peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

        MediaConstraints audioConstraints = new MediaConstraints();
        // First we create an AudioSource
        AudioSource audioSource =
                peerConnectionFactory.createAudioSource(audioConstraints);

        // Once we have that, we can create our AudioTrack
        // Note that AUDIO_TRACK_ID can be any string that uniquely
        // identifies that audio track in your application
        AudioTrack localAudioTrack =
                peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
            }
        });

        try {
            //todo change sizes to constants
            VideoRenderer renderer = VideoRendererGui.createGui(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FIT, false);
            localVideoTrack.addRenderer(renderer);
        } catch (Exception e) {
            String s = e.getMessage();
            Log.e(D_TAG, s);
        }


    }
}