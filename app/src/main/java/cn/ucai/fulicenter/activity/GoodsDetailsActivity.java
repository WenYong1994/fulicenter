package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
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
    @Bind(R.id.good_detail_title_cars_hint)
    TextView mGoodDetailTitlCarsHint;

    //定义一个数组来存储商品详情的图片uri
    ArrayList<AlbumsBean> mAlbumsBeanList;
    ArrayList<ImageView> mImagerViewList;


    //定义一个变量来保存商品是否被收藏
    boolean isCollect=false;
    int id;
    UserAvatar user;

    //定义一个变量来保存商品是否被添加到购物车
    boolean isCart=false;
    int cartCount;

    boolean isRun=false;
    int mFocus=-1;
    Handler mHandler;
    int mCount=0;
    boolean isOnTouch=false;
    int carId;


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

    @Override
    protected void onResume() {
        super.onResume();
        //这里获取是否被收藏
        user = FuLiCenterApplication.getInstance().getUserAvatar();
        if(user!=null){
            new  OkHttpUtils<MessageBean>(this)
                    .url(I.SERVER_ROOT+I.REQUEST_IS_COLLECT)
                    .addParam(I.Goods.KEY_GOODS_ID,id+"")
                    .addParam(I.Collect.USER_NAME,user.getMuserName())
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            if(result.isSuccess()){
                                mgoodDetailTitleCollect.setImageResource(R.mipmap.bg_collect_out);
                                isCollect=true;
                            }else {
                                isCollect=false;
                            }
                        }

                        @Override
                        public void onError(String error) {
                            CommonUtils.showShortToast("获取收藏信息失败");
                        }
                    });
        }

    }

    private void setOnTouch() {
        //对触摸事件的处理。来协调手动拖动图片和自动轮播图片之间的协调
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
        setMGoodDetailTitleBackListener();
        setMGoodsDetailTitleShareListener();
        setMGoodsDetatileColoect();
        setTitleCars();
    }

    private void setTitleCars() {
        mgoodDetailTitleCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user==null){
                    CommonUtils.showShortToast("请先登录");
                    return;
                }
                if(isCart){
                    //就代表已被添加到购物车中,点击就让商品数量加一
                    new OkHttpUtils<MessageBean>(GoodsDetailsActivity.this).url(I.SERVER_ROOT+I.REQUEST_UPDATE_CART)
                            .targetClass(MessageBean.class)
                            .addParam(I.Cart.ID,carId+"")
                            .addParam(I.Cart.COUNT,cartCount+1+"")
                            .addParam(I.Cart.IS_CHECKED,true+"")
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if (result.isSuccess()){
                                        cartCount++;
                                        mGoodDetailTitlCarsHint.setText(cartCount+"");
                                    }
                                }
                                @Override
                                public void onError(String error) {
                                    CommonUtils.showShortToast("增加选中数量失败");
                                }
                            });
                }else {
                    //点击事件就是添加到购物车中去
                    new OkHttpUtils<MessageBean>(GoodsDetailsActivity.this)
                            .url(I.SERVER_ROOT+I.REQUEST_ADD_CART)
                            .addParam(I.Cart.USER_NAME,user.getMuserName())
                            .addParam(I.Cart.GOODS_ID,id+"")
                            .addParam(I.Cart.COUNT,1+"")
                            .addParam(I.Cart.IS_CHECKED,true+"")
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if(result.isSuccess()){
                                        isCart=true;
                                        CommonUtils.showShortToast("已添加商品到购物车中");
                                        cartCount=1;
                                        mGoodDetailTitlCarsHint.setText(""+cartCount);
                                        final OkHttpUtils<CartBean[]> utils = new OkHttpUtils<CartBean[]>(GoodsDetailsActivity.this);
                                        utils.url(I.SERVER_ROOT + I.REQUEST_FIND_CARTS)
                                                .targetClass(CartBean[].class)
                                                .addParam(I.Cart.USER_NAME, user.getMuserName())
                                                .addParam(I.PAGE_ID, 1+"")
                                                .addParam(I.PAGE_SIZE, 1000 + "")
                                                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                                                    @Override
                                                    public void onSuccess(CartBean[] result) {
                                                        for(CartBean bean : result){
                                                            if(bean.getGoodsId()==id){
                                                                carId=bean.getId();
                                                                return;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(String error) {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    CommonUtils.showShortToast("添加到购物车失败");
                                }
                            });
                }
            }
        });
    }

    private void setMGoodsDetatileColoect() {
        mgoodDetailTitleCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user==null){
                    CommonUtils.showShortToast("请先登录");
                    return;
                }
                if(isCollect){
                    //这就是取消收藏
                    new OkHttpUtils<MessageBean>(GoodsDetailsActivity.this)
                            .url(I.SERVER_ROOT+I.REQUEST_DELETE_COLLECT)
                            .addParam(I.Goods.KEY_GOODS_ID,id+"")
                            .addParam(I.Collect.USER_NAME,user.getMuserName())
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if(result.isSuccess()){
                                        mgoodDetailTitleCollect.setImageResource(R.mipmap.bg_collect_in);
                                        isCollect=false;
                                    }else {
                                        CommonUtils.showShortToast("取消收藏失败");
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    CommonUtils.showShortToast("取消收藏失败");
                                }
                            });
                }else {
                    //这就是添加收藏
                    new OkHttpUtils<MessageBean>(GoodsDetailsActivity.this)
                            .url(I.SERVER_ROOT+I.REQUEST_ADD_COLLECT)
                            .addParam(I.Goods.KEY_GOODS_ID,id+"")
                            .addParam(I.Collect.USER_NAME,user.getMuserName())
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if(result.isSuccess()){
                                        mgoodDetailTitleCollect.setImageResource(R.mipmap.bg_collect_out);
                                        isCollect=true;
                                    }else {
                                        CommonUtils.showShortToast("添加收藏失败");
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    CommonUtils.showShortToast("添加收藏失败");
                                }
                            });
                }
            }
        });
    }

    private void setMGoodsDetailTitleShareListener() {
        mgoodDetailTitleShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用那个这个方法弹出分享窗口
                showShare();
            }
        });
    }



    private void setMGoodDetailTitleBackListener() {
        mgoodDetailTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCollect==false){
                    //这里是如果取消收藏，就把这个商品在我的收藏页面里Adapter里面position传回去，用于remove
                    Intent intent1 = getIntent();
                    int position = intent1.getIntExtra("position",-1);
                    Intent intent = new Intent();
                    intent.putExtra("position",position);
                    setResult(RESULT_OK,intent);
                }
                MFGT.finish(GoodsDetailsActivity.this);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);
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
                            mgoodDetailPrice.setText(result.getCurrencyPrice());
                            List<AlbumsBean> list = result.getProperties()[0].getAlbums();
                            mAlbumsBeanList=new ArrayList<AlbumsBean>(list);
                            mgoodDetaiUploadingLin.setVisibility(View.GONE);
                            //数据下载完成后再去调用设置Adapter
                            initAdapter();
                        }else {
                            Log.e(TAG, "onSuccess: Null" +id);
                        }
                    }
                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(error);
                    }
                });
        user=FuLiCenterApplication.getInstance().getUserAvatar();
        if(user!=null){
            //在网络段下载收藏信息，判断这个商品是否被添加到购物车中

            new OkHttpUtils<CartBean[]>(this)
                    .url(I.SERVER_ROOT+I.REQUEST_FIND_CARTS)
                    .targetClass(CartBean[].class)
                    .addParam(I.Cart.USER_NAME,user.getMuserName())
                    .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                        @Override
                        public void onSuccess(CartBean[] result) {
                            if(result!=null){
                                for(CartBean bean:result){
                                    if(bean.getGoodsId()==id){
                                        //这就代表是被添加到购物车中的商品
                                        carId=bean.getId();
                                        mGoodDetailTitlCarsHint.setText(bean.getCount()+"");
                                        cartCount=bean.getCount();
                                        isCart=true;
                                        return;
                                    }
                                }
                                isCart=false;
                                cartCount=0;
                                mGoodDetailTitlCarsHint.setText(cartCount+"");
                            }else {
                                CommonUtils.showShortToast("获取购物车信息失败");
                            }
                        }

                        @Override
                        public void onError(String error) {
                            CommonUtils.showShortToast("获取购物车信息失败");
                        }
                    });
        }

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
            L.e("详细信息uri"+I.SERVER_ROOT+I.REQUEST_DOWNLOAD_IMAGE+"?"+I.Boutique.IMAGE_URL+
                    "="+albumsBean.getImgUrl());
        }
        GoodsDetailsAdpter adpter = new GoodsDetailsAdpter(mImagerViewList);
        mGoodsDetailsImageViewPager.setAdapter(adpter);
        //适配器完成后再去设置圆点的个数
        setCercle();
    }

    private void setCercle() {
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


    //使用快速集成的方法。当点击事件发生时调用这个
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onBackPressed() {

        if(isCollect==false){
            //这里是如果取消收藏，就把这个商品在我的收藏页面里Adapter里面position传回去，用于remove
            Intent intent1 = getIntent();
            int position = intent1.getIntExtra("position",-1);
            Intent intent = new Intent();
            intent.putExtra("position",position);
            setResult(RESULT_OK,intent);
        }

        MFGT.finish(this);
        super.onBackPressed();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


}
