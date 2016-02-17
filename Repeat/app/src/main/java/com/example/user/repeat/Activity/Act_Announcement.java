package com.example.user.repeat.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.R;


public class Act_Announcement extends AppCompatActivity {
    private TextView txt_announcementcreatedate, txt_announcemeantcontent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_announcement);
        InitialUI();
        InitialAction();
        getExtras();
    }

    private void InitialUI() {
        txt_announcementcreatedate = (TextView) findViewById(R.id.txt_announcementcreatedate);
        txt_announcemeantcontent = (TextView) findViewById(R.id.txt_announcemeantcontent);
    }

    private void InitialAction() {
//        txt_announcementcreatedate.setText(par.getCreateDate());
//        txt_announcemeantcontent.setText(par.getPushContent());
    }

    private void getExtras() {
        int MPSNo = getIntent().getIntExtra("MPSNo", 0);

    }
}
