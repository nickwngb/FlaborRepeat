package com.example.user.repeat.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;


public class Act_Problem extends Activity {
    //
    private Context ctxt = Act_Problem.this;
    private Resources res;
    private ProblemRecord pr;
    // UI
    private TextView txt_problem_createdate, txt_customercontent, txt_managercontent, txt_problem_repeatedate;
    private RatingBar rb_score;
    // other


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_problem);

        getExtras();
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void InitialSomething() {
        res = getResources();
    }

    private void InitialUI() {
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        txt_problem_createdate = (TextView) findViewById(R.id.txt_problem_createdate);
        txt_customercontent = (TextView) findViewById(R.id.txt_customercontent);
        txt_problem_repeatedate = (TextView) findViewById(R.id.txt_problem_repeatedate);
        txt_managercontent = (TextView) findViewById(R.id.txt_managercontent);
    }

    private void InitialAction() {
        txt_problem_createdate.setText(pr.getCreateProblemDate());
        txt_customercontent.setText(pr.getProblemDescription());
        txt_problem_repeatedate.setText(pr.getResponseDate());
        txt_managercontent.setText(pr.getResponseResult());

        if (pr.getProblemStatus().equals(Code.Completed)) {
            rb_score.setVisibility(View.VISIBLE);
            Uti.t(ctxt,"give your start !");
        }
    }

    private void getExtras() {
        Bundle b = getIntent().getExtras();
        pr = (ProblemRecord) b.getSerializable("ProblemRecord");
    }
}
