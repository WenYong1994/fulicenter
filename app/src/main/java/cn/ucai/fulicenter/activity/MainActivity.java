package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CartFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodsFragment;
import cn.ucai.fulicenter.fragment.PersionFragment;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class MainActivity extends AppCompatActivity {
    RadioButton mRabtn_NewGoods,mRabtn_Boutique,
            mRabtn_Category,mRabtn_Cars,mRabtn_Personal_Center;
    TextView mCarsHint;

    UserAvatar userAvatar;

    FragmentTransaction ftPersion,ftNewgoods,ftBoutique,ftCategory;
    //设置一个变量来判断是否登录成功
    boolean isLoginSuccess=false;

    RadioButton mLastCheckNoPersion;
    Fragment mLastShowNoPersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.i("MainActivity onCreate");
        initView();
    }

    private void initView() {
        mCarsHint= (TextView) findViewById(R.id.tv_id_car_hint);
        mRabtn_NewGoods = (RadioButton) findViewById(R.id.rb_id_newgoods);
        mLastCheckNoPersion=mRabtn_NewGoods;
        mLastShowNoPersion=new NewGoodsFragment();
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
                mLastShowNoPersion=newGoodsFragment;
                ftNewgoods.replace(R.id.newgoods_fragment_one,newGoodsFragment);
                ftNewgoods.commit();
                ftNewgoods=null;
                mLastCheckNoPersion= (RadioButton) v;
                break;
            case R.id.rb_id_boutique:
                mutual((RadioButton) v);
                if(ftBoutique==null){
                    ftBoutique = getSupportFragmentManager().beginTransaction();
                }
                BoutiqueFragment boutiqueFragment = new BoutiqueFragment();
                mLastShowNoPersion=boutiqueFragment;
                ftBoutique.replace(R.id.newgoods_fragment_one,boutiqueFragment);
                ftBoutique.commit();
                ftBoutique=null;
                mLastCheckNoPersion= (RadioButton) v;
                break;
            case R.id.rb_id_category:
                if(ftCategory==null){
                    ftCategory = getSupportFragmentManager().beginTransaction();
                }
                CategoryFragment fragment = new CategoryFragment();
                mLastShowNoPersion=fragment;
                ftCategory.replace(R.id.newgoods_fragment_one,fragment);
                //将这个CategoryFragment全局化
                //FuLiCenterApplication.categoryFragment=fragment;
                ftCategory.commit();
                ftCategory=null;
                mutual((RadioButton) v);
                mLastCheckNoPersion= (RadioButton) v;
                break;
            case R.id.rb_id_cars:
                if(FuLiCenterApplication.getInstance().getUserAvatar()==null){
                    CommonUtils.showShortToast("请先登录");
                    Intent intent = new Intent(this,LoginActivity.class);
                    MFGT.startActivity(this,intent);
                    return;
                }
                mutual((RadioButton) v);
                CartFragment fragmentCart =new CartFragment();
                FragmentTransaction ftCart = getSupportFragmentManager().beginTransaction();
                ftCart.replace(R.id.newgoods_fragment_one,fragmentCart);
                ftCart.commit();
                ftCart=null;
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
    protected void onResume() {
        super.onResume();
        //这就让回来的时候回到新品fragment
        if(FuLiCenterApplication.getInstance().getUserName()==null){
            mLastCheckNoPersion.setChecked(true);
            mutual(mLastCheckNoPersion);
            FragmentTransaction ftNewgoods1 = getSupportFragmentManager().beginTransaction();
            NewGoodsFragment newGoodsFragment = new NewGoodsFragment();
            ftNewgoods1.replace(R.id.newgoods_fragment_one,mLastShowNoPersion);
            ftNewgoods1.commitAllowingStateLoss();
            ftNewgoods1=null;
        }
        userAvatar=FuLiCenterApplication.getInstance().getUserAvatar();
        if(userAvatar!=null){
            //获取购物车中的数量
            setCartCount();
        }
    }

    public void setCartCount() {
        new OkHttpUtils<CartBean[]>(this)
                .url(I.SERVER_ROOT+I.REQUEST_FIND_CARTS)
                .targetClass(CartBean[].class)
                .addParam(I.Cart.USER_NAME,userAvatar.getMuserName())
                .execute(new OkHttpUtils.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        if(result!=null){
                            int total=0;
                            for(CartBean bean:result){
                                total+=bean.getCount();
                            }
                            mCarsHint.setText(total+"");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                if(ftPersion==null){
                    ftPersion = getSupportFragmentManager().beginTransaction();
                }
                mRabtn_Personal_Center.setChecked(true);
                mutual(mRabtn_Personal_Center);
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
