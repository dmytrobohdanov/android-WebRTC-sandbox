package com.dmytro.webrtctesting;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SDPObserver implements SdpObserver {
    private SessionDescription localSdp;

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
}