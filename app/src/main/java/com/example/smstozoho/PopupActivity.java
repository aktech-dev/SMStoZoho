package com.example.smstozoho;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.smstozoho.MyService.sqLiteDatabaseObj;

public class PopupActivity extends AppCompatActivity {

    private TextView popmesg,skip;
    private Spinner spinner,spinner2;
    private EditText money,desc;
    private Button send;
    private String find,str,accesstokencontent,account_id,notifid,msgrec,time;
    private int organization_id = 731606685;
    private NotificationManagerCompat notificationManager;
    private MyService myService;
    int id ;
    private ProgressDialog progressDialog;
    private ArrayList<String> chartofaccountsArrayList;
    private HashMap<String,String> myHashMap;
    private String granted="Granted";
    private String sent="Sent";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        popmesg = findViewById(R.id.popmsg);
        spinner = findViewById(R.id.spinner);
        money = findViewById(R.id.editTextTextPersonName);
        desc = findViewById(R.id.editTextNumber);
        skip = findViewById(R.id.textView3);
        send = findViewById(R.id.button2);
        spinner2 = findViewById(R.id.spinner2);
        chartofaccountsArrayList = new ArrayList<>();
        myHashMap = new HashMap<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading  \n Please Wait...");
        progressDialog.setCancelable(false);

        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        sqLiteDatabaseObj = openOrCreateDatabase("TransactionsDataBase", Context.MODE_PRIVATE, null);
        myService = new MyService();

        Intent intent = this.getIntent();

        notifid = intent.getStringExtra("id");
        msgrec = intent.getStringExtra("msg");
        time = intent.getStringExtra("time");


