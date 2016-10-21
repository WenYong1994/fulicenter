package cn.ucai.fulicenter.application;

import android.app.Application;

import cn.ucai.fulicenter.fragment.CategoryFragment;

/**
 * Created by Administrator on 2016/10/17.
 */
public class FuLiCenterApplication extends Application{
    private static  FuLiCenterApplication instance;
    public static FuLiCenterApplication application;
    //下面东西有待考究，可能稳定性不高
    //public static CategoryFragment categoryFragment;
    private static String userName;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String username) {
        userName = username;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        instance=this;
    }

    public static FuLiCenterApplication getInstance(){

            if(instance==null){
                synchronized (instance){
                    if(instance==null){
                        instance=new FuLiCenterApplication();
                    }
                }
            }
            return instance;
        }
}
