package com.androidplayground.rogue.com.androidplayground.rogue.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MicrophoneServices extends Service {
    //@androidx.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
