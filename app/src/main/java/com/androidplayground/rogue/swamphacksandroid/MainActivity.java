package com.androidplayground.rogue.swamphacksandroid;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidplayground.rogue.helper.MainActivityHelper;
import com.androidplayground.rogue.helper.SpeechRecognizerManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView result_tv;
    private Button addButton;
    private Button sendMessageButton;
    private ListView listView;
    private Button recordVoiceBtn;
    private Button stopRecordVoiceBtn;
    private SpeechRecognizerManager mSpeechManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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
    };
    static final private int PICK_CONTACT = 1;
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton= (Button) findViewById(R.id.button);
        sendMessageButton = (Button) findViewById(R.id.sendMessage);
        List<String> numbers = MainActivityHelper.readContactsList(getApplicationContext());
        List<String> names = MainActivityHelper.readContactsNameList(getApplicationContext());
        adapter=new MyListAdapter(this, names, numbers);
        listView = (ListView) findViewById(R.id.contactsListView);
        listView.setAdapter(adapter);
        findViews();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 200);
                }
                MainActivityHelper.sendMessage(getApplicationContext());
                MainActivityHelper.playAlarm(getApplicationContext());

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
                    result_tv.setText("Destroyed");
                    mSpeechManager.destroy();
                    mSpeechManager = null;
                }
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        updateListView(listView);
    }

    private void findViews() {
        addButton= (Button) findViewById(R.id.button);
        recordVoiceBtn = (Button) findViewById(R.id.recordVoiceBtn);
        sendMessageButton = (Button) findViewById(R.id.sendMessage);
        listView = (ListView) findViewById(R.id.contactsListView);
        stopRecordVoiceBtn = (Button) findViewById(R.id.stopRecordVoiceBtn);
        result_tv = (TextView) findViewById(R.id.textView);
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
                updateListView(listView);
            }
            else
            {
               // mTextMessage.setText(resultCode + "======Result");
            }
        }
    }

    public void updateListView(ListView listView) {
        List<String> contactsList = MainActivityHelper.readContactsList(getApplicationContext());
        List<String> contactsNameList = MainActivityHelper.readContactsNameList(getApplicationContext());
        if(contactsList!=null && contactsNameList!=null && contactsNameList.size() > 0 && contactsList.size() > 0) {
            //ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactsList);
            MyListAdapter adapter=new MyListAdapter(MainActivity.this , contactsNameList, contactsList);
            listView.setAdapter(adapter);
        }
        else
        {
            Log.e("MainActivity","The contacts list is NULL");
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
                        result_tv.setText(results.get(0));
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
                                //MainActivityHelper.playAlarm(getApplicationContext());
                                sendMessageButton.performClick();

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
                        result_tv.setText(sb.toString());
                    }
                }
                else
                    ;
                    result_tv.setText("No results found");
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
