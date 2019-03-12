
package geishaproject.demonote;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



import model.Data;

import presenter.DataDao;
import presenter.MyAdapter;
import utils.Backup;
import utils.Constant;
import utils.ZipAndUnzip;

import static utils.Constant.copyFolder;


//version 0.7
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ListView listView;
    FloatingActionButton floatingActionButton;
    LayoutInflater layoutInflater;
    ArrayList<Data> arrayList;
    //保存选择文件的路径
    String path;


    private TextView mAmTvBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.layout_listview);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_note);
        layoutInflater = getLayoutInflater();
        arrayList = DataDao.GetAllDatas();
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
                                DataDao.DeleteDataByIds(arrayList.get(position).getIds());
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
        Intent intent = new Intent(PublicContext.getContext(),AlarmReceiver.class);
        //传送事件
        PendingIntent pi = PendingIntent.getBroadcast(PublicContext.getContext(), arrayList.get(position).getIds(), intent, 0);
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
            case R.id.menu_export:
                mExport();
                break;
            case R.id.menu_import:
                mImport();
                break;

            default:
                break;
        }
        return  true;
        //return false;????是用哪个true or false？
    }

    /**
     * 导出便签包操作
     */
    public void mExport(){
        //获得文件存放的NoteBlocks文件夹路径
        final File oldDir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
        if (!oldDir.exists()) {
            oldDir.mkdirs();
        }
        //创建导出NoteBlocksPack文件夹
        final File newDir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksPack");
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        //将NoteBlocks下的所有文件复制到NoteBlocksPack中
        copyFolder(oldDir.getAbsolutePath(),newDir.getAbsolutePath());
        //生成NoteBlocks.json文件并保存在NoteBlocksPack文件夹中
        Backup.createJsonFile(newDir);
        //创建线程，对NoteBlocksPack进行压缩
        Thread th  = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    //压缩，生成NoteBlocks.zip
                    ZipAndUnzip.zip(newDir.getAbsolutePath(), Environment.getExternalStorageDirectory().getPath()+"/NoteBlocksPack.zip");
                    //删除NoteBlocksPack文件夹及其里面的所有文件
                    Constant.deleteFile(newDir);
                    //Toast.makeText(MainActivity.this, "导出成功！路径为：" + Environment.getExternalStorageDirectory().getPath()+"/NoteBlocksPack.zip", Toast.LENGTH_SHORT).show();

                    //ZipUtil.unzip(Environment.getExternalStorageDirectory().getPath()+"/baidu.zip", Environment.getExternalStorageDirectory().getPath());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        //启动线程
        th.start();

    }


    /**
     * 导入便签包操作
     * 如果导入便签包，则会删除当前所有便签（包括音频图片文件、DataDao及数据库中的所有数据）
     * 再将便签包里的便签添加入数据库
     */
    public void mImport(){
        selectFile();
    }

    /**
     * 调用文件管理器，进行选择文件
     */
    private void selectFile(){

        final File oldDir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
        if (!oldDir.exists()) {
            oldDir.mkdirs();
        }
        //删除所有便签
        DataDao.DeleteDataAllData();
        //删除便签保存的本地文件（图片和音频）
        Constant.deleteFile(oldDir);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
        //Toast.makeText(MainActivity.this, "***", Toast.LENGTH_SHORT).show();
    }

    /**
     * 获得选择文件的路径
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //是否选择，没选择就不会继续
        if (resultCode == Activity.RESULT_OK) {
            //得到uri，后面就是将uri转化成file的过程。
            Uri uri = data.getData();
            //判断版本获取路径的方式，在拿到uri之后进行版本判断大于等于24（即Android7.0）用最新的获取路径方式，否则用之前的方式
            if (Build.VERSION.SDK_INT >= 24) {
                //新的方式
                path = getFilePathFromURI(this, uri);
            } else {
                //旧的方式
                //得到uri，后面就是将uri转化成file的过程。
                //Uri uri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                String img_path = actualimagecursor.getString(actual_image_column_index);
                File file = new File(img_path);
                path = file.toString();
            }

            //Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
            //Log.d("filePath***", file.toString());

            //path = file.toString();

            //创建线程，解压NoteBlocks.zip
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        //ZipUtil.zip(filePath, Environment.getExternalStorageDirectory().getPath()+"/JSON.zip");
                        //创建NoteBlocks文件夹
                        File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        //解压NoteBlocks.zip到NoteBlocks文件夹中
                        ZipAndUnzip.unzip(path, dir.getAbsolutePath());
                        //读取NoteBlocks.json文件中的便签数据
                        Backup.readJsonFile(dir.getAbsolutePath()+"/NoteBlocks.json");
                        /*
                        for(int i= 0; i<DataDao.GetAllDatas().size();i++){
                            Log.d("testDataDao","data： "+DataDao.GetAllDatas().get(i));
                        }
                        */
                        Intent intent=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            //启动线程
            th.start();

        }
    }

    /**
     * 新的方式
     * @param context
     * @param contentUri
     * @return
     */
    public String getFilePathFromURI(Context context, Uri contentUri) {
        File rootDataDir = context.getFilesDir();
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return count;
    }



}
