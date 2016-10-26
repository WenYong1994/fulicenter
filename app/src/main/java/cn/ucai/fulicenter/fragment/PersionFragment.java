package cn.ucai.fulicenter.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CollectActivity;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.activity.UpdataPersionActivity;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.mydb.DBDao;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utilsdao.UtilsDao;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersionFragment extends Fragment {


    @Bind(R.id.m_Persion_UserAvatar)
    ImageView mPersionUserAvatar;
    @Bind(R.id.m_Persion_UserNick)
    TextView mPersionUserNick;
    @Bind(R.id.m_Persion_Collect_Treasure)
    TextView mPersionCollectTreasure;
    UserAvatar user;

    public PersionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persion, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        user = FuLiCenterApplication.getInstance().getUserAvatar();
        if(user!=null){
            mPersionUserNick.setText(FuLiCenterApplication.getInstance().getUserAvatar().getMuserNick());

            //这一句是清理缓存，图片可以瞬间显示；
            String str = I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                    + I.NAME_OR_HXID + "=" + user.getMuserName() +
                    "&avatarType=user_avatar&m_avatar_suffix="+
                    user.getMavatarSuffix()+"&width=200&height=200";
            Uri uri = Uri.parse(str);

            Picasso.with(getContext())
                    .load(I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                            + I.NAME_OR_HXID + "=" + user.getMuserName() + "&avatarType=user_avatar&m_avatar_suffix="+user.getMavatarSuffix()+"&width=200&height=200")
                    .error(R.drawable.user_avatar)
                    .placeholder(R.drawable.user_avatar)
                    .into(mPersionUserAvatar);

            /*String strurl= I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                    + I.NAME_OR_HXID + "=" + user.getMuserName() + "&avatarType=user_avatar&m_avatar_suffix="
                    +user.getMavatarSuffix()+"&width=50&height=50";
            ImageLoader.setImage(strurl,getContext(),mPersionUserAvatar,false);*/
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.m_Persion_Setting, R.id.m_Persion_m_Persion_Collect_Treasure_Lin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.m_Persion_Setting:
                Intent intent1 = new Intent((MainActivity)getContext(), UpdataPersionActivity.class);
                MFGT.startActivity((MainActivity)getContext(),intent1);
                break;
            case R.id.m_Persion_m_Persion_Collect_Treasure_Lin:
                Intent intent = new Intent((MainActivity)getContext(), CollectActivity.class);
                MFGT.startActivity((MainActivity)getContext(),intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在网页上面下载收藏商品的信息
        user=FuLiCenterApplication.getInstance().getUserAvatar();
        if(user!=null){
            Picasso.with(getContext())
                    .load(I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                            + I.NAME_OR_HXID + "=" + user.getMuserName() + "&avatarType=user_avatar&m_avatar_suffix="+user.getMavatarSuffix()+"&width=200&height=200")
                    .error(R.drawable.user_avatar)
                    .placeholder(R.drawable.user_avatar)
                    .into(mPersionUserAvatar);
            /*String strurl= I.SERVER_ROOT + I.REQUEST_DOWNLOAD_AVATAR + "?"
                    + I.NAME_OR_HXID + "=" + user.getMuserName() + "&avatarType=user_avatar&m_avatar_suffix="
                    +user.getMavatarSuffix()+"&width=50&height=50";
            L.e(strurl);
            ImageLoader.setImage(strurl,getContext(),mPersionUserAvatar,false);*/

            mPersionUserNick.setText(user.getMuserNick());
            new OkHttpUtils<MessageBean>(getContext())
                    .url(I.SERVER_ROOT+I.REQUEST_FIND_COLLECT_COUNT)
                    .addParam(I.Collect.USER_NAME,user.getMuserName())
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            if(result.isSuccess()){
                                if(result.getMsg().charAt(0)>'9'||result.getMsg().charAt(0)<'0'){
                                    mPersionCollectTreasure.setText(0+"");
                                }else {
                                    mPersionCollectTreasure.setText(result.getMsg());
                                }
                            }else {
                                mPersionCollectTreasure.setText(0+"");
                            }
                        }
                        @Override
                        public void onError(String error) {
                            CommonUtils.showShortToast("获取收藏数量失败");
                        }
                    });
        }
    }
}
