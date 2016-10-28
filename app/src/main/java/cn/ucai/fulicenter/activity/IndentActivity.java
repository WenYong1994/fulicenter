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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class IndentActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indent);
        ButterKnife.bind(this);
        initView();
        setListener();
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


                



            }
        });
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
}
