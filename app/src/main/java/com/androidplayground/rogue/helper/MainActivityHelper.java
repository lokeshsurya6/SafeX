package com.androidplayground.rogue.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidplayground.rogue.swamphacksandroid.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MainActivityHelper {
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;

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


    public static void sendMessage(Context context,double latitude,double longitude)
    {

        List<String> numbers = MainActivityHelper.readContactsList(context);
        GPSTracker gpsTracker = new GPSTracker(context);
        for(String num: numbers) {
            Log.e("MainActivity", "Message is Heloooooo");
            Location l = gpsTracker.getLocation();
            //Log.e("MainActivity", ("The location is:" + l));
            String message = "HELP ME!!!!\n";
            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(message);
    //        if (l != null) {
    //            double lat = l.getLatitude();
    //            double lon = l.getLongitude();
                String loc = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
                smsBody.append(Uri.parse(loc));
                Log.e("MainActivity", "Message is " + smsBody);
     //       }

            android.telephony.SmsManager.getDefault().sendTextMessage(num, null, smsBody.toString(), null, null);
        }
    }

    public static void playAlarm(Context context)
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
        ringtone.play();
    }


}
