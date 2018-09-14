package com.resource.hc.andresformpreview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Size;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

//开机自启动广播接受
public class AutoStartService extends Service
{
    public static boolean otherCameraUsed = false;
    public static boolean myCameraUsed = false;
    public static boolean myCameraWant = false;
    public static Size largest;
    public static long starttime;

    private CameraManager manager;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("测试3 "+" is onCameraAvailable");
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // 获取指定摄像头的特性
        CameraCharacteristics characteristics
                = null;
        try
        {
            characteristics = manager.getCameraCharacteristics("0");
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        // 获取摄像头支持的配置属性
        StreamConfigurationMap map = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        // 获取摄像头支持的最大尺寸
        largest = Collections.max(
                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());
        Lagest.largestgetWidth = largest.getWidth();
        Lagest.largestgetHeight = largest.getHeight();

        System.out.println("测试2 "+" is onCameraAvailable");
        setUpCameraOutputs();
    }

    private void setUpCameraOutputs() {

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        manager.registerAvailabilityCallback(new CameraManager.AvailabilityCallback(){
            //这是后竞争的
            /*@Override
            public void onCameraAvailable(String cameraId)    {
                super.onCameraAvailable(cameraId);
                if(otherCameraUsed && !myCameraUsed && cameraId.equals("0"))
                {
                    starttime = SystemClock.elapsedRealtime();
                    //启动摄像头拍照
                    //摄像头程序将State.otherCameraUsed清false
                    otherCameraUsed = false;
                    myCameraUsed = true;
                    Intent intent = new Intent(AutoStartService.this,CameraView.class);
                    intent.putExtra("starttime", starttime);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //System.out.println("测试入口2");
                    startActivity(intent);
                }
            }
            @Override
            public void onCameraUnavailable(String cameraId)    {
                super.onCameraUnavailable(cameraId);
                //System.out.println("测试 "+cameraId+" is onCameraUnavailable");\
                if(!myCameraUsed && !otherCameraUsed && cameraId.equals("0"))
                {
                    otherCameraUsed = true;
                    myCameraUsed = false;
                    //Intent intent = new Intent(AutoStartService.this,CameraView.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //System.out.println("测试入口2");
                    //startActivity(intent);
                    //System.out.println("测试 otherCameraUsed = true;otherCameraUsed == "+otherCameraUsed);
                }
                else if(myCameraUsed && !otherCameraUsed && cameraId.equals("0"))
                {
                    //还原初始状态
                    myCameraUsed = false;
                    otherCameraUsed = false;
                }
            }*/
            //这是中间竞争的
            @Override
            public void onCameraAvailable(String cameraId)    {
                super.onCameraAvailable(cameraId);
                if(otherCameraUsed && myCameraUsed && !myCameraWant && cameraId.equals("0"))
                {
                    //启动摄像头拍照
                    //摄像头程序将State.otherCameraUsed清false
                    otherCameraUsed = true;
                    myCameraUsed = false;
                    myCameraWant = false;
                    //Intent intent = new Intent(AutoStartService.this,CameraView.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //System.out.println("测试入口2");
                    //startActivity(intent);
                }
                else if(otherCameraUsed && !myCameraUsed && !myCameraWant && cameraId.equals("0"))
                {
                    otherCameraUsed = false;
                    myCameraUsed = false;
                    myCameraWant = false;
                }
            }
            @Override
            public void onCameraUnavailable(String cameraId)    {
                super.onCameraUnavailable(cameraId);
                //System.out.println("测试 "+cameraId+" is onCameraUnavailable");\
                if(!otherCameraUsed && !myCameraUsed && !myCameraWant && cameraId.equals("0"))
                {
                    starttime = SystemClock.elapsedRealtime();
                    otherCameraUsed = true;
                    myCameraUsed = false;
                    myCameraWant = true;
                    Intent intent = new Intent(AutoStartService.this,CameraView.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putLong("starttime", starttime);
                    intent.putExtras(bundle);
                    //System.out.println("测试入口2");
                    startActivity(intent);
                    //System.out.println("测试 otherCameraUsed = true;otherCameraUsed == "+otherCameraUsed);
                }
                else if(otherCameraUsed && !myCameraUsed && myCameraWant && cameraId.equals("0"))
                {
                    otherCameraUsed = true;
                    myCameraUsed = true;
                    myCameraWant = false;
                }
            }
        },null);


    }
    // 为Size定义一个比较器Comparator
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}
