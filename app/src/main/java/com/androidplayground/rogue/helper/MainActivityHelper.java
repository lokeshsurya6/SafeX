package com.androidplayground.rogue.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.androidplayground.rogue.swamphacksandroid.MainActivity;
import com.androidplayground.rogue.swamphacksandroid.MyListAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MainActivityHelper {
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;

    public static String fileName = "contactsList";
    public static String fileName2 = "contactsNameList";
    public static String fileName3 = "hardWordFile";
    public static void writeNumberToStorage(String number, Context context)
    {
        try {
            File fileToWrite = new File(context.getFilesDir(), fileName);
            if (!fileToWrite.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                fileToWrite.createNewFile();
            }

            boolean flag=true;
            Scanner scanner=new Scanner(fileToWrite);
            while(scanner.hasNextLine()){
                if(number.equals(scanner.nextLine().trim())){
                    flag=false;
                    break;
                }
            }

            if(flag){
                number = number+"\n";
                FileOutputStream fos = new FileOutputStream(fileToWrite, true);
                Log.e("MainActivity", "Writing to the file:" + fileToWrite.getName());
                //mContcts.setText("Adding the text");
                fos.write(number.getBytes());
                fos.close();
            }
            else{
                Toast.makeText(context, "Contatc already exists", Toast.LENGTH_SHORT).show();
            }
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


            boolean flag=true;
            Scanner scanner=new Scanner(fileToWrite);
            while(scanner.hasNextLine()){
                if(name.equals(scanner.nextLine().trim())){
                    flag=false;
                    break;
                }
            }

            if(flag){
                name = name+"\n";
                FileOutputStream fos = new FileOutputStream(fileToWrite, true);
                Log.e("MainActivity", "Writing to the file:" + fileToWrite.getName());
                //mContcts.setText("Adding the text");
                fos.write(name.getBytes());
                fos.close();
            }
            else{
                Toast.makeText(context, "Contatc already exists", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void delNumberFunction(String num, Context context){
        try{
            File inputFile = new File(context.getFilesDir(), fileName);
            if(!inputFile.exists()) {
                Log.e("MainActivity","File does not exist");
            }
            File outputFile= new File(context.getFilesDir(), "temp");
            if (!outputFile.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                outputFile.createNewFile();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                // Read each line from the reader and compare it with
                // with the line to remove and write if required
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(num)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            if (inputFile.delete()) {
                // Rename the output file to the input file
                if (!outputFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp to " + fileName);
                }
            } else {
                throw new IOException("Could not delete original input file " + fileName);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void delNameFunction(String name, Context context){
        try{
            File inputFile = new File(context.getFilesDir(), fileName2);
            if(!inputFile.exists()) {
                Log.e("MainActivity","File does not exist");
            }
            File outputFile= new File(context.getFilesDir(), "temp");
            if (!outputFile.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                outputFile.createNewFile();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                // Read each line from the reader and compare it with
                // with the line to remove and write if required
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(name)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            if (inputFile.delete()) {
                // Rename the output file to the input file
                if (!outputFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp to " + fileName2);
                }
            } else {
                throw new IOException("Could not delete original input file " + fileName2);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void updateHarWord(Context context, String newWord){
        try{
            File inputFile = new File(context.getFilesDir(), fileName3);
            if(!inputFile.exists()) {
                Log.e("MainActivity","File does not exist");
            }
            File outputFile= new File(context.getFilesDir(), "temp1");
            if (!outputFile.exists()) {
                Log.e("MainActivity", "File does not exist, creating one");
                outputFile.createNewFile();
            }
            //Toast.makeText(context, "Test is" + newWord, Toast.LENGTH_LONG).show();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(newWord);
            if (inputFile.delete()) {
                // Rename the output file to the input file
                if (!outputFile.renameTo(inputFile)) {
                    throw new IOException("Could not rename temp to " + fileName3);
                }
            } else {
                throw new IOException("Could not delete original input file " + fileName3);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String readHardWord(Context context){
        File file = new File(context.getFilesDir(), fileName3);
        String line="";
        if(!file.exists()) {
            Log.e("MainActivity","File does not exist");
            return null;
        }
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            line= br.readLine();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return line;
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
    //        if (l != null) { git remote set-url origin https://github.com/Prudhvee/SwampHacks.git

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

        if(ringtone.isPlaying())
        {
            ringtone.stop();
            return;
        }

        ringtone.play();
    }

    public static void updateListView(ListView listView, MainActivity act, Context context) {
        List<String> contactsList = MainActivityHelper.readContactsList(context);
        List<String> contactsNameList = MainActivityHelper.readContactsNameList(context);
        if(contactsList!=null && contactsNameList!=null && contactsNameList.size() > 0 && contactsList.size() > 0) {
            //ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactsList);
            MyListAdapter adapter=new MyListAdapter(act ,context, contactsNameList, contactsList);
            listView.setAdapter(adapter);
        }
        else
        {
            Log.e("MainActivity","The contacts list is NULL");
        }
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
