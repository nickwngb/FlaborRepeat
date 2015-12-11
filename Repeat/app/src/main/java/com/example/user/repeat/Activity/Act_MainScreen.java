package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.repeat.Adapter.ProblemListAdapter;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.R;

import java.util.ArrayList;
import java.util.List;

public class Act_MainScreen extends Activity {
    //
    private Context ctxt = Act_MainScreen.this;
    // UI
    private Button bt_repeat;
    private ListView list_reaprethistory;
    // Other
    private List<ProblemRecord> problemlist;
    private ProblemListAdapter pl_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
    }


    private void LoadingAllProblem() {
//        if (Net.isNetWork(ctxt)) {
//
//        } else {
//
//        }
    }
    class LoadingAllProblemTask extends AsyncTask<String,String,String>{

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    private void InitialSomething() {
        problemlist = new ArrayList<ProblemRecord>();
        catchdata(); //假設內容
    }

    private void InitialUI() {
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
    }

    private void InitialAction() {
        bt_repeat.setOnClickListener(onclicklistener);
        list_reaprethistory.setOnItemClickListener(onitemclicklistener);
        pl_adapter = new ProblemListAdapter(this, problemlist);
        list_reaprethistory.setAdapter(pl_adapter);
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
            b.putString("problemstatus", problemlist.get(position).problemstatus);

            if (position == 0) {
                b.putString("managercontent", "Manager:\nYou can buy something to eat");
            } else {
                b.putString("managercontent", "Manager:");
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
}
