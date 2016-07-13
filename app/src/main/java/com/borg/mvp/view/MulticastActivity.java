package com.borg.mvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.model.Network.MulticastClient;
import com.borg.mvp.model.Network.MulticastServer;

public class MulticastActivity extends AppCompatActivity implements View.OnClickListener{

    MulticastServer server;
    MulticastClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multicast);
        findViewById(R.id.btnStartSend).setOnClickListener(this);
        findViewById(R.id.btnStopSend).setOnClickListener(this);
        findViewById(R.id.btnStartReceive).setOnClickListener(this);
        findViewById(R.id.btnStopReceive).setOnClickListener(this);

        try{
            server = new MulticastServer(this);
            client = new MulticastClient(this);
        }catch (Exception e){}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStartSend:
                server.startSend();
                break;
            case R.id.btnStopSend:
                server.stopSend();
                break;
            case R.id.btnStartReceive:
                client.startReceive();
                break;
            case R.id.btnStopReceive:
                client.stopReceive();
                break;
        }
    }
}
