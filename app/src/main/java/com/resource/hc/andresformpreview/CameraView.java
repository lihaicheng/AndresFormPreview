package com.resource.hc.andresformpreview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by T5-SK on 2017/3/1.
 */

public class CameraView extends Activity implements View.OnClickListener
{


    private CaptureRequest.Builder previewRequestBuilder;
    // 摄像头ID（通常0代表后置摄像头，1代表前置摄像头）
    private String mCameraId = "0";
    // 定义代表摄像头的成员变量
    private CameraDevice cameraDevice;
    // 预览尺寸
    private Size previewSize;
    //private CaptureRequest.Builder previewRequestBuilder;
    // 定义用于预览照片的捕获请求
    private CaptureRequest previewRequest;
    // 定义CameraCaptureSession成员变量
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    long starttime;
    int temp = 0;
    CameraCaptureSession.StateCallback cameraCaptureSessionstateCallback = new CameraCaptureSession.StateCallback() // ③
    {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            // 如果摄像头为null，直接结束方法
            if (null == cameraDevice)
            {
                return;
            }

            // 当摄像头已经准备好时，开始显示预览
            captureSession = cameraCaptureSession;
            try
            {
                // 设置自动对焦模式


                //previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                //        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);
                previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 3.95f);

                // 设置自动曝光模式
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                // 开始显示相机预览
                previewRequest = previewRequestBuilder.build();
                // 设置预览时连续捕获图像数据
                captureSession.setRepeatingRequest(previewRequest,
                        null, null);  // ④
            } catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Toast.makeText(CameraView.this, "配置失败！"
                    , Toast.LENGTH_SHORT).show();
        }
    };
    //ImageReader.OnImageAvailableListener listener = ;
    //ExecutorService pool = Executors.newSingleThreadExecutor();
    Thread t ;

    /**
     * 超时时间
     */
    private static final int TIME_OUT = 10 * 10000000;
    /**
     * 设置编码
     */
    private static final String CHARSET = "utf-8";
    private static final String SUCCESS = "1";
    private static final String FAILURE = "0";

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback()
    {
        //  摄像头被打开时激发该方法
        @Override
        public void onOpened(CameraDevice cameraDevice)
        {
            CameraView.this.cameraDevice = cameraDevice;
            // 开始预览
            createCameraPreviewSession();  // ②
        }

        // 摄像头断开连接时激发该方法
        @Override
        public void onDisconnected(CameraDevice cameraDevice)
        {
            //cameraDevice.close();
            //CameraView.this.cameraDevice = null;
        }

        // 打开摄像头出现错误时激发该方法
        @Override
        public void onError(CameraDevice cameraDevice, int error)
        {
            cameraDevice.close();
            CameraView.this.cameraDevice = null;

            CameraView.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        t = new Thread(new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                for (int i = 1; i <= 10; i++)
                {
                    uploadFile(i);
                }
                //这里休眠一秒为了等待微信实验，识别传回服务器的图片
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                //cameraDevice.close();
                //CameraView.this.cameraDevice = null;
                //这里本来始要结束的，每次重启都要重新创建，现在将activity放到后台，下次就不创建了
                //moveTaskToBack(true);

                CameraView.this.finish();
            } catch (Exception e)
            {
            }
        }
    }) ;


        starttime = Long.parseLong(this.getIntent().getExtras().get("starttime").toString());
        //AutoStartService.otherCameraUsed = false;
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        //findViewById(R.id.capture).setOnClickListener(this);
        startMyActivity();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();

    }

    /*@Override
    protected void onResume()
    {
        super.onResume();
        temp = 0;

        Log.i("time", "这条log出现说明activity刚才没有被销毁");
        startMyActivity();
    }*/

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        //setIntent(intent);
        //Intent intent1 = getIntent();
        starttime = Long.parseLong(intent.getExtras().get("starttime").toString());
    }

    private void startMyActivity()
    {

        openCamera();
    }

    @Override
    public void onClick(View view)
    {
        //captureStillPicture();
    }


    // 打开摄像头
    private void openCamera()
    {
        setUpCameraOutputs();
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        //Log.i("time", "我现在想知道程序走到哪里");
        try
        {
            // 打开摄像头
            manager.openCamera(mCameraId, stateCallback, null); // ①
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession()
    {
        try
        {
            // 创建作为预览的CaptureRequest.Builder
            previewRequestBuilder = cameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            // 将textureView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(
                    imageReader.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()),
                    cameraCaptureSessionstateCallback
                    , null
            );

        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        } finally
        {

        }
    }

    private void setUpCameraOutputs()
    {
        try
        {

            // 创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader = ImageReader.newInstance(
                    5632, 4224
                    //Lagest.largestgetWidth ,Lagest.largestgetHeight
                    , ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(
                    //listener
                    new ImageReader.OnImageAvailableListener()
                    {
                        // 当照片数据可用时激发该方法
                        @Override
                        public void onImageAvailable(ImageReader reader)
                        {
                            temp++;

                            // 获取捕获的照片数据
                            Image image = reader.acquireNextImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            // 使用IO流将照片写入指定文件
                            File file = new File(getExternalFilesDir(null), "pic" + temp + ".jpg");
                            //File file = new File("/mnt/sdcard/aa/", "pic"+temp+".jpg");
                            long time = SystemClock.elapsedRealtime() - starttime;//AutoStartService.starttime;
                            Log.i("time", "time we used when we got the pic" + temp + ".jpg :" + time + "ms");
                            //System.out.println("拍摄："+  time);
                            buffer.get(bytes);
                            try (
                                    FileOutputStream output = new FileOutputStream(file))
                            {
                                output.write(bytes);

                                //Toast.makeText(CameraView.this, "保存: "
                                //        + file, Toast.LENGTH_SHORT).show();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            } finally
                            {
                                image.close();
                                if (temp == 10)
                                {
                                    t.start();
                                    cameraDevice.close();
                                    CameraView.this.cameraDevice = null;
                                    //pool.submit(target);
                                    /*try
                                    {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    cameraDevice.close();
                                    CameraView.this.cameraDevice = null;

                                    CameraView.this.finish();*/
                                }


                            }
                        }
                    }
                    ,null);

                    }
        catch(NullPointerException e)
            {
                System.out.println("出现错误。");
            }
        }

    private String uploadFile(int temp1) throws Exception
    {
        /**
         * 边界标识 随机生成
         */
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--", LINE_END = "\r\n";
        /**
         * 内容类型
         */
        String CONTENT_TYPE = "multipart/form-data";
        /**
         * 若用虚拟机测试ip为10.0.2.2
         */
        String RequestURL = "http://192.168.191.1:8080/AndroidSer/GetImg";

        URL url = new URL(RequestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(TIME_OUT);
        conn.setConnectTimeout(TIME_OUT);
        /**
         * 允许输入流
         */
        conn.setDoInput(true);
        /**
         *  允许输出流
         */
        conn.setDoOutput(true);
        /**
         * 不允许使用缓存
         */
        conn.setUseCaches(false);
        /**
         * 请求方式
         */
        conn.setRequestMethod("POST");

        /**
         * 设置编码
         */

        conn.setRequestProperty("Charset", CHARSET);
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                + BOUNDARY);
        String path = getExternalFilesDir(null) + "/pic" + temp1 + ".jpg"; //图片地址
        File file = new File(path);     //将图片转换成file类型的文件
        FileInputStream fis = new FileInputStream(file); //创建输入流
        try (
                InputStream in = fis;//getResources().openRawResource(R.raw.img1);
                //这里会出错！！！！！
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
        )
        {


            /**
             * 当文件不为空，把文件包装并且上传
             */


            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */

            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                    + "pic" + temp1 + ".jpg" + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset="
                    + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());


            /**
             * 字节数组 存放读取的文件的数据构造方法
             */

            byte[] buffer = new byte[8192];
            int i = in.read(buffer);
            while (i != -1)
            {
                dos.write(buffer, 0, i);
                dos.flush();
                i = in.read(buffer);
            }
            dos.flush();

            dos.write(LINE_END.getBytes());
            byte[] endData = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
            dos.write(endData);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            int success = 200;

            if (res == success)
            {
                Log.i("tag", "onCreate: 测试到达" + res);
                return SUCCESS;
            }
            //}
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return FAILURE;
    }

}
