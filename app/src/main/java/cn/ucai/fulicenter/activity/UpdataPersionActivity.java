package cn.ucai.fulicenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
import cn.ucai.fulicenter.utilsdao.UtilsDao;

public class UpdataPersionActivity extends AppCompatActivity {

    @Bind(R.id.common_title)
    TextView commonTitle;
    @Bind(R.id.m_Updata_Persion_Iv)
    ImageView mUpdataPersionIv;
    @Bind(R.id.m_Updata_Persion_User_Name)
    TextView mUpdataPersionUserName;
    @Bind(R.id.m_Updata_Persion_User_Nick)
    TextView mUpdataPersionUserNick;

    UserAvatar userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_persion);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        commonTitle.setText("个人资料");
    }

    @OnClick({R.id.common_back, R.id.m_Updata_Persion_Iv_Lin, R.id.m_Updata_Persion_User_Name_Lin, R.id.m_Updata_Persion_User_Nick_Lin, R.id.m_Updata_Persion_Exit_Login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_back:
                MFGT.finish(this);
                break;
            case R.id.m_Updata_Persion_Iv_Lin:
                CommonUtils.showShortToast("修改头像");
                break;
            case R.id.m_Updata_Persion_User_Name_Lin:
                CommonUtils.showShortToast("用户名不能被修改");
                break;
            case R.id.m_Updata_Persion_User_Nick_Lin:
                //这是把一个View放入对话框，实现对View里面控件进行监听
               /* View view1 = View.inflate(this, R.layout.activity_updata_nick, null);
                view1.findViewById(R.id.m_Updata_Nick_Btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtils.showShortToast("执行确认修改的炒作");
                    }
                });

                new AlertDialog.Builder(this).setView(view1)
                        .setMessage("修改昵称")
                        .setCancelable(true)
                        .setPositiveButton("取消修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                */

                Intent intent = new Intent(this,UpdataNickActivity.class);
                MFGT.startActivity(this,intent);
                break;
            case R.id.m_Updata_Persion_Exit_Login:
                //删除内存
                SharedPreferences sp = getSharedPreferences("fulicenter_userName",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                FuLiCenterApplication.getInstance().setUserAvatar(null);
                FuLiCenterApplication.getInstance().setUserName(null);
                //删除数据库里面的数据
                if(new DBDao(FuLiCenterApplication.getInstance()).deleteUser(userAvatar.getMuserName())){
                }
                MFGT.startActivity(this,new Intent(this,LoginActivity.class));
                MFGT.finish(this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userAvatar = FuLiCenterApplication.getInstance().getUserAvatar();
        if(userAvatar!=null){
            mUpdataPersionUserName.setText(userAvatar.getMuserName());
            mUpdataPersionUserNick.setText(userAvatar.getMuserNick());
            Picasso.with(this)
                    .load(I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                            + I.NAME_OR_HXID + "=" + userAvatar.getMuserName() + "&avatarType=user_avatar&m_avatar_suffix=.jpg&width=200&height=200")
                    .error(R.drawable.user_avatar)
                    .placeholder(R.drawable.user_avatar)
                    .into(mUpdataPersionIv);
        }
        UtilsDao.login(this, userAvatar.getMuserName(), userAvatar.getMuserNick(), new OkHttpUtils.OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                if(result!=null){
                    if(result.isRetMsg()){
                        CommonUtils.showShortToast("登录成功");
                        String str= result.getRetData().toString();
                        Gson gson = new Gson();
                        UserAvatar userAvatar1 = gson.fromJson(str, UserAvatar.class);
                        //如果登录成功了，就把账号保存在首选项
                        saveUserName(userAvatar1.getMuserName());
                        //这个是把对象保存在Application和Application里面
                        FuLiCenterApplication.getInstance().setUserName(userAvatar1.getMuserName());
                        new DBDao(FuLiCenterApplication.getInstance()).savaUser(userAvatar);
                        FuLiCenterApplication.getInstance().setUserAvatar(userAvatar);
                        Intent intent = new Intent();
                        setResult(1,intent);
                        finish();
                    }else {
                        CommonUtils.showShortToast("账号密码已近被修改");
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
    }
    private void saveUserName(String userName) {
        SharedPreferences sp = getSharedPreferences("fulicenter_userName",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName",userName);
        editor.commit();
    }
}
