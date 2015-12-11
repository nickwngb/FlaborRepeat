package com.example.user.repeat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.repeat.Adapter.ProblemList;
import com.example.user.repeat.Other.ProblemRecord;

import java.util.ArrayList;
import java.util.List;

public class Act_MainScreen extends AppCompatActivity {
    Button bt_repeat;
    ListView list_reaprethistory;
    Context ctxt = Act_MainScreen.this;
    List<ProblemRecord> problemlist = new ArrayList<ProblemRecord>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
        bt_repeat.setOnClickListener(onclicklistener);
        list_reaprethistory.setOnItemClickListener(onitemclicklistener);
        catchdata();        //假設內容
        ProblemList();      //listadapter
    }

    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(ctxt, Act_Addwindow.class);
            startActivity(i);
        }
    };
    private AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //按一次Item事件
            Intent i = new Intent();
            Bundle b = new Bundle();
            i.setClass(ctxt, Act_Problem.class);
            b.putString("createproblemdate", problemlist.get(position).createproblemdate);
            b.putString("problemdescription", "Me:\n" + problemlist.get(position).problemdescription);
            b.putString("problemstatus",problemlist.get(position).problemstatus);

            if(position == 0){
                b.putString("managercontent","Manager:\nYou can buy something to eat");
            }else{
                b.putString("managercontent","Manager:");
            }


            i.putExtras(b);
            startActivity(i);
        }
    };
    public void onBackPressed() {
        new AlertDialog.Builder(ctxt)
                .setTitle("Exit?")
                .setMessage("Are you sure to Exit?")
                .setPositiveButton("No", null
                ).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                Act_MainScreen.super.onBackPressed();
            }
        }).show();

    }

    private void catchdata() {
        ProblemRecord pr = new ProblemRecord();
        pr.createproblemdate = "2015-12-25";
        pr.problemdescription = "I want to eat some food";
        pr.problemstatus = "Complete";

        problemlist.add(pr);

        pr = new ProblemRecord();
        pr.createproblemdate = "2015-12-24";
        pr.problemdescription = "Tell me how to go to supermarket??";
        pr.problemstatus = "Processing";

        problemlist.add(pr);

        pr = new ProblemRecord();
        pr.createproblemdate = "2015-12-23";
        pr.problemdescription = "Give me a pillow";
        pr.problemstatus = "Untreated";

        problemlist.add(pr);
    }

    private void ProblemList() { //設定list的樣式
        ProblemList adapter = new ProblemList(this, problemlist);
        list_reaprethistory.setAdapter(adapter);
    }
}
