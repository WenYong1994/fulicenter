package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.MFGT;

public class SplashAvitivty extends AppCompatActivity {

    final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_avitivty);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final Intent intent = new Intent(this, MainActivity.class);
        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                MFGT.gotoMainActivity(SplashAvitivty.this);
                finish();
            }
        }, SPLASH_TIME);
    }
}
