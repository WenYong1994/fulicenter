package cn.ucai.fulicenter.mydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;

import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;

/**
 * Created by Administrator on 2016/10/24.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    public static final String TAG = MyDBHelper.class.getSimpleName();
    private static MyDBHelper instance;
    private static final String FULICENTENR_USER_TABLE_CREATE=" create table "
            + DBDao.USER_TABLE_NAME + " ( "
            + DBDao.USER_NAME + " text primary key, "
            + DBDao.USER_NICK + " text, "
            + DBDao.USER_AVATER_ID + " integer, "
            + DBDao.USER_AVATER_TYPE + " integer, "
            + DBDao.USER_AVATER_PATH + " text, "
            + DBDao.USER_AVATER_SUFFIX + " text, "
            + DBDao.USER_AVATER_LAST_UPDATE_TIME + " text);";

    public static  String getUserDatabaseName(){
        return I.User.TABLE_NAME+"_demo.db";
    }

    public static MyDBHelper getInstance(Context context){
        if(instance==null){
            instance=new MyDBHelper(context);
        }
        return instance;
    }

    private MyDBHelper(Context context) {
        super(context,getUserDatabaseName(), null, 1);
        SQLiteDatabase db = getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FULICENTENR_USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static void CloseDB(){
        if(instance!=null){
            instance.close();
        }
    }

}
