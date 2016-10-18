package cn.ucai.fulicenter.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodsDetailsActivity;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    public static final String TAG = NewGoodsFragment.class.getSimpleName();
    View view;
    @Bind(R.id.tv_hint)
    TextView tvHint;
    @Bind(R.id.recyclerview_newgoods)
    RecyclerView recyclerviewNewgoods;
    @Bind(R.id.swipe_Refresh)
    SwipeRefreshLayout swipeRefresh;


    GridLayoutManager mGridaLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    NewGoodsAdpter mNewGoodsAdapter;
    ArrayList<NewGoodsBean> list;


    final int PAGE_SIZE=6;
    int page_id =1;

    final int PULL_UP_ACTION=0;
    final int PULL_DOWN_ACTION=1;
    final int BENGIE_ACTION=2;

    int mNewState;


    public NewGoodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_good, container, false);
        ButterKnife.bind(this, view);
        initView();
        initDate();
        setListener();
        setManagerSpan();
        return view;

    }

    private void setManagerSpan() {
        mGridaLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position==mNewGoodsAdapter.getItemCount()-1?2:1;
            }
        });
    }

    //设置监听事件
    private void setListener() {
        setRefresh();
        setRecycler();
    }

    private void setRecycler() {
        recyclerviewNewgoods.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mNewState=newState;
                int lastPosition = mGridaLayoutManager.findLastVisibleItemPosition();

                if(lastPosition>=mNewGoodsAdapter.getItemCount()-1&&newState==RecyclerView.SCROLL_STATE_IDLE
                        &&mNewGoodsAdapter.isMore()){
                    page_id++;
                    downData(page_id,PULL_UP_ACTION);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void setRefresh() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setEnabled(true);
                swipeRefresh.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                page_id=1;
                downData(page_id,PULL_DOWN_ACTION);
            }
        });
    }

    private void initView() {
        list=new ArrayList<>();
        mGridaLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        mNewGoodsAdapter=new NewGoodsAdpter(list,getContext());
        recyclerviewNewgoods.setLayoutManager(mGridaLayoutManager);
        recyclerviewNewgoods.setAdapter(mNewGoodsAdapter);
    }

    private void downData(int page_id, final int action) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(getContext());
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.GoodsDetails.KEY_CAT_ID,0+"")
                .addParam(I.PAGE_ID,page_id+"")
                .addParam(I.PAGE_SIZE,PAGE_SIZE+"")
                .targetClass(NewGoodsBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
                    @Override
                    public void onSuccess(NewGoodsBean[] result) {

                        if (result!=null&&result.length!=0){
                            list = utils.array2List(result);
                            switch (action){
                                case BENGIE_ACTION:
                                    mNewGoodsAdapter.initOrRefreshList(list);
                                    break;
                                case PULL_DOWN_ACTION:
                                    swipeRefresh.setRefreshing(false);
                                    tvHint.setVisibility(View.GONE);
                                    mNewGoodsAdapter.setMore(true);
                                    mNewGoodsAdapter.initOrRefreshList(list);
                                    ImageLoader.release();
                                    break;
                                case PULL_UP_ACTION:
                                    mNewGoodsAdapter.addList(list);
                                    break;
                            }

                        }else {
                            mNewGoodsAdapter.setMore(false);
                            mNewGoodsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e("下载失败了");
                    }
                });

    }

    //初始化数据
    private void initDate() {
        downData(1,BENGIE_ACTION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //这是商品的ViewHolder
    class NewGoodsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_newgoods)
        ImageView ivNewgoods;
        @Bind(R.id.newgoods_name)
        TextView newgoodsName;
        @Bind(R.id.newgoods_price)
        TextView newgoodsPrice;

        public NewGoodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    //这是底部提醒信息的ViewHolder
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_footer)
        TextView tvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }




    //现在开始定义适配器 Adapter
    class NewGoodsAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<NewGoodsBean> newGoodsBeenList;
        private Context context;

        //这是构造器用来降低耦合度
        public NewGoodsAdpter(ArrayList<NewGoodsBean> goodsBeenList, Context context) {
            this.newGoodsBeenList = goodsBeenList;
            this.context = context;
        }

        //定义itemType的常量
        final int NEW_GOODS_TYPE=0;
        final int FOOTER_HINT_TYPE = 1;
        final int NULL_TYPE=2;
        //用来判断是否还有更多商品
        private boolean isMore=true;



        //取得ViewGroup用来给加在商品图片是设置parent；
        ViewGroup parent;



        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent=parent;
            RecyclerView.ViewHolder viewHolder;
            if(viewType==FOOTER_HINT_TYPE){
                View viewFooter = View.inflate(context,R.layout.footer_item,null);
                viewHolder = new FooterViewHolder(viewFooter);
                return viewHolder;
            }
            View viewNewGoods = View.inflate(context,R.layout.newgoods_item,null);
            viewHolder = new NewGoodsViewHolder(viewNewGoods);
            //这里给itemView设置点击事件
            //长按进入商品详情
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) v.getTag();
                    Intent intent = new Intent();
                    intent.putExtra("id",id);
                    MFGT.startActivity((MainActivity) context, GoodsDetailsActivity.class,intent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //这里进行判断，如果是最后一个就代表是底部提醒信息
            if(position==newGoodsBeenList.size()){
                mGridaLayoutManager.setSpanCount(2);
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                //通过isMore变量来判断是否有更多数据加载
                if(isMore){
                    footerViewHolder.tvFooter.setText("下拉加载更多");
                }else {
                    footerViewHolder.tvFooter.setText("没有跟多数据可以加载");
                }
                return;
            }
            NewGoodsBean newGoodsBean = newGoodsBeenList.get(position);
            NewGoodsViewHolder newGoodsViewHolder = (NewGoodsViewHolder) holder;
            newGoodsViewHolder.newgoodsName.setText(newGoodsBean.getGoodsName());
            newGoodsViewHolder.newgoodsPrice.setText(newGoodsBean.getShopPrice());
            //把商品的id通过itemView的tag传回去
            newGoodsViewHolder.itemView.setTag(newGoodsBean.getGoodsId());
            //下载图片
            ImageLoader.build(I.SERVER_ROOT+I.REQUEST_DOWNLOAD_IMAGE)
                    .addParam(I.Boutique.IMAGE_URL,newGoodsBean.getGoodsThumb())
                    .defaultPicture(R.drawable.nopic)
                    .imageView(newGoodsViewHolder.ivNewgoods)
                    .width(160)
                    .height(240)
                    .setDragging(mNewState==RecyclerView.SCROLL_STATE_IDLE)
                    .listener(parent)
                    .showImage(context);
        }

        @Override
        public int getItemCount() {
            //如果这个list是null就返回0
            return newGoodsBeenList==null?0:newGoodsBeenList.size()+1;

        }

        //定义刷新加载时list数据改变的方法
        public  void initOrRefreshList(ArrayList<NewGoodsBean> list){
            newGoodsBeenList.clear();
            this.newGoodsBeenList.addAll(list);
            L.i(newGoodsBeenList.toString());
            notifyDataSetChanged();
        }

        public void addList(ArrayList<NewGoodsBean> list){
            this.newGoodsBeenList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position==newGoodsBeenList.size()){
                //如果是最后一个就返回FOOTER_HINT
                return FOOTER_HINT_TYPE;
            }
            //如果不是最后就代表显示商品信息
            return NEW_GOODS_TYPE;
        }
    }
}
