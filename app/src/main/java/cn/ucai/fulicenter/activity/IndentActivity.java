package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;


import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.alipay.JSONException;

public class IndentActivity extends AppCompatActivity  implements PaymentHandler{

    private static String URL = "http://218.244.151.190/demo/charge";


    @Bind(R.id.common_back)
    ImageView commonBack;
    @Bind(R.id.common_title)
    TextView commonTitle;
    @Bind(R.id.m_Indent_Name)
    EditText mIndentName;
    @Bind(R.id.m_Indent_Phone)
    EditText mIndentPhone;
    @Bind(R.id.m_Indent_Address)
    Spinner mIndentAddress;
    @Bind(R.id.m_Indent_Street)
    EditText mIndentStreet;
    @Bind(R.id.m_Indent_Tatol_Privice)
    TextView mIndentTatolPrivice;
    @Bind(R.id.m_Indent_Btn)
    Button mIndentBtn;

    ArrayList<Integer> mListCart;
    int deteCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent);
        ButterKnife.bind(this);
        initView();
        initPay();
        setListener();
    }

    private void initPay() {
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});

        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";

        PingppLog.DEBUG = true;
    }

    private void setListener() {
        commonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(IndentActivity.this);
            }
        });

        mIndentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下面做非空验证
                String name = mIndentName.getText().toString().trim();
                String phone = mIndentPhone.getText().toString().trim();
                String streest = mIndentStreet.getText().toString().trim();
                if(name==null||"".equals(name)){
                    mIndentName.setError("收货人不能为空");
                    mIndentName.requestFocus();
                    return;
                }
                if(phone==null||"".equals(phone)){
                    mIndentPhone.setError("电话不能为空");
                    mIndentPhone.requestFocus();
                    return;
                }
                if(streest==null||"".equals(streest)){
                    mIndentStreet.setError("地址不能为空");
                    mIndentStreet.requestFocus();
                    return;
                }
                L.i(mListCart.toString());
                onPay();


            }
        });
    }

    private void onPay() {
        // 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date());

        // 计算总金额（以分为单位）
        L.i(getIntent().getIntExtra("tatolPrivice",0)+"");
        int amount = getIntent().getIntExtra("tatolPrivice",0)*100;
        JSONArray billList = new JSONArray();
        // 构建账单json对象
        JSONObject bill = new JSONObject();

        // 自定义的额外信息 选填

        try {
            bill.put("order_no", orderNo);
            bill.put("amount", amount);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        //壹收款: 创建支付通道的对话框
        PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);
    }

    private void initView() {
        commonTitle.setText("确认订单");
        Intent intent = getIntent();
        mIndentTatolPrivice.setText(" 合计："+intent.getIntExtra("tatolPrivice",0));
        mListCart= (ArrayList<Integer>) intent.getSerializableExtra("cartList");
        String[] stringArray = getResources().getStringArray(R.array.area);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void handlePaymentResult(Intent data) {
        if (data != null) {
            /**
             * code：支付结果码  -2:服务端错误、 -1：失败、 0：取消、1：成功
             * error_msg：支付结果信息
             */
            int code = data.getExtras().getInt("code");
            String errorMsg = data.getExtras().getString("error_msg");
            if(code==0){
                MFGT.finish(this);
            }else if(code==-1){
                CommonUtils.showLongToast("支付失败，请重新支付");
            }else if(code==1) {
                for(final int Cart_id: mListCart){
                    new OkHttpUtils<MessageBean>(IndentActivity.this)
                            .url(I.SERVER_ROOT + I.REQUEST_DELETE_CART)
                            .addParam(I.Cart.ID, Cart_id+"")
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if(result.isSuccess()){
                                        L.e("删除商品："+Cart_id);
                                    }
                                    deteCount++;
                                }
                                @Override
                                public void onError(String error) {

                                }
                            });
                }
                new Thread(){
                    @Override
                    public void run() {
                        while(true){
                            if(deteCount==mListCart.size()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MFGT.finish(IndentActivity.this);
                                    }
                                });
                                return;
                            }
                        }
                    }
                }.start();

            }





        }
    }
}
