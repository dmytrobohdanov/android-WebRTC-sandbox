package com.dmytro.caller;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dmytro.webrtctesting.R;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;


public class MainActivity extends AppCompatActivity {
    private static final String VIDEO_TRACK_ID = "id001";
    private static final String AUDIO_TRACK_ID = VIDEO_TRACK_ID;
    private final boolean INITIALIZE_AUDIO = true;
    private final boolean INITIALIZE_VIDEO = false;
    private final boolean VIDEO_CODEC_HW_ACCELERATION = false;
    private GLSurfaceView videoView;
    private VideoTrack localVideoTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //todo: check is everything ok and initializeAndroidGlobals returned TRUE
        PeerConnectionFactory.initializeAndroidGlobals(
                this,
                INITIALIZE_AUDIO,
                INITIALIZE_VIDEO,
                VIDEO_CODEC_HW_ACCELERATION,
                null);

        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();

        // Returns the number of camera devices
        VideoCapturerAndroid.getDeviceCount();

        // Returns the front face device name
//        VideoCapturerAndroid.getNameOfFrontFacingDevice();
        // Returns the back facing device name
//        VideoCapturerAndroid.getNameOfBackFacingDevice();

        // Creates a VideoCapturerAndroid instance for the device name
        VideoCapturerAndroid capturer =
                VideoCapturerAndroid.create(VideoCapturerAndroid.getNameOfFrontFacingDevice());


        MediaConstraints videoConstraints= new MediaConstraints();
        // First we create a VideoSource
        VideoSource videoSource =
                peerConnectionFactory.createVideoSource(capturer, videoConstraints);

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


        // To create our VideoRenderer, we can use the
        // included VideoRendererGui for simplicity
        // First we need to set the GLSurfaceView that it should render to
        videoView = (GLSurfaceView) findViewById(R.id.surfaceView);

        // Then we set that view, and pass a Runnable
        // to run once the surface is ready
        VideoRendererGui.setView(videoView, new Runnable() {
            @Override
            public void run() {
                // Now that VideoRendererGui is ready, we can get our VideoRenderer
                VideoRenderer renderer = null;
                try {
                    renderer = VideoRendererGui.createGui(
                            0,
                            0,
                            videoView.getWidth(),
                            videoView.getHeight(),
                            VideoRendererGui.ScalingType.SCALE_ASPECT_FILL,
                            false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // And finally, with our VideoRenderer ready, we
                // can add our renderer to the VideoTrack.
                assert renderer != null;
                localVideoTrack.addRenderer(renderer);
            }
        });


    }
}
