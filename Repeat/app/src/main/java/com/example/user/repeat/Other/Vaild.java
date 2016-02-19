package com.example.user.repeat.Other;

import android.content.Context;
import android.widget.Toast;

import com.example.user.repeat.R;

/**
 * Created by v on 2016/2/16.
 */
public class Vaild {
	private static String regex = "\\d+";
    public static boolean login(Context context,String phont){
        if(phont.isEmpty()){
            Toast.makeText(context, context.getResources().getString(R.string.msg_err_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
		if (!phont.matches(regex)) {
            Toast.makeText(context, context.getResources().getString(R.string.msg_err_format), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public static boolean addProblem(Context context,String content){
        if(content.isEmpty()){
            Toast.makeText(context, context.getResources().getString(R.string.msg_err_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
