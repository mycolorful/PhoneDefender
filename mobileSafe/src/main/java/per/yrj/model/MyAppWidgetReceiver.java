package per.yrj.model;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/3/14.
 */
public class MyAppWidgetReceiver extends AppWidgetProvider{
    @Override
    public void onReceive(final Context context, Intent intent) {

        context.startService(new Intent(context, AppWidgetService.class));
    }

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);
    }
}

