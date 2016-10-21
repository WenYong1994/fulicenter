package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class BoutiqueSecondActivity extends AppCompatActivity {

    @Bind(R.id.boutique_sencond_title_back)
    ImageView boutiqueSencondTitleBack;
    @Bind(R.id.boutique_sencond_title_text)
    TextView boutiqueSencondTitleText;
    @Bind(R.id.recyclerview_Boutique_Sencond)
    RecyclerView recyclerviewBoutiqueSencond;
    @Bind(R.id.boutique_sencond_swi)
    SwipeRefreshLayout mSwi;
    @Bind(R.id.boutique_sencond_hint)
    TextView tvHint;
    RecyclerView recyclerView;

    GridLayoutManager mGridaLayoutManager;
    ArrayList<NewGoodsBean> list;
    GoodsAdpter mGoodsAdapter;
    int cat_id;


    int mNewState;


    final int PAGE_SIZE=6;
    int page_id =1;

    final int PULL_UP_ACTION=0;
    final int PULL_DOWN_ACTION=1;
    final int BENGIE_ACTION=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_second);
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
        setManagerSpan();
    }

    private void setListener() {
        setRecycler();
        setRefresh();
        setBoutiqueSencondBack();
    }

    private void setBoutiqueSencondBack() {
        boutiqueSencondTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(BoutiqueSecondActivity.this);
            }
        });
    }

    private void setRecycler() {
        recyclerviewBoutiqueSencond.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mNewState=newState;
                int lastPosition = mGridaLayoutManager.findLastVisibleItemPosition();

                if(lastPosition>=mGoodsAdapter.getItemCount()-1&&newState==RecyclerView.SCROLL_STATE_IDLE
                        &&mGoodsAdapter.isMore()){
                    page_id++;
                    downData(page_id,PULL_UP_ACTION);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void setRefresh() {
        mSwi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwi.setEnabled(true);
                mSwi.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                page_id=1;
                downData(page_id,PULL_DOWN_ACTION);
            }
        });
    }

    private void setManagerSpan() {
        mGridaLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position==mGoodsAdapter.getItemCount()-1?2:1;
            }
        });
    }

    private void initView() {
        list=new ArrayList<>();
        mGridaLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        mGoodsAdapter=new GoodsAdpter(list,this);
        recyclerviewBoutiqueSencond.setLayoutManager(mGridaLayoutManager);
        recyclerviewBoutiqueSencond.setAdapter(mGoodsAdapter);
        Intent intent = getIntent();
        cat_id = intent.getIntExtra("cat_id",0);
        String name = intent.getStringExtra("name");
        boutiqueSencondTitleText.setText(name);
    }

    private void initData() {
        downData(1,BENGIE_ACTION);
    }
    private void downData(int page_id, final int action) {
        final OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(this);
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.GoodsDetails.KEY_CAT_ID,cat_id+"")
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
                                    mGoodsAdapter.initOrRefreshList(list);
                                    break;
                                case PULL_DOWN_ACTION:
                                    mSwi.setRefreshing(false);
                                    tvHint.setVisibility(View.GONE);
                                    mGoodsAdapter.setMore(true);
                                    mGoodsAdapter.initOrRefreshList(list);
                                    ImageLoader.release();
                                    break;
                                case PULL_UP_ACTION:
                                    mGoodsAdapter.addList(list);
                                    break;
                            }

                        }else {
                            mGoodsAdapter.setMore(false);
                            mGoodsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e("下载失败了");
                    }
                });

    }


    //这是商品的ViewHolder
    class GoodsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_newgoods)
        ImageView ivNewgoods;
        @Bind(R.id.newgoods_name)
        TextView newgoodsName;
        @Bind(R.id.newgoods_price)
        TextView newgoodsPrice;

        public GoodsViewHolder(View itemView) {
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
    class GoodsAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<NewGoodsBean> newGoodsBeenList;
        private Context context;

        //这是构造器用来降低耦合度
        public GoodsAdpter(ArrayList<NewGoodsBean> goodsBeenList, Context context) {
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
            viewHolder = new GoodsViewHolder(viewNewGoods);
            //这里给itemView设置点击事件
            //长按进入商品详情
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) v.getTag();
                    Intent intent = new Intent();
                    intent.putExtra("id",id);
                    MFGT.startActivity((BoutiqueSecondActivity) context, GoodsDetailsActivity.class,intent);
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
            GoodsViewHolder newGoodsViewHolder = (GoodsViewHolder) holder;
            newGoodsViewHolder.newgoodsName.setText(newGoodsBean.getGoodsName());
            newGoodsViewHolder.newgoodsPrice.setText(newGoodsBean.getCurrencyPrice());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MFGT.finish(this);
    }

}
