package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;
import cn.ucai.fulicenter.fragment.PersionFragment;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;

public class MainActivity extends AppCompatActivity {
    RadioButton mRabtn_NewGoods,mRabtn_Boutique,
            mRabtn_Category,mRabtn_Cars,mRabtn_Personal_Center;

    FragmentTransaction ftPersion,ftNewgoods,ftBoutique,ftCategory;
    //设置一个变量来判断是否登录成功
    boolean isLoginSuccess=false;

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
                if(ftNewgoods==null){
                    ftNewgoods = getSupportFragmentManager().beginTransaction();
                }
                NewGoodsFragment newGoodsFragment = new NewGoodsFragment();
                ftNewgoods.replace(R.id.newgoods_fragment_one,newGoodsFragment);
                ftNewgoods.commit();
                ftNewgoods=null;
                break;
            case R.id.rb_id_boutique:
                mutual((RadioButton) v);
                if(ftBoutique==null){
                    ftBoutique = getSupportFragmentManager().beginTransaction();
                }
                BoutiqueFragment boutiqueFragment = new BoutiqueFragment();
                ftBoutique.replace(R.id.newgoods_fragment_one,boutiqueFragment);
                ftBoutique.commit();
                ftBoutique=null;
                break;
            case R.id.rb_id_category:
                if(ftCategory==null){
                    ftCategory = getSupportFragmentManager().beginTransaction();
                }
                CategoryFragment fragment = new CategoryFragment();
                ftCategory.replace(R.id.newgoods_fragment_one,fragment);
                //将这个CategoryFragment全局化
                //FuLiCenterApplication.categoryFragment=fragment;
                ftCategory.commit();
                ftCategory=null;
                mutual((RadioButton) v);
                break;
            case R.id.rb_id_cars:
                mutual((RadioButton) v);
                break;
            case R.id.rb_id_persional_center:
                if(FuLiCenterApplication.getInstance().getUserName()==null){
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                    if(isLoginSuccess){
                        mutual((RadioButton) v);
                    }else {
                        ((RadioButton) v).setChecked(false);
                    }
                }else {
                    if(ftPersion==null){
                        ftPersion = getSupportFragmentManager().beginTransaction();
                    }
                    PersionFragment persionFragmeng = new PersionFragment();
                    ftPersion.replace(R.id.newgoods_fragment_one,persionFragmeng);
                    ftPersion.commit();
                    ftPersion=null;
                    mutual((RadioButton) v);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                if(ftPersion==null){
                    ftPersion = getSupportFragmentManager().beginTransaction();
                }
                PersionFragment persionFragmeng = new PersionFragment();
                ftPersion.replace(R.id.newgoods_fragment_one,persionFragmeng);
                ftPersion.commit();
                ftPersion=null;
                isLoginSuccess=true;
            }
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
