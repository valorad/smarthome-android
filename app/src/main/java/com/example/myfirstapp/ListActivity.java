package com.example.myfirstapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        setTitle("Bluetooth devices");

        ListView bluetooth_devices = findViewById(R.id.bluetooth_list);


//        String[] devices = {"iPhone", "Anadroid"};
//
//        ListAdapter listAdapter = new ListAdapter() {
//            @Override
//            public boolean areAllItemsEnabled() {
//                return false;
//            }
//
//            @Override
//            public boolean isEnabled(int position) {
//                return false;
//            }
//
//            @Override
//            public void registerDataSetObserver(DataSetObserver observer) {
//
//            }


       // bluetooth_devices.setAdapter(listAdapter);


    }
}
