package per.yrj.mobilesafe;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import per.yrj.model.AppInfo;
import per.yrj.model.MyAdapter;


public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_ex_stoarge)
    private TextView tvExStorage;
    @ViewInject(R.id.lv_app_list)
    private ListView lvAppList;
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    MyAdapter appListAdapter;
    /**
     * 当前被点击的item的索引
     */
    private int mClickIndex;

    /**
     * 系统应用信息
     */
    private List<AppInfo> sysAppInfos;
    /**
     * 用户应用信息
     */
    private List<AppInfo> userAppInfos;
    /**
     * 所有应用信息
     */
    private List<AppInfo> appInfos;

    /**
     * 记录被点击的item的AppInfo信息
     */
    private AppInfo mClickItem;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();
    }

    private void initUi() {
        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);
        long romStorage = Environment.getDataDirectory().getFreeSpace();
        long sdStorage = Environment.getExternalStorageDirectory().getFreeSpace();
        //Formatter.formatFileSize()可以讲一个long类型的数字转化为文件大小
        tvRom.setText("可用内部存储:" + Formatter.formatFileSize(this, romStorage));
        tvExStorage.setText("可用外部存储：" + Formatter.formatFileSize(this, sdStorage));

        //--------------------------------------------------------------------------
        //获取app数据比较耗时，所以新开线程获取app数据
        new Thread() {
            @Override
            public void run() {
                //获取app数据
                appInfos = getAppInfos();
                //发送handler消息
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 接收消息后设置lvAppList
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sysAppInfos = new ArrayList<AppInfo>();
            userAppInfos = new ArrayList<AppInfo>();
            //将系统app和用户app分开保存在两个list中
            for (AppInfo appInfo : appInfos) {
                if (appInfo.isSystemApp()) {
                    sysAppInfos.add(appInfo);
                } else {
                    userAppInfos.add(appInfo);
                }
            }

            appListAdapter = new MyAdapter(userAppInfos, sysAppInfos) {
                ViewHolder holder;
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    //添加两个特殊item
                    TextView tvTitle = new TextView(AppManagerActivity.this);
                    tvTitle.setTextSize(25f);
                    tvTitle.setTextColor(Color.BLACK);
                    tvTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                    if (position == 0) {
                        tvTitle.setText("用户应用" + userAppInfos.size() + "个");
                        return tvTitle;
                    } else if (position == userAppInfos.size() + 1) {
                        tvTitle.setText("系统应用" + sysAppInfos.size() + "个");
                        return tvTitle;
                    }

                    //这里必须再加一条convertView instanceof TextView的判断语句
                    //不管converView为空还是属于TextView的实例都要重新填充
                    //因为此listView中含有的两个特殊item不能被其他item复用，而这两个item又是TextView的实例
                    if (convertView == null || convertView instanceof TextView) {
                        holder = new ViewHolder();
                        convertView = View.inflate(AppManagerActivity.this, R.layout.app_list_item, null);
                        holder.ivIcon = (ImageView) convertView.findViewById(R.id.tv_app_icon);
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_app_name);
                        holder.tvStorage = (TextView) convertView.findViewById(R.id.tv_app_dir);
                        holder.tvSize = (TextView) convertView.findViewById(R.id.tv_app_size);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    AppInfo appInfo = null;
                    if (position > 0 && position <= userAppInfos.size()) {
                        //先将用户应用显示出来
                        appInfo = userAppInfos.get(position - 1);
                    } else if (position >= userAppInfos.size() + 2) {
                        //再将系统应用显示出来
                        appInfo = sysAppInfos.get(position - 2 - userAppInfos.size());
                    }
                    setData(appInfo);

                    return convertView;
                }

                /**
                 * 把传入的appInfo设置到item上
                 *
                 * @param appInfo
                 */
                private void setData(AppInfo appInfo) {
                    holder.ivIcon.setImageDrawable(appInfo.getIcon());
                    holder.tvName.setText(appInfo.getAppName());
                    if (appInfo.isRom()) {
                        holder.tvStorage.setText("手机内存");
                    } else {
                        holder.tvStorage.setText("SD卡内存");
                    }
                    holder.tvSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getAppSize()));
                }
            };
            lvAppList.setAdapter(appListAdapter);

            lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //如果点击的事两个特殊的item则不作响应
                    if (position == 0 || position == userAppInfos.size() + 1)
                        return;
                    //将点击的item的AppInfo传给clickItem
                    mClickItem = (AppInfo) lvAppList.getItemAtPosition(position);
                    //将点击的item索引传给mClickIndex
                    mClickIndex = position;

                    //如果之前显示了popupWindow,就先关闭
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }

                    showPopupWindow(view);
                }

                /**
                 * 显示popupWindow，并为其中的控件绑定单击事件
                 * @param view
                 */
                private void showPopupWindow(View view) {
                    View v = View.inflate(AppManagerActivity.this, R.layout.popupwindow, null);
                    TextView uninstall = (TextView) v.findViewById(R.id.tv_uninstall);
                    TextView run = (TextView) v.findViewById(R.id.tv_run);
                    TextView share = (TextView) v.findViewById(R.id.tv_share);

                    uninstall.setOnClickListener(AppManagerActivity.this);
                    run.setOnClickListener(AppManagerActivity.this);
                    share.setOnClickListener(AppManagerActivity.this);

                    popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindow.showAsDropDown(view, 130, -120);

                    //设置动画
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(150);
                    v.startAnimation(sa);
                }
            });

            lvAppList.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //如果之前显示了popupWindow并且lvAppList处于被触摸滑动状态，就将其关闭
                    if (popupWindow != null && scrollState == SCROLL_STATE_TOUCH_SCROLL && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //设置显示标题
                    if (tvTitle.getVisibility() == View.INVISIBLE)
                        tvTitle.setVisibility(View.VISIBLE);
                    if (firstVisibleItem >= 0 && firstVisibleItem <= userAppInfos.size())
                        tvTitle.setText("用户应用" + userAppInfos.size() + "个");
                    else if (firstVisibleItem > userAppInfos.size()) {
                        tvTitle.setText("系统应用" + sysAppInfos.size() + "个");
                    }
                }
            });
        }
    };

    /**
     * @return 获取所有已安装的应用程序信息
     */
    private List<AppInfo> getAppInfos() {

        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();
            //获取并设置图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(icon);
            //获取并设置应用名
            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setAppName(name);
            //获取并设置包名
            String packageName = packageInfo.packageName;
            appInfo.setPackageName(packageName);
            //获取并设置应用大小
            String srcDir = packageInfo.applicationInfo.sourceDir;
            long size = new File(srcDir).length();
            appInfo.setAppSize(size);
            //获取应用安装标记
            int flag = packageInfo.applicationInfo.flags;

            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统app
                appInfo.setIsSystemApp(true);
            } else {
                //表示用户app
                appInfo.setIsSystemApp(false);
            }

            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示SD卡存储
                appInfo.setIsRom(false);
            } else {
                //表示内部存储
                appInfo.setIsRom(true);
            }

            appInfos.add(appInfo);
        }
        return appInfos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                Intent unIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + mClickItem.getPackageName()));
                startActivityForResult(unIntent, 0);
                break;
            case R.id.tv_run:
                Intent runIntent = this.getPackageManager().getLaunchIntentForPackage(mClickItem.getPackageName());
                startActivity(runIntent);
                break;
            case R.id.tv_share:
                break;
        }
        popupWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initUi();
    }

    @Override
    protected void onDestroy() {
        if (popupWindow!=null && popupWindow.isShowing())
            popupWindow.dismiss();
        super.onDestroy();
    }



    private static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvStorage;
        public TextView tvSize;
    }

}
