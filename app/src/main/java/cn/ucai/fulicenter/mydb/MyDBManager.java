package cn.ucai.fulicenter.mydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;

import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.L;

/**
 * Created by Administrator on 2016/10/24.
 */
public class MyDBManager {
    public static final String TAG = MyDBManager.class.getSimpleName();
    private MyDBHelper mHelper;
    private static MyDBManager mManager =new MyDBManager();


    public synchronized static MyDBManager getInstance(){
        return mManager;
    }

    public MyDBHelper onInit(Context context){
        if(mHelper==null){
            mHelper=MyDBHelper.getInstance(context);
        }
        return mHelper;
    }


    public synchronized void CloseDB(){
        if(mHelper!=null){
            MyDBHelper.CloseDB();
        }
    }

    public synchronized boolean saveUser(UserAvatar userAvatar){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(DBDao.USER_NAME,userAvatar.getMuserName());
        values.put(DBDao.USER_NICK,userAvatar.getMuserNick());
        values.put(DBDao.USER_AVATER_ID,userAvatar.getMavatarId());
        values.put(DBDao.USER_AVATER_TYPE,userAvatar.getMavatarType());
        values.put(DBDao.USER_AVATER_PATH,userAvatar.getMavatarPath());
        values.put(DBDao.USER_AVATER_SUFFIX,userAvatar.getMavatarSuffix());
        values.put(DBDao.USER_AVATER_LAST_UPDATE_TIME,userAvatar.getMavatarLastUpdateTime());
        if(db.isOpen()){
            return db.replace(DBDao.USER_TABLE_NAME,null,values)!=-1;
        }
        return false;
    }

    public UserAvatar getUser(String username){
        SQLiteDatabase db =mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBDao.USER_TABLE_NAME,username);
        String str = DBDao.USER_NAME+"=?";
        if(db.isOpen()){
            Cursor cursor = db.query(DBDao.USER_TABLE_NAME, null, str, new String[]{username}, null, null, null);
            if(cursor.moveToNext()){
                UserAvatar user = new UserAvatar();
                user.setMuserName(cursor.getString(cursor.getColumnIndex(DBDao.USER_NAME)));
                user.setMuserNick(cursor.getString(cursor.getColumnIndex(DBDao.USER_NICK)));
                user.setMavatarId(cursor.getInt(cursor.getColumnIndex(DBDao.USER_AVATER_ID)));
                user.setMavatarType(cursor.getInt(cursor.getColumnIndex(DBDao.USER_AVATER_ID)));
                user.setMavatarPath(cursor.getString(cursor.getColumnIndex(DBDao.USER_AVATER_PATH)));
                user.setMavatarSuffix(cursor.getString(cursor.getColumnIndex(DBDao.USER_AVATER_SUFFIX)));
                user.setMavatarLastUpdateTime(Long.valueOf(cursor.getString(cursor.getColumnIndex(DBDao.USER_AVATER_LAST_UPDATE_TIME))));
                return user;
            }
        }
        return null;
    }

    public boolean updateUser(UserAvatar userAvatar){
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBDao.USER_NAME,userAvatar.getMuserName());
        values.put(DBDao.USER_NICK,userAvatar.getMuserNick());
        values.put(DBDao.USER_AVATER_ID,userAvatar.getMavatarId());
        values.put(DBDao.USER_AVATER_TYPE,userAvatar.getMavatarType());
        values.put(DBDao.USER_AVATER_PATH,userAvatar.getMavatarPath());
        values.put(DBDao.USER_AVATER_SUFFIX,userAvatar.getMavatarSuffix());
        values.put(DBDao.USER_AVATER_LAST_UPDATE_TIME,userAvatar.getMavatarLastUpdateTime());
        if(db.isOpen()){
            int update = db.update(DBDao.USER_TABLE_NAME, values, DBDao.USER_NAME + "=?", new String[]{userAvatar.getMuserName()});
            return update>0;
        }
        return true;
    }

    public boolean deleteUser(String userName){
        SQLiteDatabase db= mHelper.getWritableDatabase();
        int i = db.delete(DBDao.USER_TABLE_NAME, DBDao.USER_NAME + "=?", new String[]{userName});
        return  i>0;
    }

}
