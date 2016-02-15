package com.example.user.repeat.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.R;

/**
 * Created by hao_jun on 2016/2/15.
 */
public class Act_TrimImage extends Activity {
    private Context ctxt = Act_TrimImage.this;
    private ImageView img_result;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trimimage);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void InitialSomething() {
        image = getIntent().getStringExtra("image");
    }

    private void InitialUI() {
        img_result = (ImageView) findViewById(R.id.img_result);
    }

    private void InitialAction() {
        if (!image.isEmpty()) {
            img_result.setImageBitmap(BitmapTransformer.Base64ToBitmap(image));
            Toast.makeText(ctxt, "OK", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctxt, "NO", Toast.LENGTH_SHORT).show();
        }
    }
}
