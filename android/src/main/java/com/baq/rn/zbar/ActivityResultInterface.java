package com.baq.rn.zbar;

import android.content.Intent;

public interface ActivityResultInterface {
    void callback(int requestCode, int resultCode, Intent data);
}
