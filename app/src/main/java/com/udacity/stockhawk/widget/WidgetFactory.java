package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by vengalrao on 22-03-2017.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Cursor mCursor;
    private Context mContext;
    private int widgetId;

    public WidgetFactory(Context context, Intent intent){
        mContext=context;
        widgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if(mCursor!=null){
            mCursor.close();
        }
        mCursor=mContext.getContentResolver().query(Contract.Quote.URI,
                new String[]{Contract.Quote._ID,Contract.Quote.COLUMN_SYMBOL,Contract.Quote.COLUMN_PRICE,Contract.Quote.COLUMN_PERCENTAGE_CHANGE},
                null,
                null,
                Contract.Quote.COLUMN_SYMBOL
                );
    }

    @Override
    public void onDestroy() {
        if(mCursor!=null){
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if(mCursor!=null)
        return mCursor.getCount();
        else
            return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        RemoteViews views=new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        Log.v("Widget","entered1");
        if(mCursor.moveToPosition(position)){
            Log.v("Widget","entered");
            views.setTextViewText(R.id.stock_symbol,mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
            views.setTextViewText(R.id.bid_price,mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
            views.setTextViewText(R.id.stock_change,mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE)));
        }

        final Intent fillIntent=new Intent();
        fillIntent.putExtra("SYMBOL",mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
        views.setOnClickFillInIntent(R.id.stock_widget_list,fillIntent);
        return views;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.stock_widget_list, description);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
