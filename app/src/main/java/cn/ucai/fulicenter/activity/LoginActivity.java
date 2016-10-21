package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utilsdao.UtilsDao;
import okhttp3.internal.Util;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.common_title)
    TextView mCommonTitle;
    @Bind(R.id.mLogin_UserName)
    EditText mLoginUserName;
    @Bind(R.id.mLogin_PassWord)
    EditText mLoginPassWord;
    @Bind(R.id.mLogin_Btn_Login)
    Button mLoginBtnLogin;
    @Bind(R.id.mLogin_Btn_Register)
    Button mLoginBtnRegister;
    @Bind(R.id.common_back)
    ImageView commonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected void onStart() {
        MyReceiver my = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("cn.ucai.fulicenter_register_to_login");
        registerReceiver(my,filter);
        super.onStart();
    }

    private void initData() {
        mCommonTitle.setText("账号登录");
    }


    @OnClick({R.id.mLogin_Btn_Login, R.id.mLogin_Btn_Register,R.id.common_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mLogin_Btn_Login:
                final String userName = mLoginUserName.getText().toString().trim();
                String passWord = mLoginPassWord.getText().toString().trim();
                UtilsDao.login(this, userName, passWord, new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if(result!=null){
                            if(result.isRetMsg()){
                                CommonUtils.showShortToast("登录成功");
                                FuLiCenterApplication.setUserName(userName);
                            }else {
                                CommonUtils.showShortToast("账号密码有误，请检查后再登录");
                            }
                        }else {
                            CommonUtils.showShortToast("登录失败");
                        }
                    }
                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("网络开小差中...");
                    }
                });

                break;
            case R.id.mLogin_Btn_Register:
                register();
                break;
            case R.id.common_back:
                MFGT.finish(this);
                break;
        }
    }

    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        MFGT.startActivity(this, intent);
    }

    class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String useName = intent.getStringExtra("userName");
            String passWord = intent.getStringExtra("passWord");
            L.i("userName:"+useName+",passWord:"+passWord);
            if(useName!=null&&passWord!=null){
                mLoginPassWord.setText(passWord);
                mLoginUserName.setText(useName);
            }
        }
    }


}
