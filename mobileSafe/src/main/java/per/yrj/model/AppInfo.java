package per.yrj.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/3/1.
 */
public class AppInfo {
    /**
     * app图标
     */
    private Drawable icon;
    /**
     * app的大小
     */
    private long appSize;
    /**
     * app的名称
     */
    private String appName;
    /**
     * 是否为系统程序
     */
    private boolean isSystemApp;
    /**
     * 是否存储在手机内存中
     */
    private boolean isRom;
    /**
     * app的包名
     */
    private String packageName;
    /**
     * 是否被选中
     */
    private boolean isChecked;
    /**
     * 占用运存
     */
    private long romDirty;
    /**
     * apk路径
     */
    private String apkPath;

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public long getRomDirty() {
        return romDirty;
    }

    public void setRomDirty(long romDirty) {
        this.romDirty = romDirty;
    }

    public Drawable getIcon() {
        return icon;

    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setIsSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
