package com.example.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smsapp.R;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnSend2;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editText);
        etContent = findViewById(R.id.editText2);
        btnSend = findViewById(R.id.button);
        btnSend2 = findViewById(R.id.button2);

        checkPermission();

        br = new MessageReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString();
                String[] to = etTo.getText().toString().split(", ");
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < to.length; i++){
                    smsManager.sendTextMessage(to[i], null, content, null, null);
                }
            }
        });

        btnSend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = etTo.getText().toString();
                String msg = etContent.getText().toString();
                Uri uriSms = Uri.parse("smsto:+"+number);
                Intent intentSMS = new Intent(Intent.ACTION_SENDTO, uriSms);
                intentSMS.putExtra("sms_body", msg);
                startActivity(intentSMS);
            }
        });

    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
