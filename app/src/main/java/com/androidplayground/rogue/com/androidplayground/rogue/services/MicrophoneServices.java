package com.androidplayground.rogue.com.androidplayground.rogue.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MicrophoneServices extends IntentService {

    public MicrophoneServices()
    {
        super("MicrophoneServices");
    }
    @Override
    protected void onHandleIntent(@android.support.annotation.Nullable Intent intent) {

    }
}