        id = Integer.parseInt(notifid);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);




        popmesg.setText(msgrec);
        money.setText(smsFilter(msgrec));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if(selectedItem.equalsIgnoreCase("Expense")){
                    progressDialog.show();
                    getAccessToken((float) 0.0,0,"","","");
                }
                else
                    spinner2.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You Skip this Transaction", Toast.LENGTH_SHORT).show();
                if(id != 50){
                notificationManager.deleteNotificationChannel("Chanel_Id"+id);}
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String moneyCheck = money.getText().toString();
                String type = spinner.getSelectedItem().toString();
                String description = desc.getText().toString();
                if(TextUtils.isEmpty(moneyCheck)){money.setError("Enter amount"); progressDialog.dismiss();return;}
                if(type.equals("Please select transaction type")){
                    Toast.makeText(PopupActivity.this, "Please select transaction type", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }
                if(TextUtils.isEmpty(description)){description="Nill";}
                if(type.equalsIgnoreCase("Expense")){
                    String account_name = spinner2.getSelectedItem().toString();
                    account_id = myHashMap.get(account_name);
                }

                float amount = Float.parseFloat(moneyCheck);
                money.setText(moneyCheck);
                desc.setText(description);
                getAccessToken(amount,id,time,type,description);

            }
        });

    }


    private void getAccessToken(float amount, int notifid,String time, String type,String description){

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://accounts.zoho.com/oauth/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Tokengen tokengen = retrofit2.create(Tokengen.class);

        Call<Accesstoken> call = tokengen.getAccessToken("1000.3ff8f8d71d04214896d8fecb69a13420.46e85db9b3dd9505d514b2d0f3a8f2e4" ,"1000.R68PP7I30OJSK6I4RU644QA4VL6JCJ",
                "0908cd7c90bed3982a9c8642263c776de43c40f0f6","http://www.zoho.com/books","refresh_token");
        call.enqueue(new Callback<Accesstoken>() {
            @Override
            public void onResponse(Call<Accesstoken> call, Response<Accesstoken> response) {
                if(!response.isSuccessful()){

                    Toast.makeText(PopupActivity.this, response.code()+"Failed storing in database", Toast.LENGTH_SHORT).show();
                    notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                    String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                    sqLiteDatabaseObj.execSQL(query);
                    progressDialog.dismiss();
                    finish();
                    return;
                }
                Accesstoken tokenResponse = response.body();
                accesstokencontent = "";
                accesstokencontent += "Zoho-oauthtoken " + tokenResponse.getAccess_token();
                if(type.equalsIgnoreCase("deposit")){
                createTransaction(amount,type,notifid,time,description);}
                if(type.equalsIgnoreCase("Expense")){
                    createExpense(amount,type,notifid,time,description);
                }
                else
                    getExpenseList();
            }

            @Override
            public void onFailure(Call<Accesstoken> call, Throwable t) {
                Toast.makeText(PopupActivity.this, "Some problem Occurred , saved in database the error is "+t.getMessage(), Toast.LENGTH_SHORT).show();
                notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                sqLiteDatabaseObj.execSQL(query);
                progressDialog.dismiss();
                finish();
            }
        });
    }


    private void createTransaction(float amount, String type, int notifid,String time,String description) {

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl("https://books.zoho.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String drawings ="2498853000000012001";
        String ncb ="2498853000000000361";

        Transaction transaction= new Transaction("2498853000000012001",
                "2498853000000000361", type, amount,description);

        GetTrans getTran = retrofit3.create(GetTrans.class);
        Call<Transaction> call3=getTran.postTrans(accesstokencontent,organization_id,transaction);

        call3.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(PopupActivity.this, "Saving to Database Response is not SuccessFull "+response.code(), Toast.LENGTH_SHORT).show();
                    notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                    String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                    sqLiteDatabaseObj.execSQL(query);
                    progressDialog.dismiss();
                    finish();
                    return;
                }
                Toast.makeText(PopupActivity.this, "Transaction Created SuccessFully in ZOHO", Toast.LENGTH_SHORT).show();
                notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                String queryy = "UPDATE data SET status = "+"'"+sent+"' "+"WHERE time = "+"'"+time+"'";
                String query1 = "UPDATE data SET amount = "+"'"+amount+"' "+"WHERE time = "+"'"+time+"'";
                String query2 = "UPDATE data SET type = "+"'"+type+"' "+"WHERE time = "+"'"+time+"'";
                String query3 = "UPDATE data SET description = "+"'"+description+"' "+"WHERE time = "+"'"+time+"'";
                sqLiteDatabaseObj.execSQL(query);
                sqLiteDatabaseObj.execSQL(queryy);
                sqLiteDatabaseObj.execSQL(query1);
                sqLiteDatabaseObj.execSQL(query2);
                sqLiteDatabaseObj.execSQL(query3);
                progressDialog.dismiss();
                finish();

            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText(PopupActivity.this, "Saving to Database Failed : Due to... " + t.getMessage(), Toast.LENGTH_SHORT).show();
                notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                sqLiteDatabaseObj.execSQL(query);
                progressDialog.dismiss();
                finish();
            }
        });

    }

    private void getExpenseList(){

        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl("https://books.zoho.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetExpnseList getExpnseList = retrofit3.create(GetExpnseList.class);

        Call<ExpenseList> call = getExpnseList.getExpenseLists(accesstokencontent , organization_id, "AccountType.Expense");

        call.enqueue(new Callback<ExpenseList>() {
            @Override
            public void onResponse(Call<ExpenseList> call, Response<ExpenseList> response) {
                if(!response.isSuccessful()){

                    Toast.makeText(PopupActivity.this, response.code()+"Failed response", Toast.LENGTH_SHORT).show();
                    return;
                }
                ExpenseList expenseListResponse = response.body();

               for(Chartofaccounts i:expenseListResponse.getChartofaccounts()){

                   chartofaccountsArrayList.add(i.getAccount_name());
                   myHashMap.put(i.getAccount_name(),i.getAccount_id());
               }

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, chartofaccountsArrayList);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(adapter2);
                spinner2.setSelection(0);
                spinner2.setVisibility(View.VISIBLE);
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<ExpenseList> call, Throwable t) {
                Toast.makeText(PopupActivity.this, "Some problem Occurred  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createExpense(float amount, String type, int notifid, String time,String description) {


        Retrofit retrofit4 = new Retrofit.Builder()
                .baseUrl("https://books.zoho.com/api/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String t =String.valueOf(SystemClock.uptimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        String date = simpleDateFormat.format(new Date());


        PostExpenseBody postExpenseBody = new PostExpenseBody(account_id,date,amount,description,description);

        PostExpense postExpense = retrofit4.create(PostExpense.class);
        Call<PostExpenseBody> call3=postExpense.postExpense(accesstokencontent,organization_id,postExpenseBody);

        call3.enqueue(new Callback<PostExpenseBody>() {
            @Override
            public void onResponse(Call<PostExpenseBody> call, Response<PostExpenseBody> response) {

                if(!response.isSuccessful()){
                    Log.e("resTAG", "onResponse: "+response.message()+response.code());
                    Toast.makeText(PopupActivity.this, "Saving to Database Response is not SuccessFull "+response.code(), Toast.LENGTH_SHORT).show();
                    notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                    String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                    sqLiteDatabaseObj.execSQL(query);
                    progressDialog.dismiss();
                    finish();
                    return;
                }
                Toast.makeText(PopupActivity.this, "Transaction Created SuccessFully in ZOHO", Toast.LENGTH_SHORT).show();
                notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                String queryy = "UPDATE data SET status = "+"'"+sent+"' "+"WHERE time = "+"'"+time+"'";
                String query1 = "UPDATE data SET amount = "+"'"+amount+"' "+"WHERE time = "+"'"+time+"'";
                String query2 = "UPDATE data SET type = "+"'"+type+"' "+"WHERE time = "+"'"+time+"'";
                String query3 = "UPDATE data SET description = "+"'"+description+"' "+"WHERE time = "+"'"+time+"'";
                sqLiteDatabaseObj.execSQL(query);
                sqLiteDatabaseObj.execSQL(queryy);
                sqLiteDatabaseObj.execSQL(query1);
                sqLiteDatabaseObj.execSQL(query2);
                sqLiteDatabaseObj.execSQL(query3);
                progressDialog.dismiss();
                finish();

            }

            @Override
            public void onFailure(Call<PostExpenseBody> call, Throwable t) {
                Toast.makeText(PopupActivity.this, "Saving to Database Failed : Due to... " + t.getMessage(), Toast.LENGTH_SHORT).show();
                notificationManager.deleteNotificationChannel("Chanel_Id"+notifid);
                String query = "UPDATE data SET permission = "+"'"+granted+"' "+"WHERE time = "+"'"+time+"'";
                sqLiteDatabaseObj.execSQL(query);
                progressDialog.dismiss();
                finish();
            }
        });


    }

    public String smsFilter(String body){
        if (body == null) { }
        else
        {
            boolean b = body.contains("حساب 307*232");
            String[] msg = body.split(" ");
            for (int i = 0; i < msg.length; i++)
            {
                if (msg[i].contains("مبلغ")  && b)
                {
                    find = msg[i + 1];
                    break;
                }
                else
                    find = "0";
            }

            if (find != "0")
            {
                str = find.replaceAll("[^0-9]", "");


            }
            else {
                Toast.makeText(this, "msg not contain sar value Please Enter by YourSelf", Toast.LENGTH_SHORT).show();
            }
        }

        return str;
    }

            @Override
    public void onBackPressed() {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                notificationManager.deleteNotificationChannel("Chanel_Id"+id);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning!")
                        .setMessage("Are you sure You Want to Quit the Transaction \n You will lose Your Progress")
                        .setCancelable(true)
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener);
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
    }
}