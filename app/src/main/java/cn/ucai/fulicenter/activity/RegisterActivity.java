package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.MD5;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

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
        panduanNull();
        OkHttpUtils<Result> utils = new OkHttpUtils<>(this);
        //final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("注册中。。。");
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();

        utils.url(I.SERVER_ROOT+I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.NICK,userNick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(passWord))
                .targetClass(Result.class)
                .post()
                .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        //pd.dismiss();
                        if(result==null){
                            Toast.makeText(FuLiCenterApplication.getInstance(), "注册失败", Toast.LENGTH_SHORT).show();
                        }else {
                           if(result.isRetMsg()){
                               CommonUtils.showShortToast("注册成功");
                               finish();
                           } else {
                               if (result.getRetCode()==I.MSG_REGISTER_USERNAME_EXISTS){
                                   CommonUtils.showShortToast("账号已存在");
                               }
                               CommonUtils.showShortToast("注册失败");
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        //pd.dismiss();
                        Toast.makeText(FuLiCenterApplication.getInstance(), "由于网络原因注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void panduanNull() {
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

        if (!userName.matches("[a-zA-Z]\\w{5,15}")) {
            mRegisterUserName.setError("非法账号，5-15个字符，并且使用字母开头");
            mRegisterUserName.requestFocus();
        }

        if(!okPassWord.equals(passWord)){
            mRegisterOkPassWrod.setError("两次密码必须相同");
            mRegisterOkPassWrod.requestFocus();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
