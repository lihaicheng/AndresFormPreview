package com.resource.hc.andresformpreview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//开机自启动广播接受
public class AutoStartBroadcastReceiver extends BroadcastReceiver
{
    private static final String ACTION1 = "android.intent.action.BOOT_COMPLETED";
    private static final String ACTION2 = "android.intent.action.MEDIA_UNMOUNTED";
    private static final String ACTION3 = "android.intent.action.MEDIA_MOUNTED";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("test service1");

        if (intent.getAction().equals(ACTION1) ) {
            System.out.println("测试"+ACTION1);
            //开机启动程序
            Intent newIntent = new Intent(context,MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            Intent intent2 = new Intent(context,AutoStartService.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
        if ( intent.getAction().equals(ACTION2) ) {
            System.out.println("测试"+ACTION2);
            //开机启动程序
            Intent newIntent = new Intent(context,MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            Intent intent2 = new Intent(context,AutoStartService.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
        if ( intent.getAction().equals(ACTION3)) {
            System.out.println("测试"+ACTION3);
            //开机启动程序
            Intent newIntent = new Intent(context,MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
            Intent intent2 = new Intent(context,AutoStartService.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}

