package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    final int PULL_UP_OR_INIT=1;
    final int PULL_DOWN=2;
    int page_id=1;
    final int PAGE_SIZE=10;

    final int DOWN_OVER=0;

    GoodsAdapter adapter;
    LinearLayoutManager manager;

    UserAvatar userAvatar;

    @Bind(R.id.m_Cart_Total)
    TextView mCartTotal;
    @Bind(R.id.m_Cart_Save)
    TextView mCartSave;
    @Bind(R.id.m_Cart_Hint)
    TextView mCartHint;
    @Bind(R.id.m_Cart_Recy)
    RecyclerView mCartRecy;
    @Bind(R.id.m_Cart_Swi)
    SwipeRefreshLayout mCartSwi;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        userAvatar= FuLiCenterApplication.getInstance().getUserAvatar();
        initView();
        initData(PULL_UP_OR_INIT,page_id);
        return view;
    }


    private void initView() {
        ArrayList<CartBean> list = new ArrayList<>();
        adapter=new GoodsAdapter(list,getContext());
        manager =new LinearLayoutManager(getContext());
        mCartRecy.setAdapter(adapter);
        mCartRecy.setLayoutManager(manager);
    }

    private void initData(final int action, int page_id) {
        if(userAvatar==null){
            return;
        }
        final OkHttpUtils<CartBean[]> utils=new OkHttpUtils<CartBean[]>(getContext());
                utils.url(I.SERVER_ROOT+I.REQUEST_FIND_CARTS)
                .targetClass(CartBean[].class)
                .addParam(I.Cart.USER_NAME,userAvatar.getMuserName())
                .addParam(I.PAGE_ID,page_id+"")
                .addParam(I.PAGE_SIZE,PAGE_SIZE+"")
                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        if(result!=null){
                            switch (action){
                                case PULL_UP_OR_INIT:
                                    ArrayList<CartBean> list = utils.array2List(result);
                                    adapter.initOrRefreshList(list);
                                    break;
                                case PULL_DOWN:



                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("获取购物车信息失败");
                    }
                });

    }



    class CartGoodsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.m_Cart_Check)
        ImageView mCartCheck;
        @Bind(R.id.m_Cart_Iv)
        ImageView mCartIv;
        @Bind(R.id.m_Cart_Goods_Name)
        TextView mCartGoodsName;
        @Bind(R.id.m_Cart_Add)
        ImageView mCartAdd;
        @Bind(R.id.m_Cart_Goods_Count)
        TextView mCartGoodsCount;
        @Bind(R.id.m_Cart_Cut)
        ImageView mCartCut;
        @Bind(R.id.m_Cart_Privice)
        TextView mCartPrivice;


        public CartGoodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    //创建一个方法利用正则表达式，将价格字符串转换为整形变量
    public int privice2Int(String privice) {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(privice);
        String intPrivice = "0";
        if (matcher.find()) {
            intPrivice = privice.substring(privice.indexOf(matcher.group()));
        }
        return Integer.parseInt(intPrivice);
    }


    class GoodsAdapter extends RecyclerView.Adapter<CartGoodsViewHolder> {
        //定义一个变量来保存价格总和
        int mTotalPrivice=0;

        boolean isMore=true;

        ArrayList<CartBean> list;
        int falg=0;
        Context context;
        HashMap<Integer,GoodsDetailsBean> map=new HashMap<>();

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }


        public void addList(ArrayList<CartBean> list){
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void initOrRefreshList(ArrayList<CartBean> list){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public GoodsAdapter(ArrayList<CartBean> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public CartGoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context,R.layout.cart_item,null);
            CartGoodsViewHolder holder = new CartGoodsViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CartGoodsViewHolder holder, int position) {
            CartBean bean = list.get(position);
            if(bean.isChecked()){
                holder.mCartCheck.setImageResource(R.mipmap.checkbox_pressed);
            }else {
                holder.mCartCheck.setImageResource(R.mipmap.checkbox_normal);
            }
            holder.mCartGoodsCount.setText("("+bean.getCount()+")");
            holder.mCartGoodsName.setText(bean.getGoods().getGoodsName());
            



        }







        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
