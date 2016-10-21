package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.MFGT;

public class RegisterActivity extends AppCompatActivity {


    @Bind(R.id.common_title)
    TextView mCommonTitle;
    @Bind(R.id.mRegister_UserName)
    EditText mRegisterUserName;
    @Bind(R.id.mRegister_UserNick)
    EditText mRegisterUserNick;
    @Bind(R.id.mRegister_PassWrod)
    EditText mRegisterPassWrod;
    @Bind(R.id.mRegister_Ok_PassWrod)
    EditText mRegisterOkPassWrod;
    @Bind(R.id.mRegister_Btn_Register)
    Button mRegisterBtnRegister;

    String[] mArrErrMsg = {"账号不能空", "昵称不能为空", "密码不能为空", "确认密码不能为空"};
    @Bind(R.id.common_back)
    ImageView mCommonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void setListener() {
        mCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(RegisterActivity.this);
            }
        });
    }

    private void initData() {
        mCommonTitle.setText("免费注册");
    }

    @OnClick(R.id.mRegister_Btn_Register)
    public void onClick() {
        String userName = mRegisterUserName.getText().toString().trim();
        String userNick = mRegisterUserNick.getText().toString().trim();
        String passWord = mRegisterPassWrod.getText().toString().trim();
        String okPassWord = mRegisterOkPassWrod.getText().toString().trim();

        if (userName == null || userName.equals("")) {
            mRegisterUserName.setError(mArrErrMsg[0]);
            mRegisterUserName.requestFocus();
            return;
        }
        if (userNick == null || userNick.equals("")) {
            mRegisterUserNick.setError(mArrErrMsg[1]);
            mRegisterUserNick.requestFocus();
            return;
        }
        if (passWord == null || passWord.equals("")) {
            mRegisterPassWrod.setError(mArrErrMsg[2]);
            mRegisterPassWrod.requestFocus();
            return;
        }

        if (okPassWord == null || okPassWord.equals("")) {
            mRegisterOkPassWrod.setError(mArrErrMsg[3]);
            mRegisterOkPassWrod.requestFocus();
            return;
        }

        if (!userName.matches("[0-9a-zA-Z_]{6,15}")) {
            mRegisterUserName.setError("账号只能有字母数字下滑线，长度只能是6-15");
            mRegisterUserName.requestFocus();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
