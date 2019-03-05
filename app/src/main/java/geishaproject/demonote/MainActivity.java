
package geishaproject.demonote;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;

import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;



import model.Data;

import presenter.DataDao;
import presenter.MyAdapter;

import presenter.MyDatabase;


//version 0.7
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView listView;

    FloatingActionButton floatingActionButton;

    LayoutInflater layoutInflater;

    ArrayList<Data> arrayList;

    DataDao dataDao;




    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.layout_listview);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_note);

        layoutInflater = getLayoutInflater();

        dataDao = new DataDao(this);

        arrayList = dataDao.getAllDataArr();

        MyAdapter adapter = new MyAdapter(layoutInflater,arrayList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {   //点击一下跳转到编辑页面（编辑页面与新建页面共用一个布局）

            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),New_note.class);

                intent.putExtra("ids",arrayList.get(position).getIds());

                startActivity(intent);

                MainActivity.this.finish();

            }

        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {   //长按删除

            @Override

            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(MainActivity.this) //弹出一个对话框
                        .setMessage("确定要删除此便签？")
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                //删除广播
                                daleteSever(position);
                                dataDao.deleteDataByIds(arrayList.get(position).getIds());
                                MyAdapter myAdapter = new MyAdapter(layoutInflater,arrayList);
                                myAdapter.notifyDataSetChanged();
                                listView.setAdapter(myAdapter);

                            }

                        })
                        .create()
                        .show();
                return true;
            }

        });



        floatingActionButton.setOnClickListener(new View.OnClickListener() {   //点击悬浮按钮时，跳转到新建页面

            @Override

            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),New_note.class);
                startActivity(intent);
                MainActivity.this.finish();
            }

        });


    }

    private void daleteSever(int position) {
        //删除广播
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //建立广播对象
        Intent intent = new Intent(MyApplication.getContext(),AlarmReceiver.class);
        //传送事件
        PendingIntent pi = PendingIntent.getBroadcast(MyApplication.getContext(), arrayList.get(position).getIds(), intent, 0);
        Log.d(TAG,""+ arrayList.get(position).getIds());
        //删除
        am.cancel(pi);
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_lo,menu);

        return true;

    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_newnote:
                Intent intent = new Intent(getApplicationContext(),New_note.class);
                startActivity(intent);
                MainActivity.this.finish();
                break;

            case R.id.menu_exit:
                MainActivity.this.finish();
                break;

            default:

                break;


        }

        return  true;

        //return false;????是用哪个true or false？

    }

}
