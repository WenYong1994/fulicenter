package cn.ucai.fulicenter.utilsdao;

import android.content.Context;

import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.MD5;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * Created by Administrator on 2016/10/21.
 */
public class UtilsDao {
    public static void login(Context context,String userName,String passWord,OkHttpUtils.OnCompleteListener<Result> listener){
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.url(I.SERVER_ROOT+I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,userName)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(passWord))
                .targetClass(Result.class)
                .execute(listener);
    }
}
