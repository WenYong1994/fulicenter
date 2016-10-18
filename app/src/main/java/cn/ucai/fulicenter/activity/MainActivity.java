package cn.ucai.fulicenter.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;
import cn.ucai.fulicenter.utils.L;

public class MainActivity extends AppCompatActivity {
    RadioButton mRabtn_NewGoods,mRabtn_Boutique,
            mRabtn_Category,mRabtn_Cars,mRabtn_Personal_Center;
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.i("MainActivity onCreate");
        initView();
    }

    private void initView() {
        mRabtn_NewGoods = (RadioButton) findViewById(R.id.rb_id_newgoods);
        mRabtn_Boutique = (RadioButton) findViewById(R.id.rb_id_boutique);
        mRabtn_Category = (RadioButton) findViewById(R.id.rb_id_category);
        mRabtn_Cars = (RadioButton) findViewById(R.id.rb_id_cars);
        mRabtn_Personal_Center = (RadioButton) findViewById(R.id.rb_id_persional_center);
        NewGoodsFragment newGoodsFragment = new NewGoodsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.newgoods_fragment_one,newGoodsFragment);
        ft.commit();
    }

    public void onCheckedChange(View v){
        switch (v.getId()){
            case R.id.rb_id_newgoods:
                mutual((RadioButton) v);
                FragmentTransaction ftNewgoods = getSupportFragmentManager().beginTransaction();
                NewGoodsFragment newGoodsFragment = new NewGoodsFragment();
                ftNewgoods.replace(R.id.newgoods_fragment_one,newGoodsFragment);
                ftNewgoods.commit();
                break;
            case R.id.rb_id_boutique:
                mutual((RadioButton) v);
                FragmentTransaction ftBoutique = getSupportFragmentManager().beginTransaction();
                BoutiqueFragment boutiqueFragment = new BoutiqueFragment();
                ftBoutique.replace(R.id.newgoods_fragment_one,boutiqueFragment);
                ftBoutique.commit();
                break;
            case R.id.rb_id_category:
                mutual((RadioButton) v);
                break;
            case R.id.rb_id_cars:
                mutual((RadioButton) v);
                break;
            case R.id.rb_id_persional_center:
                mutual((RadioButton) v);
                break;
        }
    }

    private void mutual(RadioButton rabtn){
        if(rabtn!=mRabtn_Boutique){
            mRabtn_Boutique.setChecked(false);
        }
        if(rabtn!=mRabtn_Cars){
            mRabtn_Cars.setChecked(false);

        }
        if(rabtn!=mRabtn_Category){
            mRabtn_Category.setChecked(false);

        }
        if(rabtn!=mRabtn_NewGoods){
            mRabtn_NewGoods.setChecked(false);
        }
        if(rabtn!=mRabtn_Personal_Center){
            mRabtn_Personal_Center.setChecked(false);
        }
    }
}
