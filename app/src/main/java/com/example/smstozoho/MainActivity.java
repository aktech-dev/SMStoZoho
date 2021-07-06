 package com.example.smstozoho;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.smstozoho.MyService.sqLiteDatabaseObj;


 public class MainActivity extends AppCompatActivity{
    TextView textView;
    ImageButton imageButton;
    Cursor res;
    ArrayList<TableData> histroy1;
    RecyclerView recyclerView;
    TableDataAdapter tableDataAdapter;
    Button showhis;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    Toast.makeText(MainActivity.this, "Permission Granted to read your SMS", Toast.LENGTH_SHORT).show();
                }
                else {// permission denied, boo! Disable the
                    Toast.makeText(MainActivity.this, "Permission denied to read your SMS", Toast.LENGTH_SHORT).show(); }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView  = findViewById(R.id.textView);
        imageButton = findViewById(R.id.imageView2);
        recyclerView = findViewById(R.id.recycle);
        showhis = findViewById(R.id.button);


        LinearLayoutManager layoutInflater = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutInflater);

        sqLiteDatabaseObj = openOrCreateDatabase("TransactionsDataBase", Context.MODE_PRIVATE, null);
        //sqLiteDatabaseObj.execSQL("DROP TABLE IF EXISTS data");

        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);

        histroy1 = new ArrayList<>();

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_SMS}, 1);

        showhis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                histroy1.clear();
                res = sqLiteDatabaseObj.rawQuery("Select * from data order by id DESC",null);
                res.moveToFirst();
                while(res.isAfterLast() == false){
                    TableData tableData = new TableData(res.getString(res.getColumnIndex("id")),res.getString(res.getColumnIndex("message")),
                            res.getString(res.getColumnIndex("amount")),res.getString(res.getColumnIndex("type")),
                            res.getString(res.getColumnIndex("description")), res.getString(res.getColumnIndex("time")),
                            res.getString(res.getColumnIndex("permission")), res.getString(res.getColumnIndex("status")));

                    histroy1.add(tableData);
                    res.moveToNext();
                }
                tableDataAdapter = new TableDataAdapter(getApplicationContext(),histroy1);
                recyclerView.setAdapter(tableDataAdapter);
            }
        });




//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_SMS}, 1);
//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS}, 1);
//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_WAP_PUSH}, 1);
//        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECEIVE_MMS}, 1);

    }

     @Override
     public boolean onKeyLongPress(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
             Log.d("Test", "Long press!");
             //flag = false;
             //flag2 = true;
             return true;
         }
         return super.onKeyLongPress(keyCode, event);
     }

    @Override
    protected void onPause() {
        Intent MyIntentService = new Intent(getApplicationContext(), MyIntentService.class);
        getApplicationContext().startService(MyIntentService);
//      NewThread newThread = new NewThread();
//      newThread.start();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        startService(new Intent(this,MyService.class));
        super.onStop();
    }

    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra) {
                case WifiManager.WIFI_STATE_ENABLED:
                    imageButton.setBackgroundResource(R.drawable.circle);
                    //sendSmsAfterConn();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    imageButton.setBackgroundResource(R.drawable.circle_off);
                    break;
            }
        }
    };

//    private void getcurency() {
//        Call<Post> call = jsonPlaceHolder.getPosts(organization_id);
//        call.enqueue(new Callback<Post>() {
//            @Override
//            public void onResponse(Call<Post> call, Response<Post> response) {
//                if(!response.isSuccessful()){
//
//                    textView.setText("code : " + response.code());
//                    textview1.setText(response.errorBody().toString());
//
//                    return;
//                }
//                Post posts = response.body();
//
//
//                for(Cruncy cruncy:posts.getCurrencies()) {
//                    String content = "";
//                    content += "id" + cruncy.getCurrency_id() + "\n";
//                    content += "code" + cruncy.getCurrency_code() + "\n";
//
//
//                    textView.append(content);
//
//                }
//
//
//
//
//
//            }
//
//            @Override
//            public void onFailure(Call<Post>call, Throwable t) {
//                textView.setText(t.getMessage());
//                textview1.setText(t.getMessage());
//            }
//        });
//    }

}


