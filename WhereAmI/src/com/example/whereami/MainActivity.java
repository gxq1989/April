package com.example.whereami;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author April
 * 
 */
public class MainActivity extends Activity {

    private TextView mTextView;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.ACTION_UPDATE_ADDRESS.equals(intent.getAction())) {

                mTextView.setText(intent.getStringExtra(Utils.KEY_ADDRESS));
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) this.findViewById(R.id.tv);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utils.ACTION_UPDATE_ADDRESS);
        this.registerReceiver(mBroadcastReceiver, intentFilter);

        Intent intent = new Intent();
        intent.setClass(this, WhereAmIService.class);
        this.startService(intent);
    }

}
