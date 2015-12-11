package com.example.user.repeat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Act_Addwindow extends AppCompatActivity {
    Button bt_submit;
    EditText edit_problemcontent;
    Context ctxt = Act_Addwindow.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addwindow);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        edit_problemcontent = (EditText) findViewById(R.id.edit_problemcontent);


        bt_submit.setOnClickListener(onclicklistener);
    }
    private View.OnClickListener onclicklistener = new View.OnClickListener(){

        public void onClick(View v) {
            //{

            //把問題內容上傳至資料庫


            //}


            Toast.makeText(ctxt,"send success",Toast.LENGTH_SHORT).show();
            finish();   //結束這個畫面
        }
    };
}
