package com.example.qiandao;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;

import java.io.File;

/*
* 2019/9/27
* 郭帅
* 获取用户信息界面
* 跳转到二维码界面
* */
public class MainActivity extends AppCompatActivity {
    TextView textView2,textView3;
    Button Login_bt,bt_begin;
    EditText editText_name,editText_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startQrCode();
        //初始化
        Login_bt = findViewById(R.id.Login_bt);
        editText_name = findViewById(R.id.Name_ed);
        editText_number = findViewById(R.id.Number_ed);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        bt_begin = findViewById(R.id.bt_begin);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Problem.class);
                startActivity(intent);
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Instructions.class);
                startActivity(intent);
            }
        });
        bt_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main3Activity.class);
                getAlbumStorageDir("签到文件");
                startActivity(intent);
            }
        });
        Login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st1 = editText_name.getText().toString(),st2 = editText_number.getText().toString();
                if(st1.isEmpty()||st2.isEmpty()){
                    Toast.makeText(MainActivity.this,"信息不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this,Sm_Activity.class);
                    intent.putExtra("name",st1);
                    intent.putExtra("number",st2);
                    startActivity(intent);
                }
            }
        });
    }
    //权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(MainActivity.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    //权限申请
    private void startQrCode() {
        // 申请文件读写权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
    }
    //创建外部存储
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if(!file.mkdirs()) {
            Log.d("tag", "Directory not created");
        }
        return file;
    }
}
