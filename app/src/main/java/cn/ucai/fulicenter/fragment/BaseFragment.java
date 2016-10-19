package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;

/**
 * Created by Administrator on 2016/10/19.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initView();
        downData();
        return null;
    }

    public abstract void initView();

    public abstract void downData();

}
