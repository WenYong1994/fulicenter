package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.OkHttpUtils;

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
    @Bind(R.id.good_detail_view_pager)
    ViewPager mgoodDetailViewPager;
    @Bind(R.id.good_detail_text_detail)
    TextView mgoodDetailTextDetail;
    @Bind(R.id.good_detail_uploading_lin)
    LinearLayout mgoodDetaiUploadingLin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);
        ButterKnife.bind(this);
        initData();
        setListener();
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
                            mgoodDetaiUploadingLin.setVisibility(View.GONE);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
