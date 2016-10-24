package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersionFragment extends Fragment {


    @Bind(R.id.m_Persion_UserAvatar)
    ImageView mPersionUserAvatar;
    @Bind(R.id.m_Persion_UserNick)
    TextView mPersionUserNick;

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
        UserAvatar user = FuLiCenterApplication.getInstance().getUserAvatar();
        mPersionUserNick.setText(FuLiCenterApplication.getInstance().getUserAvatar().getMuserNick());
        Picasso.with(getContext())
                .load(I.SERVER_ROOT+I.REQUEST_DOWNLOAD_AVATAR+"?"
                +I.NAME_OR_HXID+"="+user.getMuserName()+"&avatarType=user_avatar&m_avatar_suffix=.jpg&width=200&height=200")
                .error(R.drawable.user_avatar)
                .placeholder(R.drawable.user_avatar)
                .into(mPersionUserAvatar);
        L.e(I.SERVER_ROOT+I.REQUEST_DOWNLOAD_AVATAR+"?"
                +I.NAME_OR_HXID+"="+user.getMuserName()+"&avatarType=user_avatar&m_avatar_suffix=.jpg&width=200&height=200");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.m_Persion_Setting)
    public void onClick() {

    }
}
