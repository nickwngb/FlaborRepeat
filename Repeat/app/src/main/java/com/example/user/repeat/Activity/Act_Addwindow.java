package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.repeat.Asyn.AddProblem;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.FreeDialog;
import com.example.user.repeat.Other.Hardware;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.Other.Vaild;
import com.example.user.repeat.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Act_Addwindow extends Activity {
    //
    private Context ctxt = Act_Addwindow.this;
    private Resources res;
    private HttpConnection conn;
    private User user;
    // UI
    private Button bt_submit;
    private EditText edit_problemcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addproblem);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void AddProblem() {
        if (Net.isNetWork(ctxt)) {
            AddProblem task = new AddProblem(conn, new AddProblem.OnAddProblemListener() {
                final ProgressDialog fd = FreeDialog.getProgressDialog(ctxt, "Loading...");

                public void finish(Integer result) {
                    fd.dismiss();
                    switch (result) {
                        case Code.Success:
                            Intent i = new Intent();
                            i.putExtra("SUCCESS", true);
                            setResult(RESULT_OK, i);
                            finishActivity();
                            break;
                        case Code.ResultEmpty:
                            Uti.t(ctxt, "Add Fail");
                            break;
                        case Code.ConnectTimeOut:
                            Uti.t(ctxt, "Connection Fail");
                            break;
                        default:
                            Uti.t(ctxt, "Error : " + result);
                    }
                }
            });
            String content = edit_problemcontent.getText().toString();
            task.execute(user.getFLaborNo(), user.getCustomerNo(), content, getCurrentDateTime(), Code.Untreated, user.getChineseName(), Code.Flabor);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
        user = User.getUser();
    }

    private void InitialUI() {
        bt_submit = (Button) findViewById(R.id.bt_submit);
        edit_problemcontent = (EditText) findViewById(R.id.edit_problemcontent);
    }

    private void InitialAction() {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                String content = edit_problemcontent.getText().toString();
                if (Vaild.addProblem(ctxt, content)) {
                    //AddProblem();
                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
    }

}
