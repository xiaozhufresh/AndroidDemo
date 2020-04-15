package com.ceye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ProgressBar progressBar;
    private Button downLoad;
    private String apk_url = "https://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
    public static final String TAG = MainActivity.class.getSimpleName();
    private final String CEYE = "ceye";
    private Handler mHandler= new downLoadHandler(this);

    public TextView getTextView() {
        return textView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = this.<TextView>findViewById(R.id.tv);
        progressBar = this.<ProgressBar>findViewById(R.id.progressBar);
        downLoad = this.<Button>findViewById(R.id.download);
        downLoad.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downLoad(apk_url);
                    }
                }).start();


            }

            private void downLoad(String apk_url) {
                try {
                    URL url = new URL(apk_url);
                    URLConnection conn = url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    int apkLengh = conn.getContentLength();//需要下载文件的大小
                    Log.i(TAG,"the downLoad files contentlength: "+ apkLengh);
                    String downLoadFodersName = Environment.getExternalStorageDirectory()+ File.separator+ CEYE +File.separator;
                    File file = new File(downLoadFodersName);
                    if (!file.exists()){
                        file.mkdir();
                    }
                    String fileName = downLoadFodersName+"test.apk";
                    File apkFile = new File(fileName);
                    Log.i(TAG,apkFile.getAbsolutePath());
                    if (apkFile.exists()){
                        apkFile.delete();
                    }
                    int downLoadSize = 0;
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    OutputStream outputStream = new FileOutputStream(fileName);
                    while((length=inputStream.read(bytes))!=-1){
                        outputStream.write(bytes,0,length);
                        downLoadSize+=length;
                        int progress = downLoadSize*100/apkLengh;
                        Message message = mHandler.obtainMessage();
                        message.obj=progress;
                        message.what=0;
                        mHandler.sendMessage(message);
                        //progressBar.setProgress(progress);
                        Log.i(TAG,"download progress: "+ progress);
                    }
                    Log.i(TAG,"download success ");
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG,"download fail ");
                }

            }
        });





    }
    public static class downLoadHandler extends Handler {
        public final WeakReference<MainActivity > mMainactivity;

        public downLoadHandler(MainActivity activity) {
            mMainactivity =new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mMainactivity.get();
            switch (msg.what){
                case 0:
                    int progress = (int) msg.obj;
                    activity.getProgressBar().setProgress(progress);
                    activity.textView.setText("当前已下载："+progress+"%");
                    break;

            }
        }
    }
    class TestAnsyTask extends AsyncTask<Integer,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("加载中");
        }

        @Override
        protected String doInBackground(Integer... paramas) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return String.valueOf(paramas[0]*2+2);
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            textView.setText("加载完成: "+integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
