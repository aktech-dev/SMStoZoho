package com.example.smstozoho;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;
    SmsMessage[] smsMessages;
    SmsMessage smsMessage;
    Object[] pdus;
    String format;
    int i=0;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();


            pdus = (Object[]) data.get("pdus");
            smsMessages = new SmsMessage[pdus.length];
            format = data.getString("format");


        for(i=0; i<pdus.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            }
            else {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
        }
           if(smsMessages[i-1].getDisplayOriginatingAddress().equalsIgnoreCase("AlahliSMS"))
                //if(smsMessages[i-1].getDisplayOriginatingAddress().equalsIgnoreCase("+923028309601"))
            {
                StringBuffer content = new StringBuffer();
                for (SmsMessage sms : smsMessages) {
                    content.append(sms.getMessageBody());
                }
                String message = content.toString();
                //String message =  smsMessage.getMessageBody() ;
                mListener.messageReceived(message);
            }

                else{ }
        }


    public static void bindListener(MessageListener listener){
        mListener = listener;

    }



}
