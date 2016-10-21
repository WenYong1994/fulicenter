package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.MFGT;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mCommonTitle.setText("账号登录");
    }


    @OnClick({R.id.mLogin_Btn_Login, R.id.mLogin_Btn_Register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mLogin_Btn_Login:
                break;
            case R.id.mLogin_Btn_Register:
                register();
                break;
        }
    }

    private void register() {
        Intent intent = new Intent(this,RegisterActivity.class);
        MFGT.startActivity(this,intent);
    }
}
