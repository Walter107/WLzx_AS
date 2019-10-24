package com.example.qiandao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
*
* 获取文件列表
* 历史信息
* 新建活动
*
* */
public class Main3Activity extends AppCompatActivity {
    ListView listView;
    Button button;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        button = findViewById(R.id.button3);
        listView = findViewById(R.id.M3_List);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处应该输入创建文件的名称
                final EditText editText = new EditText(Main3Activity.this);
                new AlertDialog.Builder(Main3Activity.this)
                        .setTitle("请输入活动名称")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Main3Activity.this, Main2Activity.class);
                                intent.putExtra("file_name", editText.getText() + ".txt");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        //列表判空
        if(getFilesAllName(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getPath()+"/签到文件")==null){
            //不显示列表
        }else {
            arrayAdapter = new ArrayAdapter<>(Main3Activity.this, R.layout.items, getFilesAllName(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS).getPath()+"/签到文件"));
            listView.setAdapter(arrayAdapter);
            //显示列表，设置点击事件
            list_click();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getFilesAllName(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getPath()+"/签到文件")==null){
            //不显示列表
        }else {
            arrayAdapter = new ArrayAdapter<>(Main3Activity.this, R.layout.items, getFilesAllName(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS).getPath()+"/签到文件"));
            listView.setAdapter(arrayAdapter);
            list_click();
        }
    }
    //获取目录下文件名称
    public List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");return null;}
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getName());
        }
        return s;
    }
    public void list_click(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.item_tx);
                Intent intent = new Intent(Main3Activity.this,Main2Activity.class);
                intent.putExtra("file_name",textView.getText());
                startActivity(intent);
            }
        });
    }
    //删除前面的内容只保持文件名称
    public String getfilename(String s) {
        return s.substring(s.indexOf("qiandao/files/")+"qiandao/files/".length());
    }
}
