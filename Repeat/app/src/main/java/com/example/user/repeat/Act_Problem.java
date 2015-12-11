package com.example.user.repeat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

public class Act_Problem extends AppCompatActivity {
    String createproblemdate,problemdescription,managercontent,problemstatus;
    TextView txt_problem_createdate,txt_customercontent,txt_managercontent;
    RatingBar rb_score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_problem);
        Bundle bundle = getIntent().getExtras();
        createproblemdate=bundle.getString("createproblemdate");
        problemdescription=bundle.getString("problemdescription");
        managercontent=bundle.getString("managercontent");
        problemstatus = bundle.getString("problemstatus");


        rb_score = (RatingBar) findViewById(R.id.rb_score);
        txt_problem_createdate = (TextView) findViewById(R.id.txt_problem_createdate);
        txt_customercontent = (TextView) findViewById(R.id.txt_customercontent);
        txt_managercontent = (TextView) findViewById(R.id.txt_managercontent);

        txt_problem_createdate.setText(createproblemdate);
        txt_customercontent.setText(problemdescription);
        txt_managercontent.setText(managercontent);
        if (problemstatus.equals("Complete")){
            rb_score.setVisibility(View.VISIBLE);
        }else{
            rb_score.setVisibility(View.INVISIBLE);
        }
    }
}
