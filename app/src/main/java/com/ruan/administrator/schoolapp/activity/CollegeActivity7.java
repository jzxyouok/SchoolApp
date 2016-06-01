package com.ruan.administrator.schoolapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruan.administrator.schoolapp.R;
import com.ruan.administrator.schoolapp.bean.CollegePub7;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class CollegeActivity7 extends AppCompatActivity {

    public static final int RESULT_CODE = 1;
    private ListView lv_list;
    private List<CollegePub7> list;
    private LectureAdapter adapter;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(adapter == null){
                adapter = new LectureAdapter();
                lv_list.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture1);
        //初始化Bmob
        Bmob.initialize(this, "8e6a9663ed805f4f07b5b3782e6adadd");
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                BmobQuery<CollegePub7> query = new BmobQuery<CollegePub7>();
                boolean isCache = query.hasCachedResult(CollegeActivity7.this, CollegePub7.class);
                if (isCache){
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
                }else{
                    query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                }

                query.order("-createdAt");
                query.findObjects(CollegeActivity7.this, new FindListener<CollegePub7>() {
                    @Override
                    public void onSuccess(List<CollegePub7> list) {
                        CollegeActivity7.this.list = list;
                        if (list != null){
                            handler.sendEmptyMessage(0);
                            Log.i("test","list："+list.toString());
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.i("test","错误："+s);
                        if(s.contains("The network is not available")){
                            Toast.makeText(CollegeActivity7.this, "网络不可用", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            Toast.makeText(CollegeActivity7.this, "下载错误：" + s, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        }.start();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        ImageView publich = (ImageView) findViewById(R.id.iv_activity_lecture);
        BmobUser user = BmobUser.getCurrentUser(this);
        String username = user.getUsername();
        if (username.equals("管理学院")){
            publich.setVisibility(ImageView.VISIBLE);
        }
        TextView title = (TextView) findViewById(R.id.tv_college_title);
        title.setText("管理学院");
        lv_list = (ListView) findViewById(R.id.list_view);
    }

    /**
     * 发布的点击事件
     */
    public void publish(View view) {
        startActivity(new Intent(CollegeActivity7.this, CollegePublishActivity7.class));
        finish();
    }

    private class LectureAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = View.inflate(CollegeActivity7.this,R.layout.item_list_view,null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            CollegePub7 pub = list.get(position);
            holder.tv_title.setText(pub.getTitle());
            holder.tv_content.setText(pub.getContent());
            //获取发布时间
            String createdAt = pub.getCreatedAt();
            holder.tv_time.setText(createdAt.substring(2));
            holder.tv_name.setText(pub.getName());
            lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CollegePub7 computerPub = list.get(position);
                    Intent intent = new Intent();
                    intent.setClass(CollegeActivity7.this,ShowDetailLectureActivity7.class);
                    intent.putExtra("userObjId", computerPub.getUserObjId());
                    intent.putExtra("title", computerPub.getTitle());
                    intent.putExtra("content", computerPub.getContent());
                    intent.putExtra("objectId", computerPub.getObjectId());
                    startActivityForResult(intent, CollegeActivity7.RESULT_CODE);
                    finish();
                }
            });
            return convertView;
        }
        class ViewHolder{
            TextView tv_title;
            TextView tv_content;
            TextView tv_time;
            TextView tv_name;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(CollegeActivity7.this,CollegeActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}