package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.CommonUtils;

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

    String[] mArrUserInformation;
    EditText[] mArrEdit;
    String[] mArrErrMsg={"账号不能空","昵称不能为空","密码不能为空","确认密码不能为空"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mCommonTitle.setText("免费注册");
    }

    @OnClick(R.id.mRegister_Btn_Register)
    public void onClick() {
        String userName = mRegisterUserName.getText().toString();
        String passWord = mRegisterPassWrod.getText().toString();
        String userNick = mRegisterUserNick.getText().toString();
        String okPassWord = mRegisterOkPassWrod.getText().toString();
        String[] arrUserInformation1 = {userName,userNick,passWord,okPassWord};
        EditText[] arrEdit1 = {mRegisterUserName,mRegisterUserNick,mRegisterPassWrod,mRegisterOkPassWrod};
        mArrUserInformation=arrUserInformation1;
        mArrEdit=arrEdit1;
        isNullMethord();
        if(userName.matches("[0-9a-zA-Z_]{6,15}")){
            mRegisterUserName.setError("账号只能有字母数字下滑线，长度只能是6-15");
            mRegisterUserName.requestFocus();
            CommonUtils.showShortToast("账号只能有字母数字下滑线，长度只能是6-15");
        }



    }

    private synchronized boolean isNullMethord() {
        for(int i=0;i<mArrEdit.length;i++){
            if(mArrEdit[i].getText().toString()==null||mArrEdit[i].getText().toString()==""){
                mArrEdit[i].setError(mArrErrMsg[i]);
                mArrEdit[i].requestFocus();
                return true;
            }
        }
        return false;
    }
}
