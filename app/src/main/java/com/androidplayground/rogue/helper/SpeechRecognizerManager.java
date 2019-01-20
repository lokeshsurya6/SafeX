package com.androidplayground.rogue.helper;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class SpeechRecognizerManager {

    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;

    protected boolean mIsListening;
    private boolean mIsStreamSolo;

    private boolean mMute=true;
    private final static String TAG="SpeechRecognizerManager";

    private onResultsReady mListener;

    public SpeechRecognizerManager(Context context, onResultsReady listener) {
        try{
            mListener=listener;
        }
        catch(ClassCastException e)
        {
            Log.e(TAG,e.toString());
        }
        Log.d("SpeechRecognizerManager","In the SpeechRecognizerManager");
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                context.getPackageName());
        startListening();
    }

    private void listenAgain()
    {
        if(mIsListening) {
            mIsListening = false;
            mSpeechRecognizer.cancel();
            startListening();
        }
    }


    private void startListening()
    {
        if(!mIsListening)
        {
            mIsListening = true;
                // turn off beep sound
                if (!mIsStreamSolo && mMute) {
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                    //mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
                    mIsStreamSolo = true;
                }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    public void destroy()
    {
        mIsListening=false;
        if (mIsStreamSolo) {
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 1);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 1);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 1);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 1);
            //mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 1);
            mIsStreamSolo = true;
            mIsStreamSolo = true;
        }
        Log.d(TAG, "onDestroy");
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer=null;
        }

    }

    //-----------SpeechRecognitionListener
    protected class SpeechRecognitionListener implements RecognitionListener
    {

        @Override
        public void onError(int error) {

            if(error==SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
            {
                if(mListener!=null) {
                    ArrayList<String> errorList=new ArrayList<String>(1);
                    errorList.add("ERROR RECOGNIZER BUSY");
                    if(mListener!=null)
                        mListener.onResults(errorList);
                }
                return;
            }

            if(error==SpeechRecognizer.ERROR_NO_MATCH)
            {
                if(mListener!=null)
                    mListener.onResults(null);
            }

            if(error==SpeechRecognizer.ERROR_NETWORK)
            {
                ArrayList<String> errorList=new ArrayList<String>(1);
                errorList.add("STOPPED LISTENING");
                if(mListener!=null)
                    mListener.onResults(errorList);
            }
            Log.d(TAG, "error = " + error);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listenAgain();
                }
            },100);

        }

        @Override
        public void onResults(Bundle results) {
            if(results!=null && mListener!=null)
                mListener.onResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
            listenAgain();
        }


        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.e("SpeechManager","Ready for speech");

        }

        @Override
        public void onBeginningOfSpeech() {
            Log.e("SpeechManager","Beginning of speech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.e("SpeechManager","on rms changed speech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.e("SpeechManager","on buffer received for speech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e("SpeechManager","on end of speech");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.e("SpeechManager","ON partial result from speech");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.e("SpeechManager","Event for speech");
        }
    }

    public boolean ismIsListening() {
        return mIsListening;
    }

    public interface onResultsReady
    {
        public void onResults(ArrayList<String> results);
    }

    public void mute(boolean mute)
    {
        mMute=mute;
    }

    public boolean isInMuteMode()
    {
        return mMute;
    }
}
