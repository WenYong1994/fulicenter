package cn.ucai.fulicenter.fragment;


import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BouiqueBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {

    View view;
    @Bind(R.id.tv_hint_Boutique_First)
    TextView tvHintBoutiqueFirst;
    @Bind(R.id.recyclerview_newgoods)
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
        initViwe();
        initData(BENGIE_ACTION);
        return view;
    }

    private void initViwe() {
        list = new ArrayList<>();
        mAdpter = new BoutiqueFragmentAdpter(list,getContext());
        recyclerviewBoutique.setAdapter(mAdpter);
        mManager = new LinearLayoutManager(getContext());
        recyclerviewBoutique.setLayoutManager(mManager);
    }

    private void initData(final int action) {
        final OkHttpUtils<BouiqueBean[]> myutils = new OkHttpUtils<BouiqueBean[]>(getContext());
        myutils.url(I.SERVER_ROOT+I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BouiqueBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<BouiqueBean[]>() {
                    @Override
                    public void onSuccess(BouiqueBean[] result) {
                        if(result!=null||result.length!=0){
                            switch (action){
                                case BENGIE_ACTION:
                                    L.i("result"+ Arrays.toString(result));
                                    ArrayList list1=(myutils.array2List(result));
                                    L.i("result,list+"+list1.toString());
                                    mAdpter.initOrRefreshList(list1);
                                    break;
                                case PULL_DOWN_ACTION:

                                    break;
                                case PULL_UP_ACTION:

                                    break;
                            }
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
            BoutiqueFirstViewHolder holder = new BoutiqueFirstViewHolder(view2);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position==list.size()){
                FooterViewHolder myHolder = (FooterViewHolder) holder;
                if(isMore()){
                    myHolder.tvFooter.setText("上拉加载更多数据");

                }else{
                    myHolder.tvFooter.setText("没有更多数据加载");
                }
                return;
            }
            BouiqueBean bouiquBean = list.get(position);
            BoutiqueFirstViewHolder myHolder = (BoutiqueFirstViewHolder) holder;
            myHolder.boutiqueFirstTextviewOne.setText(bouiquBean.getTitle());
            myHolder.boutiqueFirstTextviewTwo.setText(bouiquBean.getName());
            myHolder.boutiqueFirstTextviewThree.setText(bouiquBean.getDescription());
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
            L.i("initOrRefreshList+1"+list1.toString());
            this.list.addAll(list1);
            L.i("initOrRefreshList+2"+this.list.toString());
            notifyDataSetChanged();
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
}
