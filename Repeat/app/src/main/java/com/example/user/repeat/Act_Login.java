package com.example.user.repeat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Act_Login extends AppCompatActivity {
    Button bt_login;
    EditText edit_loginphone;
    String phone;
    Context ctxt = Act_Login.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        bt_login = (Button) findViewById(R.id.bt_login);
        edit_loginphone = (EditText) findViewById(R.id.edit_loginphone);
        bt_login.setOnClickListener(onclicklistener);
    }
    private View.OnClickListener onclicklistener = new View.OnClickListener(){

        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(ctxt,Act_MainScreen.class);
            phone = edit_loginphone.getText().toString();
            startActivity(i);
            finish();
        }
    };
}
