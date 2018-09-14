package com.resource.hc.andresformpreview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity

{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_main);

        Intent service = new Intent(this,AutoStartService.class);
        startService(service);
        //service.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        System.out.println("测试 "+" is onCameraAvailable");
        finish();
    }
}
