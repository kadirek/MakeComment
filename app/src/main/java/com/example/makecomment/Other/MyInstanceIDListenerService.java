package com.example.makecomment.Other;

import com.google.firebase.messaging.RemoteMessage;

public class MyInstanceIDListenerService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    }
}