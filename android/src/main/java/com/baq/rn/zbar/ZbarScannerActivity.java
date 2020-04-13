package com.baq.rn.zbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ZbarScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private Float currentRatio = 0.1f;
    ViewGroup contentFrame;
    TextView tvHeaderCamera;
    String headerCamera = "Arahkan kamera ke Barcode/QR Code";
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_zbar_scanner);
        Bundle data = getIntent().getExtras();
        if(data != null){
            headerCamera = data.getString("headerCamera", "Arahkan kamera ke Barcode/QR Code");
        }
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        tvHeaderCamera = (TextView) findViewById(R.id.tvHeaderCamera);

        tvHeaderCamera.setText(headerCamera);
        View btnClose = findViewById(R.id.btnClose);
        View btnAspectRation = findViewById(R.id.btnAspectRation);

        final SharedPreferences sharedPreferences = getSharedPreferences("TADAID", MODE_PRIVATE);
        currentRatio = sharedPreferences.getFloat("RATIO_CAMERA", 0.1f);

        btnAspectRation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentRatio == 0.1f){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("RATIO_CAMERA", 0.5f);
                    editor.apply();
                    currentRatio = 0.5f;
                }else  if(currentRatio == 0.5f){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("RATIO_CAMERA", 1f);
                    editor.apply();
                    currentRatio = 1f;
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("RATIO_CAMERA", 0.1f);
                    editor.apply();
                    currentRatio = 0.1f;
                }

                Toast.makeText(ZbarScannerActivity.this, "Aspect Tolerance Changed to " + currentRatio, Toast.LENGTH_SHORT).show();
                callCamera();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void callCamera(){
        contentFrame.removeAllViews();
        mScannerView = new ZBarScannerView(this);
        contentFrame.addView(mScannerView);
        mScannerView.setAspectTolerance(currentRatio);
        List<BarcodeFormat> setBarCodeFormat = new ArrayList<>();
        setBarCodeFormat.add(BarcodeFormat.CODE128);
        setBarCodeFormat.add(BarcodeFormat.QRCODE);
        mScannerView.setFormats(setBarCodeFormat);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onResume() {
        super.onResume();

        callCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getContents() +
//                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(getApplicationContext(), TakePhoto.class);
//        intent.putExtra("code", rawResult.getContents());
//        startActivity(intent);
        Intent intent = getIntent();
        intent.putExtra("code",rawResult.getContents());
        setResult(RESULT_OK, intent);
        finish();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ZbarScannerActivity.this);
//            }
//        }, 2000);
    }
}
