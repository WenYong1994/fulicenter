package cn.ucai.fulicenter.mydb;

import android.content.Context;

import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.L;

/**
 * Created by Administrator on 2016/10/24.
 */
public class DBDao {
    public static final String TAG = DBDao.class.getSimpleName();

    public static  final String USER_TABLE_NAME="t_superwechat_user";
    public static final String USER_NAME="m_user_name";
    public static final String USER_NICK="m_user_nick";
    public static final String USER_AVATER_ID="m_user_avatar_id";
    public static final String USER_AVATER_PATH="m_user_avatar_path";
    public static final String USER_AVATER_SUFFIX="m_user_avatar_suffix";
    public static final String USER_AVATER_TYPE="m_user_avatar_type";
    public static final String USER_AVATER_LAST_UPDATE_TIME="m_avatar_lastupdate_time";


    public DBDao(Context context){
        //实例化数据库
        MyDBManager.getInstance().onInit(context);
    }

    public  void colseDB(){
        MyDBManager.getInstance().CloseDB();
    }

    public void savaUser(UserAvatar userAvatar){
        MyDBManager.getInstance().saveUser(userAvatar);
    }

    public  UserAvatar getUser(String username){
        return MyDBManager.getInstance().getUser(username);
    }

    public boolean updataUser(UserAvatar userAvatar){
        return MyDBManager.getInstance().updateUser(userAvatar);
    }

    public boolean deleteUser(String userName){
        return MyDBManager.getInstance().deleteUser(userName);
    }


}
