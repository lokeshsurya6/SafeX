package com.androidplayground.rogue.swamphacksandroid;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplayground.rogue.helper.MainActivityHelper;


import java.util.List;

public class MyListAdapter extends ArrayAdapter<String> {


    private final MainActivity activity;
    private final Context context;
    private final List<String> maintitle;
    private final List<String> subtitle;

    public MyListAdapter(MainActivity activity, Context context, List<String> maintitle, List<String> subtitle) {
        super(activity, R.layout.mylist, maintitle);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;

        this.activity = activity;

    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=activity.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        final TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        Button del= (Button) rowView.findViewById(R.id.delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("MyListAdapter","");
                Toast.makeText(context, maintitle.get(position)+ " " + subtitle.get(position), Toast.LENGTH_SHORT).show();
                MainActivityHelper.delNumberFunction(subtitle.get(position), context);
                MainActivityHelper.delNameFunction(maintitle.get(position), context);
                MainActivityHelper.updateListView(activity.getListView(), activity, context );
            }
        });
        titleText.setText(maintitle.get(position));
        subtitleText.setText(subtitle.get(position));

        return rowView;

    };
}
