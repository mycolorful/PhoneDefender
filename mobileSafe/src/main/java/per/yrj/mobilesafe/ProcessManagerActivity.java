package per.yrj.mobilesafe;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import per.yrj.model.AppInfo;
import per.yrj.model.MyAdapter;

public class ProcessManagerActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_running_process)
    private TextView tvProcess;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.lv_app_list)
    private ListView lvAppList;
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    MyAdapter processListAdapter;
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

    private List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos;
    private ActivityManager activityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();
    }

    private void initUi() {
        setContentView(R.layout.activity_progress_manager);
        ViewUtils.inject(this);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningAppProcessInfos = activityManager.getRunningAppProcesses();
        tvProcess.setText("一共有" + runningAppProcessInfos.size() + "个进程在运行");
        setMemoryInfoText();
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
     * 设置tvRom的显示文本信息
     */
    private void setMemoryInfoText() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;
        long availMem = memoryInfo.availMem;
        tvRom.setText("总内存/可用内存" + Formatter.formatFileSize(this, totalMem) + "/" + Formatter.formatFileSize(this, availMem));
    }

    /**
     * 接收消息后设置lvAppList
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //设置总共的进程数,通过
            // tvProcess.setText("一共有" + runningAppProcessInfos.size() + "个进程在运行");获取的
            //进程数与实际显示的不一致，因为有部分进程的权限比较高，获取不到。
            tvProcess.setText("一共有" + appInfos.size() + "个进程在运行");

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

            processListAdapter = new MyAdapter(userAppInfos, sysAppInfos) {
                ViewHolder holder;

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    //添加两个特殊item
                    TextView tvTitle = new TextView(ProcessManagerActivity.this);
                    tvTitle.setTextSize(25f);
                    tvTitle.setTextColor(Color.BLACK);
                    tvTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                    if (position == 0) {
                        tvTitle.setText("用户进程" + userAppInfos.size() + "个");
                        return tvTitle;
                    } else if (position == userAppInfos.size() + 1) {
                        tvTitle.setText("系统进程" + sysAppInfos.size() + "个");
                        return tvTitle;
                    }

                    //这里必须再加一条convertView instanceof TextView的判断语句
                    //不管converView为空还是属于TextView的实例都要重新填充
                    //因为此listView中含有的两个特殊item不能被其他item复用，而这两个item又是TextView的实例
                    if (convertView == null || convertView instanceof TextView) {
                        holder = new ViewHolder();
                        convertView = View.inflate(ProcessManagerActivity.this, R.layout.process_list_item, null);
                        holder.ivIcon = (ImageView) convertView.findViewById(R.id.tv_app_icon);
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_app_name);
                        holder.tvRom = (TextView) convertView.findViewById(R.id.tv_dirty);
                        holder.cbIsKill = (CheckBox) convertView.findViewById(R.id.cb_is_kill);
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
                private void setData(final AppInfo appInfo) {
                    holder.ivIcon.setImageDrawable(appInfo.getIcon());
                    holder.tvName.setText(appInfo.getAppName());
                    holder.tvRom.setText(Formatter.formatFileSize(ProcessManagerActivity.this, appInfo.getRomDirty()));
                    if(appInfo.getPackageName().equals(getPackageName())){
                        holder.cbIsKill.setVisibility(View.INVISIBLE);
                        return;
                    }
                    holder.cbIsKill.setChecked(appInfo.isChecked());
                }
            };
            lvAppList.setAdapter(processListAdapter);

            lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //如果点击的事两个特殊的item则不作响应
                    if (position == 0 || position == userAppInfos.size() + 1)
                        return;
                    //将点击的item的AppInfo传给clickItem

                    //对CheckBox做处理
                    AppInfo appInfo = null;
                    if (position > 0 && position <= userAppInfos.size()) {
                        //先将用户应用显示出来
                        appInfo = userAppInfos.get(position - 1);
                    } else if (position >= userAppInfos.size() + 2) {
                        //再将系统应用显示出来
                        appInfo = sysAppInfos.get(position - 2 - userAppInfos.size());
                    }
                    if (appInfo.isChecked()) {
                        appInfo.setIsChecked(false);
                        ((CheckBox) (view.findViewById(R.id.cb_is_kill))).setChecked(false);
                    } else {
                        appInfo.setIsChecked(true);
                        ((CheckBox) (view.findViewById(R.id.cb_is_kill))).setChecked(true);
                    }
                }

            });

            lvAppList.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //设置显示标题
                    if (tvTitle.getVisibility() == View.INVISIBLE)
                        tvTitle.setVisibility(View.VISIBLE);
                    if (firstVisibleItem >= 0 && firstVisibleItem <= userAppInfos.size())
                        tvTitle.setText("用户进程" + userAppInfos.size() + "个");
                    else if (firstVisibleItem > userAppInfos.size()) {
                        tvTitle.setText("系统进程" + sysAppInfos.size() + "个");
                    }
                }
            });
        }
    };

    /**
     * @return 获取所有已安装的应用程序信息
     */
    private List<AppInfo> getAppInfos() {
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        appInfos = new ArrayList<AppInfo>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            AppInfo appInfo = new AppInfo();
            //获取包名
            String packageName = runningAppProcessInfo.processName;
            appInfo.setPackageName(packageName);
            //获取并设置包名
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                System.err.println("====================" + packageName);
                continue;
            }
            appInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
            //获取并设置图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            appInfo.setIcon(icon);
            Debug.MemoryInfo[] memoryInfo = activityManager
                    .getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            appInfo.setRomDirty(memoryInfo[0].getTotalPrivateDirty() * 1024);
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
            appInfo.setIsChecked(false);
            appInfos.add(appInfo);
        }
        return appInfos;
    }

    /**
     * 响应点击事件，实现全选功能
     *
     * @param view
     */
    public void chooseAll(View view) {
        for (AppInfo appInfo : userAppInfos) {
            if (!appInfo.getPackageName().equals(getPackageName())) {
                appInfo.setIsChecked(true);
            }
        }
        for (AppInfo appInfo : sysAppInfos) {
            appInfo.setIsChecked(true);
        }
        processListAdapter.notifyDataSetChanged();
    }

    public void chooseOpposite(View view) {
        for (AppInfo appInfo : userAppInfos) {
            if (!appInfo.getPackageName().equals(getPackageName())) {
                if(appInfo.isChecked()){
                    appInfo.setIsChecked(false);
                }else{
                    appInfo.setIsChecked(true);
                }
            }
        }
        for (AppInfo appInfo : sysAppInfos) {
            if(appInfo.isChecked()){
                appInfo.setIsChecked(false);
            }else{
                appInfo.setIsChecked(true);
            }
        }
        processListAdapter.notifyDataSetChanged();
    }

    public void killProcess(View view) {
        int count = 0;
        for (ListIterator<AppInfo> iterator = userAppInfos.listIterator(); iterator.hasNext();) {
            AppInfo appInfo = iterator.next();
            if(appInfo.isChecked()) {
                activityManager.killBackgroundProcesses(appInfo.getPackageName());
                iterator.remove();
                count++;
            }
        }
        for (ListIterator<AppInfo> iterator = sysAppInfos.listIterator(); iterator.hasNext();) {
            AppInfo appInfo = iterator.next();
            if(appInfo.isChecked()) {
                activityManager.killBackgroundProcesses(appInfo.getPackageName());
                iterator.remove();
                count++;
            }
        }
        processListAdapter.notifyDataSetChanged();
        Toast.makeText(this, "成功清理了"+count+"个进程\n", Toast.LENGTH_LONG).show();
        //更新TextView的显示信息
        setMemoryInfoText();
        tvProcess.setText("一共有" + (userAppInfos.size()+sysAppInfos.size()) + "个进程在运行");
    }

    public void openSetting(View view) {

    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvRom;
        public CheckBox cbIsKill;
    }

}
