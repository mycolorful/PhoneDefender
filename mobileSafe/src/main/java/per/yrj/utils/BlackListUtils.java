package per.yrj.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/21.
 */
public class BlackListUtils {
    private SQLiteDatabase db;

    public BlackListUtils(Context context){
        db = new BlackLIstOpenHelper(context).getWritableDatabase();
    }

    /**
     *往blacklist表中添加一条数据
     * @param num 要插入的手机号码
     * @param mode 拦截的模式
     * @return 是否添加成功
     */
    public boolean add (String num, String mode){
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", num);
        contentValues.put("mode", mode);
        long lines = db.insert("blacklist",null,contentValues);
        if(lines == -1)
            return false;
        return true;
    }

    /**
     * 删除一条数据
     * @param num 要删除的号码
     * @return 是否删除成功
     */
    public boolean delete(String num){
        int affect = db.delete("blacklist", "number=?", new String[]{num});
        if (affect == 0)
            return false;
        return true;
    }

    /**
     * 修改拦截模式
     * @param num 要修改的手机号
     * @param mode 要修改为的模式
     * @return 是否修改成功
     */
    public boolean changeMode(String num, String mode){
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int affect = db.update("blacklist", values, "number=?", new String[]{num});
        if(affect == 0)
            return false;
        return true;
    }

    /**
     * 查询指定电话号码的拦截模式
     * @param num 要查询的电话号码
     * @return 拦截模式
     */
    public String queryMode(String num){
        String mode = "";
        Cursor cursor = db.query("blacklist", new String[]{"node"}, "numbler=?", new String[]{num}, null, null, null);
        if(cursor.moveToNext()){
            mode = cursor.getString(0);
        }
        cursor.close();
        return mode;
    }

    /**
     * @return 返回所有的数据
     */
    public List<BlackListInfo> queryAll(){
        List<BlackListInfo> list = new ArrayList<BlackListInfo>();
        Cursor cursor = db.query("blacklist", new String[]{"number", "mode"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            BlackListInfo blackListInfo = new BlackListInfo();
            blackListInfo.setNum(cursor.getString(0));
            blackListInfo.setMode(cursor.getString(1));
            list.add(blackListInfo);
        }
        cursor.close();
        return list;
    }

}
