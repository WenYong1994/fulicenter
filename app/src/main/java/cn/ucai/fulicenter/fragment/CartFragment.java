package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    final int PULL_UP_OR_INIT = 1;
    final int PULL_DOWN = 2;
    int page_id = 1;
    final int PAGE_SIZE = 10;


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
        userAvatar = FuLiCenterApplication.getInstance().getUserAvatar();
        initView();
        initData(PULL_UP_OR_INIT, page_id);
        setListener();
        return view;
    }

    private void setListener() {
        mCartSwi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCartSwi.setRefreshing(true);
                mCartSwi.setEnabled(true);
                page_id = 1;
                initData(PULL_UP_OR_INIT, page_id);
                mCartHint.setVisibility(View.VISIBLE);
            }
        });
    }


    private void initView() {
        ArrayList<CartBean> list = new ArrayList<>();
        adapter = new GoodsAdapter(list, getContext());
        manager = new LinearLayoutManager(getContext());
        mCartRecy.setAdapter(adapter);
        mCartRecy.setLayoutManager(manager);
    }

    private void initData(final int action, int page_id) {
        if (userAvatar == null) {
            return;
        }
        final OkHttpUtils<CartBean[]> utils = new OkHttpUtils<CartBean[]>(getContext());
        utils.url(I.SERVER_ROOT + I.REQUEST_FIND_CARTS)
                .targetClass(CartBean[].class)
                .addParam(I.Cart.USER_NAME, userAvatar.getMuserName())
                .addParam(I.PAGE_ID, page_id + "")
                .addParam(I.PAGE_SIZE, PAGE_SIZE + "")
                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        if (result != null) {
                            switch (action) {
                                case PULL_UP_OR_INIT:
                                    mCartSwi.setRefreshing(false);
                                    mCartHint.setVisibility(View.GONE);
                                    ArrayList<CartBean> list = utils.array2List(result);
                                    for (CartBean bean : list) {
                                        bean.setChecked(false);
                                    }
                                    adapter.initOrRefreshList(list);
                                    adapter.addAllPrivice();
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

    @OnClick(R.id.m_Cart_Pay)
    public void onClick() {
        CommonUtils.showShortToast("点我支付");
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
            ButterKnife.bind(this, itemView);
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
        int mTotalPrivice = 0;
        int mSave = 0;

        boolean isMore = true;

        ArrayList<CartBean> list;
        int falg = 0;
        Context context;
        HashMap<Integer, GoodsDetailsBean> map = new HashMap<>();

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }


        public void addList(ArrayList<CartBean> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void initOrRefreshList(ArrayList<CartBean> list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public GoodsAdapter(ArrayList<CartBean> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public CartGoodsViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.cart_item, null);
            final CartGoodsViewHolder holder = new CartGoodsViewHolder(view);
            setHolderMCartAdd(holder);
            setmCartCheckListener(holder);
            setCartCut(holder);
            return holder;
        }

        private void setCartCut(final CartGoodsViewHolder holder) {
            holder.mCartCut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int potition = (Integer) v.getTag();
                    final CartBean bean = list.get(potition);
                    if (bean.getCount() - 1 == 0) {
                        //就需要删除这个购物车信息
                        new OkHttpUtils<MessageBean>(context)
                                .url(I.SERVER_ROOT + I.REQUEST_DELETE_CART)
                                .addParam(I.Cart.ID, bean.getId() + "")
                                .targetClass(MessageBean.class)
                                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                    @Override
                                    public void onSuccess(MessageBean result) {
                                        if (result.isSuccess()) {
                                            list.remove(potition);
                                            notifyDataSetChanged();
                                            addAllPrivice();
                                            ((MainActivity)getActivity()).setCartCount();
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });


                    }
                    if (bean.getCount() > 0) {
                        //就是将商品数量减一
                        new OkHttpUtils<MessageBean>(context)
                                .url(I.SERVER_ROOT + I.REQUEST_UPDATE_CART)
                                .targetClass(MessageBean.class)
                                .addParam(I.Cart.ID, bean.getId() + "")
                                .addParam(I.Cart.COUNT, bean.getCount() - 1 + "")
                                .addParam(I.Cart.IS_CHECKED, false + "")
                                .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                    @Override
                                    public void onSuccess(MessageBean result) {
                                        bean.setCount(bean.getCount() - 1);
                                        holder.mCartGoodsCount.setText("(" + bean.getCount() + ")");
                                        addAllPrivice();
                                        ((MainActivity)getActivity()).setCartCount();
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                    }
                }
            });
        }

        private void setmCartCheckListener(final CartGoodsViewHolder holder) {
            holder.mCartCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartBean bean = list.get((Integer) v.getTag());
                    bean.setChecked(!bean.isChecked());
                    if (bean.isChecked()) {
                        holder.mCartCheck.setImageResource(R.mipmap.checkbox_pressed);
                    } else {
                        holder.mCartCheck.setImageResource(R.mipmap.checkbox_normal);
                    }
                    addAllPrivice();
                }
            });
        }

        private void setHolderMCartAdd(final CartGoodsViewHolder holder) {
            holder.mCartAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    final CartBean bean = list.get(position);
                    new OkHttpUtils<MessageBean>(context)
                            .targetClass(MessageBean.class)
                            .url(I.SERVER_ROOT + I.REQUEST_UPDATE_CART)
                            .addParam(I.Cart.ID, bean.getId() + "")
                            .addParam(I.Cart.COUNT, bean.getCount() + 1 + "")
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if (result.isSuccess()) {
                                        bean.setCount(bean.getCount() + 1);
                                        holder.mCartGoodsCount.setText("(" + bean.getCount() + ")");
                                        addAllPrivice();
                                        ((MainActivity)getActivity()).setCartCount();
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                }
            });
        }
        @Override
        public void onBindViewHolder(final CartGoodsViewHolder holder, int position) {
            CartBean bean = list.get(position);
            if (bean.isChecked()) {
                holder.mCartCheck.setImageResource(R.mipmap.checkbox_pressed);
            } else {
                holder.mCartCheck.setImageResource(R.mipmap.checkbox_normal);
            }
            holder.mCartCheck.setTag(position);
            holder.mCartAdd.setTag(position);
            holder.mCartCut.setTag(position);
            holder.mCartGoodsCount.setText("(" + bean.getCount() + ")");
            holder.mCartGoodsName.setText(bean.getGoods().getGoodsName());
            Picasso.with(context).load(I.SERVER_ROOT + I.REQUEST_DOWNLOAD_IMAGE + "?" + I.Boutique.IMAGE_URL +
                    "=" + bean.getGoods().getGoodsImg())
                    .placeholder(R.drawable.nopic)
                    .error(R.drawable.nopic)
                    .into(holder.mCartIv);
            holder.mCartPrivice.setText(bean.getGoods().getCurrencyPrice());
        }

        public void addAllPrivice() {
            mTotalPrivice = 0;
            mSave = 0;
            for (CartBean bean : list) {
                if (bean.isChecked()) {
                    mTotalPrivice += bean.getCount() * privice2Int(bean.getGoods().getCurrencyPrice());
                    mSave += -bean.getCount() * (privice2Int(bean.getGoods().getRankPrice()) - privice2Int(bean.getGoods().getCurrencyPrice()));
                }
            }
            mCartTotal.setText("合计：" + mTotalPrivice);
            mCartSave.setText("节省：" + mSave);
        }
        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
