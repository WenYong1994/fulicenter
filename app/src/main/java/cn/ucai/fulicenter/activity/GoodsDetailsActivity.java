package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.view.Cercle;

public class GoodsDetailsActivity extends AppCompatActivity {
public static final String TAG = GoodsDetailsActivity.class.getSimpleName();
    @Bind(R.id.good_detail_title_back)
    ImageView mgoodDetailTitleBack;
    @Bind(R.id.good_detail_title_hint)
    TextView mgoodDetailTitleHint;
    @Bind(R.id.good_detail_title_share)
    ImageView mgoodDetailTitleShare;
    @Bind(R.id.good_detail_title_collect)
    ImageView mgoodDetailTitleCollect;
    @Bind(R.id.good_detail_title_cars)
    ImageView mgoodDetailTitleCars;
    @Bind(R.id.good_detail_english_name)
    TextView mgoodDetailEnglishName;
    @Bind(R.id.good_detail_name)
    TextView mgoodDetailName;
    @Bind(R.id.good_detail_good_color)
    TextView mgoodDetailGoodColor;
    @Bind(R.id.good_detail_price)
    TextView mgoodDetailPrice;
    @Bind(R.id.good_detail_text_detail)
    TextView mgoodDetailTextDetail;
    @Bind(R.id.good_detail_uploading_lin)
    LinearLayout mgoodDetaiUploadingLin;
    @Bind(R.id.Goods_Details_Image_ViewPager)
    ViewPager mGoodsDetailsImageViewPager;
    @Bind(R.id.good_detail_cercle)
    Cercle mGoodDetailCercle;

    //定义一个数组来存储商品详情的图片uri
    ArrayList<AlbumsBean> mAlbumsBeanList;
    ArrayList<ImageView> mImagerViewList;


    boolean isRun=false;
    int mFocus=-1;
    Handler mHandler;
    int mCount;
    boolean isOnTouch=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
        initData();
        setListener();
        initHandler();
        setOnTouch();
    }

    private void setOnTouch() {
        mGoodsDetailsImageViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
                     isOnTouch=true;
                }else {
                    isOnTouch=false;
                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(10);
                            Message msg = Message.obtain();
                            msg.arg1=mGoodsDetailsImageViewPager.getCurrentItem();
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }
                return false;
            }
        });
    }

    private void initHandler() {
        mHandler =  new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(mGoodDetailCercle!=null){
                    //这样让图片和下面圆点保持一致
                    mGoodsDetailsImageViewPager.setCurrentItem(msg.arg1);
                    mGoodDetailCercle.setFocus(msg.arg1);
                    mFocus=msg.arg1;
                }
            }
        };
    }
    private void setListener() {
        mgoodDetailTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id",-1);
        new OkHttpUtils<GoodsDetailsBean>(this)
                .url(I.SERVER_ROOT+I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.Goods.KEY_GOODS_ID,id+"")
                .targetClass(GoodsDetailsBean.class)
                .execute(new OkHttpUtils.OnCompleteListener<GoodsDetailsBean>() {
                    @Override
                    public void onSuccess(GoodsDetailsBean result) {
                        if (result!=null){
                            mgoodDetailEnglishName.setText(result.getGoodsEnglishName());
                            mgoodDetailName.setText(result.getGoodsName());
                            mgoodDetailTextDetail.setText(result.getGoodsBrief());
                            List<AlbumsBean> list = result.getProperties()[0].getAlbums();
                            mAlbumsBeanList=new ArrayList<AlbumsBean>(list);
                            mgoodDetaiUploadingLin.setVisibility(View.GONE);
                            initAdapter();
                        }else {
                            Log.e(TAG, "onSuccess: Null" +id);
                        }
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(GoodsDetailsActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initAdapter() {
        mImagerViewList =new ArrayList<>();
        for(AlbumsBean albumsBean : mAlbumsBeanList){
            ImageView iv = new ImageView(this);
            Picasso.with(this).load(I.SERVER_ROOT+I.REQUEST_DOWNLOAD_IMAGE+"?"+I.Boutique.IMAGE_URL+
                    "="+albumsBean.getImgUrl())
                    .placeholder(R.drawable.nopic)
                    .error(R.drawable.nopic)
                    .into(iv);
            mImagerViewList.add(iv);
        }
        GoodsDetailsAdpter adpter = new GoodsDetailsAdpter(mImagerViewList);
        mGoodsDetailsImageViewPager.setAdapter(adpter);
        setCercle();
    }

    private void setCercle() {
        L.e("mCount:"+mCount);
        mGoodDetailCercle.setCount(mImagerViewList.size());
        mCount=mAlbumsBeanList.size();
        if(!isRun){
            isRun=true;
            new Thread(){
                @Override
                public void run() {
                    whileSetFocus();
                }
            }.start();
        }
    }
    private  void whileSetFocus() {
        while (isRun){
            if(!isOnTouch){
                mFocus = mFocus < mCount - 1 ? mFocus + 1 : 0;
                Message message = Message.obtain();
                message.arg1 = mFocus;
                mHandler.sendMessage(message);
                SystemClock.sleep(4000);
            }
        }
    }

    class GoodsDetailsAdpter extends  PagerAdapter{

        ArrayList<ImageView> list;

        public GoodsDetailsAdpter(ArrayList<ImageView> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
