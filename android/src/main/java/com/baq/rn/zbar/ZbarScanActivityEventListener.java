package com.baq.rn.zbar;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;

public class ZbarScanActivityEventListener implements ActivityEventListener {
    private ActivityResultInterface mCallback;

    public ZbarScanActivityEventListener(ReactApplicationContext reactContext, ActivityResultInterface callback) {
        reactContext.addActivityEventListener(this);
        mCallback = callback;
    }

    // < RN 0.33.0
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallback.callback(requestCode, resultCode, data);
    }

    // >= RN 0.33.0
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        mCallback.callback(requestCode, resultCode, data);
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
