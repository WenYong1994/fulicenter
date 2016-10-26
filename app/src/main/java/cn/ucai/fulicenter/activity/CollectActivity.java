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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class CollectActivity extends AppCompatActivity {

    @Bind(R.id.m_Collect)
    RecyclerView mCollect;
    @Bind(R.id.common_title)
    TextView commonTitle;
    @Bind(R.id.m_Collect_Hint)
    TextView mCollectHint;
    @Bind(R.id.m_Collect_Swi)
    SwipeRefreshLayout mCollectSwi;

    int Page_id = 1;
    final int PAGE_SIZE = 5;

    GridLayoutManager manager;
    MyAdapter adapter;

    final int PULL_UP = 1;
    final int PULL_DOWN_OR_INIT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);
        initView();
        initData(Page_id, PULL_DOWN_OR_INIT);
        setLisner();
    }

    private void setLisner() {
        setSwiListner();
        setMRecycler();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position==adapter.getItemCount()-1?2:1;
            }
        });
    }

    private void setMRecycler() {
        mCollect.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition = manager.findLastVisibleItemPosition();

                if (lastPosition >= adapter.getItemCount()-1 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && adapter.isMore()) {
                    Page_id++;
                    initData(Page_id, PULL_UP);
                }

            }
        });
    }

    private void setSwiListner() {
        mCollectSwi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCollectSwi.setEnabled(true);
                mCollectSwi.setRefreshing(true);
                mCollectHint.setVisibility(View.VISIBLE);
                Page_id = 1;
                initData(Page_id, PULL_DOWN_OR_INIT);
            }
        });
    }

    private void initData(int page_id, final int actiong) {
        final UserAvatar user = FuLiCenterApplication.getInstance().getUserAvatar();
        final OkHttpUtils<CollectBean[]> utils = new OkHttpUtils<>(this);
        utils.url(I.SERVER_ROOT + I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, user.getMuserName())
                .addParam(I.PAGE_ID, page_id + "")
                .addParam(I.PAGE_SIZE, PAGE_SIZE + "")
                .targetClass(CollectBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CollectBean[]>() {
                    @Override
                    public void onSuccess(CollectBean[] result) {
                        if (result != null && result.length != 0) {
                            adapter.setMore(true);
                            ArrayList<CollectBean> list = utils.array2List(result);
                            switch (actiong) {
                                case PULL_DOWN_OR_INIT:
                                    adapter.updataList(list);
                                    mCollectSwi.setRefreshing(false);
                                    mCollectHint.setVisibility(View.GONE);
                                    break;
                                case PULL_UP:
                                    adapter.addList(list);
                                    break;
                            }
                        } else {
                            adapter.setMore(false);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void initView() {
        commonTitle.setText("收藏的宝贝");
        ArrayList<CollectBean> list = new ArrayList<>();
        manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        adapter = new MyAdapter(list, this);
        mCollect.setLayoutManager(manager);
        mCollect.setAdapter(adapter);
    }

    @OnClick(R.id.common_back)
    public void onClick() {
        MFGT.finish(this);
    }


    class FooterViewHolser extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_footer)
        TextView tvFooter;

        public FooterViewHolser(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.m_Goods_Iv)
        ImageView mGoodsIv;
        @Bind(R.id.m_Good_Detele_Collect)
        ImageView mGoodDeteleCollect;
        @Bind(R.id.m_Goods_Name)
        TextView mGoodsName;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final int FOOTER_ITEM=1;
        final  int GOODS_ITEM=2;


        ArrayList<CollectBean> list;
        Context context;

        boolean isMore = true;


        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        public MyAdapter(ArrayList<CollectBean> list, Context context) {
            this.list = list;
            this.context = context;
        }

        public void updataList(ArrayList<CollectBean> list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void addList(ArrayList<CollectBean> list) {
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        public void removeList(int position){
            this.list.remove(position);
            notifyDataSetChanged();
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==FOOTER_ITEM){
                View view = View.inflate(context,R.layout.footer_item,null);
                FooterViewHolser holder = new FooterViewHolser(view);
                return holder;
            }
            View view = View.inflate(context, R.layout.collect_item, null);
            MyViewHolder holder = new MyViewHolder(view);
            holder.mGoodDeteleCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = (int) v.getTag();
                    final CollectBean bean = list.get(position);
                    new OkHttpUtils<MessageBean>(context)
                            .url(I.SERVER_ROOT+I.REQUEST_DELETE_COLLECT)
                            .addParam(I.Collect.USER_NAME,bean.getUserName())
                            .addParam(I.Collect.GOODS_ID,bean.getGoodsId()+"")
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if(result.isSuccess()){
                                        list.remove(position);
                                        notifyDataSetChanged();
                                    }else {
                                        CommonUtils.showShortToast("删除收藏失败");
                                    }
                                }
                                @Override
                                public void onError(String error) {
                                    CommonUtils.showShortToast("删除收藏失败");
                                }
                            });
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    CollectBean bean = list.get(position);

                    //这里使用startActivityForResult。来获取商品是否被取消收藏
                    Intent intent = new Intent(CollectActivity.this,GoodsDetailsActivity.class);
                    intent.putExtra("id", bean.getGoodsId());
                    intent.putExtra("position",position);
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                }
            });
            return holder;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position==list.size()){
                FooterViewHolser hl= (FooterViewHolser) holder;
                if(isMore){
                    hl.tvFooter.setText("上拉加载更多数据");
                }else {
                    hl.tvFooter.setText("没有更多的收藏");
                }
                return;
            }
            CollectBean bean = list.get(position);
            MyViewHolder holder1 = (MyViewHolder) holder;
            holder1.mGoodsName.setText(bean.getGoodsName().toString());
            Picasso.with(context)
                    .load(I.DOWNLOAD_IMG_URL + bean.getGoodsImg())
                    .placeholder(R.drawable.nopic)
                    .into(holder1.mGoodsIv);
            holder1.itemView.setTag(position);
            holder1.mGoodDeteleCollect.setTag(position);
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==list.size()){
                return FOOTER_ITEM;
            }
            return GOODS_ITEM;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                int position = data.getIntExtra("position",-1);
                if(position>=0){
                    adapter.removeList(position);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
