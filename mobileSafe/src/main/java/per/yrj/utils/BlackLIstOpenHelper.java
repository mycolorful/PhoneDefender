package per.yrj.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/2/21.
 */
public class BlackLIstOpenHelper extends SQLiteOpenHelper {

    public BlackLIstOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * 创建一个blacklist表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //number为手机号码，mode为拦截模式
        db.execSQL("create table blacklist (_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
