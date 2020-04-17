package com.ceye.myapplication;

import android.app.Activity;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;


public class HandlerActivity extends Activity {
    private Button mbutton;
    private static TextView tv_test;
    int count=0;
    private InnerHandler mHandler = new InnerHandler(this);
    private static class InnerHandler extends Handler {
        private final WeakReference<HandlerActivity> mActivity;

        private InnerHandler(HandlerActivity activity) {
            mActivity = new WeakReference<HandlerActivity>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HandlerActivity activity = mActivity.get();

            if(activity!=null){
                switch (msg.what){
                    case 1:
                        tv_test.setText("我是在子线程中更新的……");
                        int count= (int) msg.obj;
                        Toast.makeText(activity,"我有更新了："+String.valueOf(count),Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_test =findViewById(R.id.tv_test);
        mbutton = findViewById(R.id.button);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       Message msg = Message.obtain();
                       msg.what=1;
                       msg.obj=count++;
                       mHandler.sendMessage(msg);

                    }
                }).start();
            }
        });
    }
}
