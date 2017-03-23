package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by vengalrao on 22-03-2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int appWidgetId:appWidgetIds){
            RemoteViews view=new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent intent=new Intent(context, WidgetIntentService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            view.setRemoteAdapter(R.id.stock_widget_list,intent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, view);
            } else {
                setRemoteAdapterV11(context, view);
            }

            Intent pend=new Intent(context,DetailActivity.class);
            PendingIntent pendingIntent= TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(pend)
                    .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            view.setPendingIntentTemplate(R.id.stock_widget_list,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,view);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.stock_widget_list);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stock_widget_list);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.stock_widget_list,
                new Intent(context, DetailActivity.class));
    }


    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.stock_widget_list,
                new Intent(context, DetailActivity.class));
    }
}
