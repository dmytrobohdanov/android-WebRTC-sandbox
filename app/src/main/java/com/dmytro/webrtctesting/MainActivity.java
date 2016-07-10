package com.dmytro.webrtctesting;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.LinkedList;


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

    private final String D_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (GLSurfaceView) findViewById(R.id.surfaceView);

        Log.d(D_TAG, "version 0.0.0.4");

        videoView.setEGLContextClientVersion(1);
        boolean hasAndroidGlobalsInitializedSuccessfully =
                PeerConnectionFactory.initializeAndroidGlobals(
                        this,
                        INITIALIZE_AUDIO,
                        INITIALIZE_VIDEO,
                        VIDEO_CODEC_HW_ACCELERATION,
                        VideoRendererGui.getEGLContext());

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


        // We start out with an empty MediaStream object,
        // created with help from our PeerConnectionFactory
        // Note that LOCAL_MEDIA_STREAM_ID can be any string
//        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream("someID");

// Now we can add our tracks.
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);

        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);

//        new PeerConnection.IceServer("stun:stun.l.google.com:19302")
        MediaConstraints constraints = new MediaConstraints();

        constraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToRecieveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));


        PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
            /**
             * Triggered when media is received on a new stream from remote peer.
             */
            @Override
            public void onAddStream(MediaStream mediaStream) {

            }

            /**
             * Triggered when a remote peer opens a DataChannel.
             */
            @Override
            public void onDataChannel(DataChannel dataChannel) {

            }

            /**
             *  Triggered when a new ICE candidate has been found.
             */
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {

            }

            /**
             * Triggered when the IceConnectionState changes.
             */
            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            }

            /**
             *  Triggered when the IceGatheringState changes.
             */
            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

            }

            /**
             *  Triggered when a remote peer close a stream.
             */
            @Override
            public void onRemoveStream(MediaStream mediaStream) {

            }

            /**
             * Triggered when renegotiation is necessary.
             */
            @Override
            public void onRenegotiationNeeded() {

            }

            /**
             * Triggered when the SignalingState changes.
             */
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {

            }
        };

        SdpObserver sdpObserver = new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {

            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        };

        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(
                configuration,
                constraints,
                peerConnectionObserver);

        peerConnection.addStream(mediaStream);

        peerConnection.createOffer(sdpObserver, constraints);
        peerConnection.createAnswer();

    }

}