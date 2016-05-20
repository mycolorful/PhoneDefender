package per.yrj.model;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import per.yrj.mobilesafe.R;

/**
 * Created by Administrator on 2016/3/15.
 */
public class AppWidgetService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(){
            @Override
            public void run() {
                //先获取桌面控件管理者
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetReceiver.class);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_layout);
                //设置响应单击事件要执行的事物
                Intent sintent = new Intent();
                //这里隐式的启动一个广播接来执行响应事件
                sintent.setAction("abc.cba");
                //返回pendingIntent
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, sintent, 0);
                //设置监听事件
                views.setOnClickPendingIntent(R.id.tv, pendingIntent);
                appWidgetManager.updateAppWidget(provider, views);
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
