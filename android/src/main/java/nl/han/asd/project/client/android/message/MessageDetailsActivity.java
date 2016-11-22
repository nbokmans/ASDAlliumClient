package nl.han.asd.project.client.android.message;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import nl.han.asd.project.client.android.R;
import nl.han.asd.project.client.android.utility.BaseActivity;
import nl.han.asd.project.client.commonclient.message.Message;

@EActivity(R.layout.activity_message_details)
public class MessageDetailsActivity extends BaseActivity {
    @ViewById
    ListView message_path_list;

    @ViewById
    TextView message_text;

    @Extra("MESSAGE_PATH")
    ArrayList<String> messagePath;

    @Extra("MESSAGE_TEXT")
    String messageText;

    @AfterViews
    public void init(){
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Message Details");
        }
        message_text.setText(messageText);
        ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, messagePath);
        message_path_list.setAdapter(adapter);
    }
}
