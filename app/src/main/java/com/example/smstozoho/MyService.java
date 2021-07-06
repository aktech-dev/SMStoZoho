package com.example.smstozoho;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.Service;
import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MyService extends Service implements MessageListener,ActionListener,CancelListener{


    private static  int NOTIF_ID = 1;
    private static  final String NOTIF_CHANNEL_ID = "Channel_Id";
    private static   String CHANNEL_ID = "Chanel_Id";
    String body,SQLiteDataBaseQueryHolder;
    WifiManager wifiManager;
    ArrayList<TableData> history;
    HashMap<Integer,String> memory;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static int itroter=0;
    public static SQLiteDatabase sqLiteDatabaseObj;
    Cursor resultSet;
    NotificationManagerCompat notificationManager;
    NotificationManagerCompat notificationManager2;
    DateFormat dateFormat;
    ContentValues cv;

    @Override
    public void onCreate() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        history = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager2 = NotificationManagerCompat.from(getApplicationContext());
        memory = new HashMap<Integer, String>();
        sharedPreferences = getSharedPreferences("ab",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        cv = new ContentValues();
        dateFormat = new SimpleDateFormat("HH-mm-ss - dd-MM-YYYY");

    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MessageReceiver.bindListener(this);
        startforeground("BACKGROUND SERVICE STARTED");
        super.onStartCommand(intent, flags, startId);
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);
        MmsReceiver.bindListener(this);
        CancelReceiver.bindListener(this);
        return START_STICKY;
    }


    public void startforeground( String abc ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.pp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(abc)
                .setContentIntent(pendingIntent)
                .build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void messageReceived(String message) {
        body = message;
        if(body.contains("307*232"))
        {
        itroter++;
        String date = dateFormat.format(new Date());
        memory.put(itroter,body);
        smsNotify(message,date);
        storemsg(body,date);}
        else
            startforeground("other account message");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void smsNotify(String ab, String date){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "2";
            int importance1 = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID+itroter, name, importance1);
            notificationManager2.createNotificationChannel(channel1);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentAction = new Intent(this,MmsReceiver.class);
        intentAction.putExtra("id",String.valueOf(itroter));
        intentAction.putExtra("msg",ab);
        intentAction.putExtra("time",date);

        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(this,itroter,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentCancel = new Intent(this,CancelReceiver.class);
        intentCancel.putExtra("id",String.valueOf(itroter));
        intentCancel.putExtra("msg",ab);
        intentCancel.putExtra("time",date);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this,itroter,intentCancel,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigStyle =
                new NotificationCompat.BigTextStyle();
        bigStyle.bigText(ab);

        Notification notify = new NotificationCompat.Builder(this,CHANNEL_ID+itroter)
                .setOngoing(true)
                .setSmallIcon(R.drawable.pp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("New Message From Alahli")
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(ab))
                .setContentIntent(actionPendingIntent)
                .setContentText(ab)
                .addAction(R.drawable.ic_baseline_done_outline_24 , "Send",actionPendingIntent)
                .addAction(R.drawable.ic_baseline_close_24 , "Cancel",cancelPendingIntent)
                .build();
        notificationManager2.notify(NOTIF_ID+itroter,notify);
    }


    public void storemsg(String stored_msg,String date)
    {
        String permission="Denied";
        String status ="Not_Sent";
        sqLiteDatabaseObj = openOrCreateDatabase("TransactionsDataBase", Context.MODE_PRIVATE, null);
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS data(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "message VARCHAR,amount VARCHAR,type VARCHAR,description VARCHAR,time VARCHAR,permission  VARCHAR, status VARCHAR);");
        SQLiteDataBaseQueryHolder = "INSERT INTO data(message,amount,type,description,time,permission,status) " +
                "VALUES('"+stored_msg+"',  '"+null+"', '"+null+"','"+null+"','"+date+"','"+permission+"', '"+status+"');";
        sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);
    }


    @Override
    public void actionReceived(String id , String msgrec, String time) {
        Intent popIntent = new Intent(getApplicationContext(),PopupActivity.class);
        popIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        popIntent.putExtra("id",id);
        popIntent.putExtra("msg",msgrec);
        popIntent.putExtra("time",time);
        startActivity(popIntent);
    }

    @Override
    public void actionCancel(String id,String msg, String time) {
        int indx = Integer.parseInt(id);
        notificationManager2.deleteNotificationChannel("Chanel_Id"+indx);
        startforeground("Transaction Cancelled");
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    startforeground("Internet Connected Successfully!");
                    //sendSmsAfterConn();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    startforeground("Internet Disconnected");
                    break;
            }
        }
    };



    public void sendSmsAfterConn(){

        resultSet = sqLiteDatabaseObj.rawQuery("Select * from data ",null);
        resultSet.moveToFirst();
        while(resultSet.isAfterLast() == false){

//            TableData tableData = new TableData(resultSet.getString(resultSet.getColumnIndex("id")),resultSet.getString(resultSet.getColumnIndex("message")),resultSet.getString(resultSet.getColumnIndex("time")),
//                    resultSet.getString(resultSet.getColumnIndex("permission")),resultSet.getString(resultSet.getColumnIndex("status")));
//            history.add(tableData);
            resultSet.moveToNext();
        }
        String amount = resultSet.getString(0);
        String status = resultSet.getString(1);
    }

    public static boolean isNetworkConnected(Context  context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        } else return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

//    public void sendSMS(String msg) {
//        try {
//            String phoneNo ="+923167453920";
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            Toast.makeText(getApplicationContext(), "Message Sent",
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }
//    }
//
//    public void openWhatsApp(String tex){
//        try {
//            String text = tex;// Replace with your message.
//
//            String toNumber = "923167453920"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
//            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
//            boolean connected = false;
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
//            startActivity(intent);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

    }
    public int counter=0;
    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
        startService(new Intent(this,MyService.class));
        super.onTaskRemoved(rootIntent);

    }

    public void startMyService(){

//        Intent MyIntentService = new Intent(getApplicationContext(), MyIntentService.class);
//        getApplicationContext().startService(MyIntentService);
        startService(new Intent(this,MyService.class));

    }


}

//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            while(working.get()) {
//                // put your socket-code here
//                startForeground("BACKGROUND SERVICE STARTED");
//
//            }
//        }
//    };



//class NewThreade extends Thread{
//    @Override
//    public void run() {
//        MyService myService = new MyService();
//        myService.startMyService();
//    }
//}






