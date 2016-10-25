package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.mydb.DBDao;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class UpdataNickActivity extends AppCompatActivity {

    @Bind(R.id.common_back)
    ImageView commonBack;
    @Bind(R.id.common_title)
    TextView commonTitle;
    @Bind(R.id.m_Updata_Nick)
    EditText mUpdataNick;
    @Bind(R.id.m_Updata_Nick_Btn)
    Button mUpdataNickBtn;

    UserAvatar user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_nick);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        commonTitle.setText("修改昵称");
        user=FuLiCenterApplication.getInstance().getUserAvatar();
        if(user!=null){
            mUpdataNick.setText(FuLiCenterApplication.getInstance().getUserAvatar().getMuserNick());
        }
    }

    @OnClick({R.id.common_back, R.id.m_Updata_Nick_Btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_back:
                MFGT.finish(this);
                break;
            case R.id.m_Updata_Nick_Btn:
                new OkHttpUtils<Result>(this).url(I.SERVER_ROOT+I.REQUEST_UPDATE_USER_NICK)
                        .addParam(I.User.USER_NAME,user.getMuserName())
                        .addParam(I.User.NICK,mUpdataNick.getText().toString())
                        .targetClass(Result.class)
                        .execute(new OkHttpUtils.OnCompleteListener<Result>() {
                            @Override
                            public void onSuccess(Result result) {
                                CommonUtils.showShortToast("修改成功");
                                String str = result.getRetData().getMuserNick();
                                FuLiCenterApplication.getInstance().getUserAvatar().setMuserNick(str);
                                FuLiCenterApplication.getInstance().setUserName(str);
                                String strJson = result.getRetData().toString();
                                Gson gson = new Gson();
                                UserAvatar userAvatar1 = gson.fromJson(strJson, UserAvatar.class);
                                if(new DBDao(FuLiCenterApplication.getInstance()).updataUser(userAvatar1)){
                                    CommonUtils.showShortToast("数据库修改成功");
                                }
                                MFGT.finish(UpdataNickActivity.this);
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                break;
        }
    }
}
