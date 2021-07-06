package com.example.smstozoho;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

public class MyIntentService  extends IntentService {


    public MyIntentService (){
        super("Nyinetentsrcvice");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startService(new Intent(this,MyService.class));
//        Thread t = new Thread(){
//
//            @Override
//            public void run() {
//                createThread();
//            }
//        };
//        t.start();


    }



    public void createThread(){


    }

}
