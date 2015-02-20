package com.example.imgresize;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import com.example.imgresize.data.model.data.FragmentA;
import com.example.imgresize.data.model.data.FragmentB;

public class MainActivity extends ActionBarActivity implements FragmentA.Communicator {

    FragmentA fragmentA;
    FragmentB fragmentB;
    FragmentManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getSupportFragmentManager();
        fragmentA = (FragmentA) manager.findFragmentById(R.id.fragment);
        //fragmentA.setCommunicator(this);
    }


    @Override
    public void respond(Bitmap image) {
        fragmentB = (FragmentB) manager.findFragmentById(R.id.fragment2);
        fragmentB.openImage(image);
    }






}