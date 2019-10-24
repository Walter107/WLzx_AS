package com.example.qiandao;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.mob.MobSDK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;

import static cn.sharesdk.framework.utils.QRCodeUtil.d.v;
/*
*
* 扫码
* 分享
* 删除
* 查看
*
* */

public class Main2Activity extends AppCompatActivity {
    Button button,button2,button_delete;
    String file_name;
    String file_address = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS).getPath()+"/签到文件";
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(Build.VERSION.SDK_INT >= 24) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }


        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button_delete = findViewById(R.id.button_delete);
        listView = findViewById(R.id.LV_file);


        //get file name
        Intent intent = getIntent();
        file_name = intent.getStringExtra("file_name");

        //读取文件内容
        read();
        //扫码
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQrCode();
            }
        });
        //分享
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile(Main2Activity.this,file=new File(file_address+"/"+file_name));
            }
        });
        //删除
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSingleFile(file=new File(file_address+"/"+file_name));
                finish();
            }
        });

        sjdkalfjdsa();
    }
    //权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(Main2Activity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(Main2Activity.this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .CAMERA)) {
                Toast.makeText(this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(Main2Activity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息发送给txt文件
            write(scanResult);
            read();
        }
    }


    //写入操作
    private void write(String string) {
        File datafile = new File(file_address,file_name);
        try {
            if (!datafile.exists()) {
                datafile.createNewFile();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(System.currentTimeMillis()); ////正确
            //append为布尔类型变量，ture为追加文字，false为替换文字
            FileOutputStream fos = new FileOutputStream(datafile,true);
            fos.write((string+date+"\r\n").getBytes("utf-8"));
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    //读取操作
    private void read() {
        File datafile = new File(file_address,file_name);
        try {
            List list = new ArrayList();
            FileInputStream fis = new FileInputStream(datafile);
            String str ;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            while((str=bufferedReader.readLine())!=null){
                list.add(str);
            }
            arrayAdapter = new ArrayAdapter<>(Main2Activity.this,R.layout.items,list);
            listView.setAdapter(arrayAdapter);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void whrite_txt(String string){
//        try {
//            FileOutputStream fileOutputStream=openFileOutput(file_name,MODE_APPEND);
//            PrintStream printStream = new PrintStream(fileOutputStream);
//            printStream.println(string+"\r\n");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void read_txt(){
//        try {
//            List list = new ArrayList();
//            FileInputStream fileInputStream = openFileInput(file_name);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
//            String tempString;
//            while((tempString=bufferedReader.readLine())!=null){
//                list.add(tempString);
//            }
//            arrayAdapter = new ArrayAdapter<>(Main2Activity.this,R.layout.items,list);
//            listView.setAdapter(arrayAdapter);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // 調用系統方法分享文件
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(context,"文件不存在",Toast.LENGTH_SHORT).show();
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    //测试使用
    public void sjdkalfjdsa(){
        Log.d("codecraeer", "getFilesDir = " + getFilesDir());
        Log.d("codecraeer", "getExternalFilesDir = " + getExternalFilesDir("exter_test").getAbsolutePath());
        Log.d("codecraeer", "getDownloadCacheDirectory = " + Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.d("codecraeer", "getDataDirectory = " + Environment.getDataDirectory().getAbsolutePath());
        Log.d("codecraeer", "getExternalStorageDirectory = " + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d("codecraeer", "getExternalStoragePublicDirectory = " + Environment.getExternalStoragePublicDirectory("pub_test"));
    }

    /** 删除单个文件
     *  filePath$Name 要删除的文件的文件名
     * 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件成功！");
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除单个文件失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除单个文件失败不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
