package com.baq.rn.zbar;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

public class ZBarReactModule extends ReactContextBaseJavaModule {
    private static final String TAG = "ZBarReactModule";
    private static final int REQUEST_LAUNCH_SCAN_ACTIVITY = 10001;

    private final ReactApplicationContext mReactContext;
    private ZbarScanActivityEventListener zbarScanActivityEventListener;

    private Callback mCallback;
    private WritableMap response;

    public ZBarReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        Config.FileUtils.createApplicationFolder();

        zbarScanActivityEventListener = new ZbarScanActivityEventListener(reactContext, new ActivityResultInterface() {
            @Override
            public void callback(int requestCode, int resultCode, Intent data) {
                onActivityResult(requestCode, resultCode, data);
            }
        });
    }

    @Override
    public String getName() {
        return "ZBar";
    }

    @ReactMethod
    public void startScan(final String headerCamera, final Callback callback) {
        if (!isCameraAvailable()) {
            response.putString("error", "Camera not available");
            callback.invoke(response);
            return;
        }

        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            response.putString("error", "can't find current Activity");
            callback.invoke(response);
            return;
        }

        if (!permissionsCheck(currentActivity)) {
            return;
        }

        mCallback = callback;
        response = Arguments.createMap();
        Bundle bundle = new Bundle();
        bundle.putString("headerCamera", headerCamera);
        Intent intent = new Intent(currentActivity, ZbarScannerActivity.class).putExtras(bundle);
        try {
            currentActivity.startActivityForResult(intent, REQUEST_LAUNCH_SCAN_ACTIVITY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            response = Arguments.createMap();
            response.putString("error", "Cannot launch camera");
            callback.invoke(response);
        }
    }

    private boolean isCameraAvailable() {
        return mReactContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || mReactContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private boolean permissionsCheck(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (writePermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 1);
            return false;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallback == null && requestCode != REQUEST_LAUNCH_SCAN_ACTIVITY) {
            return;
        }
        response = Arguments.createMap();

        // user cancel
        if (resultCode != Activity.RESULT_OK) {
            response.putString("code", "");
            response.putString("state", "error");
        } else {
            if (data != null) {
                response.putString("code", data.getStringExtra("code"));
            }
            response.putString("state", "ok");
        }
        mCallback.invoke(response);
        mCallback = null;
        return;
    }
}
