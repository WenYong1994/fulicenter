package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueSecondActivity;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.BouiqueBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends BaseFragment {

    View view;
    @Bind(R.id.tv_hint_Boutique_First)
    TextView tvHintBoutiqueFirst;
    @Bind(R.id.recyclerview_Boutique_First)
    RecyclerView recyclerviewBoutique;
    @Bind(R.id.swipe_Refresh_Boutique_First)
    SwipeRefreshLayout swipeRefreshBoutiqueFirst;



    ArrayList<BouiqueBean> list;
    LinearLayoutManager mManager;
    BoutiqueFragmentAdpter mAdpter;

    final int PAGE_SIZE=6;
    int page_id =1;

    final int PULL_UP_ACTION=0;
    final int PULL_DOWN_ACTION=1;
    final int BENGIE_ACTION=2;

    int mNewState;

    public BoutiqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_boutique, container, false);
        ButterKnife.bind(this, view);
        //initView();
        //downData();
        super.onCreateView(inflater,container,savedInstanceState);
        setLisener();
        return view;
    }


    private void setLisener() {
        swipeRefreshBoutiqueFirst.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshBoutiqueFirst.setEnabled(true);
                swipeRefreshBoutiqueFirst.setRefreshing(true);
                tvHintBoutiqueFirst.setVisibility(View.VISIBLE);
                downData();
            }
        });
    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        mAdpter = new BoutiqueFragmentAdpter(list,getContext());
        recyclerviewBoutique.setAdapter(mAdpter);
        mManager = new LinearLayoutManager(getContext());
        recyclerviewBoutique.setLayoutManager(mManager);
    }

    @Override
    public void downData() {
        final OkHttpUtils<BouiqueBean[]> myutils = new OkHttpUtils<BouiqueBean[]>(getContext());
        myutils.url(I.SERVER_ROOT+I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BouiqueBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<BouiqueBean[]>() {
                    @Override
                    public void onSuccess(BouiqueBean[] result) {
                        if(result!=null||result.length!=0){
                            ArrayList list1=(myutils.array2List(result));
                            mAdpter.initOrRefreshList(list1);

                            tvHintBoutiqueFirst.setVisibility(View.GONE);
                        }else {
                            L.i("result"+ Arrays.toString(result));
                        }
                    }
                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("下载数据失败");
                    }
                });
    }


    class FooterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_footer)
        TextView tvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    class BoutiqueFirstViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.boutique_first_imageview)
        ImageView boutiqueFirstImageview;
        @Bind(R.id.boutique_first_textview_one)
        TextView boutiqueFirstTextviewOne;
        @Bind(R.id.boutique_first_textview_two)
        TextView boutiqueFirstTextviewTwo;
        @Bind(R.id.boutique_first_textview_three)
        TextView boutiqueFirstTextviewThree;


        public BoutiqueFirstViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class BoutiqueFragmentAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //定义itemType的常量
        final int NEW_GOODS_TYPE=0;
        final int FOOTER_HINT_TYPE = 1;
        final int NULL_TYPE=2;
        //用来判断是否还有更多商品
        private boolean isMore=true;

        ArrayList<BouiqueBean> list;
        Context context;
        ViewGroup parent;

        public BoutiqueFragmentAdpter(ArrayList<BouiqueBean> list, Context context) {
            this.list = list;
            this.context = context;
        }

        public boolean isMore() {
            return isMore;
        }

        public void setMore(boolean more) {
            isMore = more;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            this.parent=parent;
            if(viewType==FOOTER_HINT_TYPE){
                View view1 = LayoutInflater.from(context).inflate(R.layout.footer_item,parent,false);
                //View view1  =View.inflate(context,R.layout.footer_item,null);
                FooterViewHolder holder = new FooterViewHolder(view1);
                return holder;
            }
            View view2 = View.inflate(context,R.layout.boutique_first_item,null);
            final BoutiqueFirstViewHolder holder = new BoutiqueFirstViewHolder(view2);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyMsg myMsg = (MyMsg) holder.itemView.getTag();
                    int id = myMsg.id;
                    String name = myMsg.name;
                    Intent intent = new Intent();
                    intent.putExtra("cat_id",id);
                    intent.putExtra("name",name);
                    MFGT.startActivity((MainActivity)context, BoutiqueSecondActivity.class,intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position==list.size()){
                FooterViewHolder myHolder = (FooterViewHolder) holder;
                myHolder.tvFooter.setText("更多精选，请关注公众微信号ucaiXueYuan");
                return;
            }
            BouiqueBean bouiquBean = list.get(position);
            BoutiqueFirstViewHolder myHolder = (BoutiqueFirstViewHolder) holder;
            myHolder.boutiqueFirstTextviewOne.setText(bouiquBean.getTitle());
            myHolder.boutiqueFirstTextviewTwo.setText(bouiquBean.getName());
            myHolder.boutiqueFirstTextviewThree.setText(bouiquBean.getDescription());
            MyMsg myMsg = new MyMsg(bouiquBean.getId(),bouiquBean.getName());
            myHolder.itemView.setTag(myMsg);
            Picasso.with(context)
                    .load(I.DOWNLOAD_IMG_URL+bouiquBean.getImageurl())
                    .error(R.drawable.nopic)
                    .placeholder(R.drawable.nopic)
                    .into(myHolder.boutiqueFirstImageview);
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size()+1;
        }
        //定义刷新加载时list数据改变的方法
        public  void initOrRefreshList(ArrayList<BouiqueBean> list1){
            this.list.clear();
            this.list.addAll(list1);
            notifyDataSetChanged();
            swipeRefreshBoutiqueFirst.setRefreshing(false);
        }

        public void addList(ArrayList<BouiqueBean> list1){
            this.list.addAll(list1);
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            if(position==list.size()){
                //如果是最后一个就返回FOOTER_HINT
                return FOOTER_HINT_TYPE;
            }
            //如果不是最后就代表显示商品信息
            return NEW_GOODS_TYPE;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class MyMsg {
        int id;
        String name;

        public MyMsg(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

}
