package per.yrj.model;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/3/12.
 */
public abstract class MyAdapter extends BaseAdapter {
    private List<AppInfo> userAppInfos;
    private List<AppInfo> sysAppInfos;
    public MyAdapter(List<AppInfo> userAppInfos, List<AppInfo> sysAppInfos){
        this.userAppInfos = userAppInfos;
        this.sysAppInfos = sysAppInfos;
    }

    @Override
    public int getCount() {
        //将系统应用与用户应用相加再加上两个特殊item就是总的item数
        return sysAppInfos.size() + userAppInfos.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        if (position > 0 && position <= userAppInfos.size())
            return userAppInfos.get(position - 1);
        else if (position >= userAppInfos.size() + 2) {
            return sysAppInfos.get(position - 2 - userAppInfos.size());
        }
        //如果是特殊item那么返回空。当然也可以返回其他信息。此处用不到，所以返回null
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
