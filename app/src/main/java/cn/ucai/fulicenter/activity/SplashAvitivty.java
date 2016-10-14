package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.MFGT;

public class SplashAvitivty extends AppCompatActivity {

    final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_avitivty);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Intent intent = new Intent(this,MainActivity.class);
        new Thread(){
            @Override
            public void run() {
                long startTime = SystemClock.currentThreadTimeMillis();
                //DB 等耗时操作
                long endTime = SystemClock.currentThreadTimeMillis();
                if(SPLASH_TIME>(endTime-startTime)){
                    SystemClock.sleep(SPLASH_TIME-(endTime-startTime));
                }
                MFGT.gotoMainActivity(SplashAvitivty.this);
                MFGT.finish(SplashAvitivty.this);
            }
        }.start();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
