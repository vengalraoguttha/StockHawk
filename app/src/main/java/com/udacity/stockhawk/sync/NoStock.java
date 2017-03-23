package com.udacity.stockhawk.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by vengalrao on 22-03-2017.
 */

public class NoStock extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(QuoteSyncJob.ACTION_NO_STOCK.equals(intent.getAction()))
        Toast.makeText(context,"Invalid Stock!!",Toast.LENGTH_LONG).show();
        else if(QuoteSyncJob.ATION_SUCCESS.equals(intent.getAction())){
            Toast.makeText(context,"Stock Successfully Added!!",Toast.LENGTH_LONG).show();
        }
    }
}
