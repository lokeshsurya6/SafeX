package com.androidplayground.rogue.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androidplayground.rogue.swamphacksandroid.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MainActivityHelper {

    public static String fileName = "contactsList";
    public static String fileName2 = "contactsNameList";
    public static void writeNumberToStorage(String number, Context context)
    {
        try {
            File fileToWrite = new File(context.getFilesDir(), fileName);
            if (!fileToWrite.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                fileToWrite.createNewFile();
            }

            number = number+"\n";
            FileOutputStream fos = new FileOutputStream(fileToWrite, true);
            Log.e("MainActivity", "Writing to the file:" + fileToWrite.getName());
            //mContcts.setText("Adding the text");
            fos.write(number.getBytes());
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void writeNameToStorage(String name, Context context)
    {
        try {
            File fileToWrite = new File(context.getFilesDir(), fileName2);
            if (!fileToWrite.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                fileToWrite.createNewFile();
            }

            name = name+"\n";
            FileOutputStream fos = new FileOutputStream(fileToWrite, true);
            Log.e("MainActivity", "Writing to the file:" + fileToWrite.getName());
            //mContcts.setText("Adding the text");
            fos.write(name.getBytes());
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static List<String> readContactsList(Context context) {
        StringBuilder text = new StringBuilder();

        Log.e("MainActivity","Reading the contacts list");
        File file = new File(context.getFilesDir(), fileName);
        if(!file.exists()) {
            Log.e("MainActivity","File does not exist");
            return null;
        }

        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                list.add(line);
                Log.e("MainActivity","Reading the LIst");
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> readContactsNameList(Context context) {
        StringBuilder text = new StringBuilder();

        Log.e("MainActivity","Reading the contacts list");
        File file = new File(context.getFilesDir(), fileName2);
        if(!file.exists()) {
            Log.e("MainActivity","File does not exist");
            return new ArrayList<>();
        }

        List<String> list = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                list.add(line);
                Log.e("MainActivity","Reading the LIst");
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        }

        return list;
    }

//    public static void updateListView(Context context, ListView listView) {
//        List<String> contactsList = MainActivityHelper.readContactsList(context);
//        if(contactsList!=null && contactsList.size() > 0) {
//            ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactsList);
//            listView.setAdapter(adapter);
//        }
//        else
//        {
//            Log.e("MainActivity","The contacts list is NULL");
//        }
//    }


    public static void sendMessage(Context context)
    {
        List<String> numbers = MainActivityHelper.readContactsList(context);
        GPSTracker gpsTracker = new GPSTracker(context);
        for(String num: numbers) {
            Log.e("MainActivity", "Message is Heloooooo");
            Location l = gpsTracker.getLocation();
            //Log.e("MainActivity", ("The location is:" + l));
            String message = "HELP ME!!!!";
            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(message);
            if (l != null) {
                double lat = l.getLatitude();
                double lon = l.getLongitude();
                String loc = "http://maps.google.com/maps?saddr=" + lat + "," + lon;
                smsBody.append(Uri.parse(loc));
                //Log.e("MainActivity", "Message is " + smsBody);
            }

            android.telephony.SmsManager.getDefault().sendTextMessage(num, null, smsBody.toString(), null, null);
        }
    }

    public static void playAlarm(Context context)
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
        if(ringtone.isPlaying())
        {
            ringtone.stop();
            return;
        }

        ringtone.play();
    }


    public static void record(final Context applicationContext) {
        final MediaRecorder myAudioRecorder = new MediaRecorder();
        String output = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/myrecording.3gp";
        Toast.makeText(applicationContext, "The dir is" + output, Toast.LENGTH_LONG).show();
        Log.e("MainActivityHelper","The external directory is " + output);
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(output);

        try {
            Toast.makeText(applicationContext, "Starting to record"+output, Toast.LENGTH_LONG).show();
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(applicationContext, "Stopping the record", Toast.LENGTH_LONG).show();
                myAudioRecorder.stop();
                myAudioRecorder.release();

            }
        }, 6000);
    }
}
