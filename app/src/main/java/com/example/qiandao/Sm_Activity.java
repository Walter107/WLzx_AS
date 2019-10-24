package com.example.qiandao;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.google.zxing.util.QrCodeGenerator;

/*
* 2019/9/27
* 郭帅
* 用户扫码登录界面
* */
public class Sm_Activity extends Activity {
    ImageView imgQrcode;//生成的二维码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_);
        imgQrcode = findViewById(R.id.img_qrcode);
        Intent intent = getIntent();
        String str =intent.getStringExtra("name"),str2 =intent.getStringExtra("number");

        //二维码生成
        Bitmap bitmap = QrCodeGenerator.getQrCodeImage(str+"    "+str2+"    ", imgQrcode.getWidth(), imgQrcode.getHeight());
        if (bitmap == null) {
            Toast.makeText(this, "生成二维码出错", Toast.LENGTH_SHORT).show();
            imgQrcode.setImageBitmap(null);
        } else {
            imgQrcode.setImageBitmap(bitmap);
        }
    }
}
