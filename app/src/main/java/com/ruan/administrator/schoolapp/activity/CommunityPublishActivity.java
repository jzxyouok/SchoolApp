package com.ruan.administrator.schoolapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ruan.administrator.schoolapp.R;
import com.ruan.administrator.schoolapp.bean.CommunityPub;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class CommunityPublishActivity extends AppCompatActivity {

    private EditText et_community_title;
    private EditText et_community_content;
    private Button btn_publish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        //初始化Bmob
        Bmob.initialize(this, "8e6a9663ed805f4f07b5b3782e6adadd");
        //设置该文章的用户权限
        setPermission();
        //初始化控件
        initView();

    }
    /**
     * 设置该文章的用户所具有的权限
     */
    private void setPermission() {

    }

    /**
     * 初始化控件
     */
    private void initView() {
        et_community_title = (EditText) findViewById(R.id.et_publish_title);
        et_community_content = (EditText) findViewById(R.id.et_publish_content);
        btn_publish = (Button) findViewById(R.id.btn_publish);
    }

    /**
     * btn_publish的点击事件,发布活动
     */
    public void publish(View view) {
        String title = et_community_title.getText().toString();
        String content = et_community_content.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 设置当前数据的权限
         */
        //创建一个ACL对象
        BmobACL acl = new BmobACL();
        acl.setPublicReadAccess(true);//设置所有人可读
        acl.setWriteAccess(BmobUser.getCurrentUser(this),true);//设置当前用户的读写权限
        //获取当前发布活动的用户名
        String username = (String) BmobUser.getObjectByKey(CommunityPublishActivity.this,"username");
        CommunityPub pub = new CommunityPub();
        pub.setACL(acl);//设置这条数据的ACL信息
        //保存当前用户的id
        pub.setUserObjId(BmobUser.getCurrentUser(this).getObjectId());
        pub.setTitle(title);
        pub.setContent(content);
        pub.setName(username);
        pub.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CommunityPublishActivity.this, "发布成功!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CommunityPublishActivity.this, CommunityActivity.class));
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(CommunityPublishActivity.this, "发布失败！", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
