package com.androidplayground.rogue.swamphacksandroid;

import android.Manifest;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplayground.rogue.helper.MainActivityHelper;
import com.androidplayground.rogue.helper.SpeechRecognizerManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    //private TextView result_tv;
    private ImageButton addButton;
    //private Button sendMessageButton;
    private TextView result_tv;
    private Button sendMessageButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;

    private static final int PERMISSION_SEND_SMS = 1;
    private ListView listView;
    public ListView getListView()
    {
        return listView;
    }
    private ImageButton recordVoiceBtn;
    private ImageButton stopRecordVoiceBtn;
    private  ImageButton editBtn;
    private  ImageButton saveBtn;
    private SpeechRecognizerManager mSpeechManager;
    /*private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };*/
    static final private int PICK_CONTACT = 1;
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton= (ImageButton ) findViewById(R.id.button);
        //sendMessageButton = (Button) findViewById(R.id.sendMessage);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //sendMessageButton = (Button) findViewById(R.id.sendMessage);
        List<String> numbers = MainActivityHelper.readContactsList(getApplicationContext());
        List<String> names = MainActivityHelper.readContactsNameList(getApplicationContext());
        adapter=new MyListAdapter(this,getApplicationContext(), names, numbers);
        listView = (ListView) findViewById(R.id.contactsListView);
        listView.setAdapter(adapter);

        File fileToWrite = new File(getApplicationContext().getFilesDir(), "hardWordFile");
        if (!fileToWrite.exists()) {
            Log.e("MainActivity", "File does not exist, creating one");
            try {
                fileToWrite.createNewFile();
                FileOutputStream fos = new FileOutputStream(fileToWrite, false);
                String str="help";
                fos.write(str.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        EditText et=(EditText)findViewById(R.id.hardwork);
        et.setText(MainActivityHelper.readHardWord(getApplicationContext()));

        findViews();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT);
            }
        });

        /*sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et=(EditText)findViewById(R.id.hardwork);
                et.setVisibility(View.VISIBLE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et=(EditText)findViewById(R.id.hardwork);
                if(et.getText().toString()!=null){
                    Log.e("SaveText",et.getText().toString());
                    MainActivityHelper.updateHarWord(getApplicationContext(), et.getText().toString());
                    String line=MainActivityHelper.readHardWord(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Test is*****************" + line, Toast.LENGTH_LONG).show();
                }
            }
        });

        recordVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

                    if (mSpeechManager == null) {
                        SetSpeechListener();
                    } else if (!mSpeechManager.ismIsListening()) {
                        mSpeechManager.destroy();
                        SetSpeechListener();
                    }

                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET}, 200);
                    recordVoiceBtn.performClick();
                }
            }
        });

        stopRecordVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSpeechManager!=null) {
                    //result_tv.setText("Destroyed");
                    mSpeechManager.destroy();
                    mSpeechManager = null;
                }
            }
        });
        //updateListView(listView);
        MainActivityHelper.updateListView(listView, this, getApplicationContext());
    }

    private void findViews() {
        addButton= (ImageButton ) findViewById(R.id.button);
        recordVoiceBtn = (ImageButton) findViewById(R.id.recordVoiceBtn);
        editBtn=(ImageButton) findViewById(R.id.edit);
        saveBtn=(ImageButton) findViewById(R.id.save);
        //sendMessageButton = (Button) findViewById(R.id.sendMessage);
        listView = (ListView) findViewById(R.id.contactsListView);
        stopRecordVoiceBtn = (ImageButton) findViewById(R.id.stopRecordVoiceBtn);
        //result_tv = (TextView) findViewById(R.id.textView);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case PERMISSION_SEND_SMS:
                if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                }
                break;
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = resultIntent.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                String[] projection2= {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using <code><a href="/reference/android/content/CursorLoader.html">CursorLoader</a></code> to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                Cursor cursor2 = getContentResolver()
                        .query(contactUri, projection2, null, null, null);
                cursor2.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameColumn = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                //ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_SOURCE
               // int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String number = cursor.getString(numberColumn);
                String name = cursor2.getString(nameColumn);
                //String name = cursor.getString(nameColumn);
                //String fial = number+" "+name;
                //mTextMessage.setText(number);
                MainActivityHelper.writeNumberToStorage(number, getApplicationContext());
                MainActivityHelper.writeNameToStorage(name, getApplicationContext());

                //updateListView(listView);
                MainActivityHelper.updateListView(listView, this, getApplicationContext());
            }
            else
            {
               // mTextMessage.setText(resultCode + "======Result");
            }
        }
    }




    private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                Log.e("MainActivity","SetSpeechListener");
                if(results!=null && results.size()>0)
                {
                    Log.e("Size of results",((Integer)results.size()).toString());
                    if(results.size()==1)
                    {
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                        //result_tv.setText(results.get(0));
                    }
                    else {
                        StringBuilder sb = new StringBuilder();

                        if (results.size() > 5) { 
                            results = (ArrayList<String>) results.subList(0, 5);
                        }
                        for (String result : results) {
                            if(result.equalsIgnoreCase(getString(R.string.HotWord)))
                            {
                                Log.e("MainActivity", "HOt Word Detected" + result);
                                //MainActivityHelper.sendMessage(getApplicationContext());
                               // MainActivityHelper.playAlarm(getApplicationContext());

                                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                                {
                                    //         ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS},11);
                                }
                                mFusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                Log.e("MainActivity", "abcd ");

                                                // Got last known location. In some rare situations this can be null.
                                                double latitude = location.getLatitude();
                                                double longitude = location.getLongitude();
                                                MainActivityHelper.sendMessage(getApplicationContext(),latitude, longitude);


                                            }
                                        });

                                MainActivityHelper.playAlarm(getApplicationContext());
                                //sendMessageButton.performClick();

                                //Start recording the Microphone input
                                mSpeechManager.destroy();
                                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                                }
                                MainActivityHelper.record(getApplicationContext());

                            }
                            sb.append(result).append("\n");
                            Log.e("String BUffer",sb.toString());
                        }
                        //result_tv.setText(sb.toString());
                    }
                }
                else
                    ;
                    //result_tv.setText("No results found");
            }
        });
    }

    @Override
    protected void onPause() {
        if(mSpeechManager!=null) {
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
        super.onPause();
    }


}
