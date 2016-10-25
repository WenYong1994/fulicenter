package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.mydb.DBDao;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class SplashAvitivty extends AppCompatActivity {
    public static final String TAG = SplashAvitivty.class.getSimpleName();
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
                //这里执行在数据库中获取上一次登录成功储存的对象
                //现从首选项中把账号获得
                SharedPreferences sp = getSharedPreferences("fulicenter_userName",MODE_PRIVATE);
                String userName = sp.getString("userName",null);
                if(userName!=null){
                    UserAvatar user = new DBDao(FuLiCenterApplication.getInstance()).getUser(userName);
                    //再将数据存到FuliCenterAppllcation中\
                    if(user!=null){
                        FuLiCenterApplication.getInstance().setUserAvatar(user);
                        FuLiCenterApplication.getInstance().setUserName(user.getMuserName());
                    }
                }
                MFGT.gotoMainActivity(SplashAvitivty.this);
                finish();
            }
        }, SPLASH_TIME);
    }
}
