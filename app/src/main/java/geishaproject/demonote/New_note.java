
package geishaproject.demonote;



import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import geishaproject.demonote.R;
import model.Data;

import presenter.DataDao;
import presenter.MyDatabase;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;


public class New_note extends AppCompatActivity {
    private static final String TAG = "New_note";//Log调试
    //标题和内容文本框
    EditText ed_title;
    EditText ed_content;
    //右下角按钮
    FloatingActionButton floatingActionButton;
    //工具类
    DataDao dataDao;
    //当前
    Data data;
    //用于接受活动传过来的ids值以便读取对应数据
    /*
        >0 --- 数据库已有对应数据
        0 --- 新建
        -1 --- 未找到对应id
     */
    int ids;

    //相片部分
    //相片控件数组
    private int[] box = {
            R.id.take_photo_container1,
            R.id.take_photo_container2,
            R.id.take_photo_container3
    };
    private static int REQUSET_CODE = 1;//请求码，判断是哪个回传的请求
    private int mIndex = 0;
    //ImageView mPhotoBtn;
    ImageView mResultContainer;

    //录音部分
    private Button StartRecord,StopRecord,PlayRecord;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String voicePath;  //录音文件路径
    private long time;  //录音时长
    File dir; //文件操作

    Random rand = new Random();
    int times = rand.nextInt(10000);//闹钟唯一标识
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        //实例化操作
        //实例化两个文字控件
        ed_title = (EditText) findViewById(R.id.title);
        ed_content = (EditText) findViewById(R.id.content);
        //实例化右下角按钮控件
        floatingActionButton = (FloatingActionButton) findViewById(R.id.finish);
        //实例化工具类
        dataDao = new DataDao(this);
        //实例化录音控件
        player = new MediaPlayer();
        StartRecord = (Button) findViewById(R.id.StartBtn);
        StopRecord = (Button) findViewById(R.id.StopBtn);
        PlayRecord = (Button) findViewById(R.id.PlayBtn);
        dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksAudio");

        //初始化拍照功能
        initView();

        //获取上一个活动传来的intent
        Intent intent = this.getIntent();
        ids = intent.getIntExtra("ids", 0);
        //根据ids判断是新建还是读写，如果是读写，则显示对应数据
        if (ids != 0) {
            data = dataDao.getDataByIds(ids);
            ed_title.setText(data.getTitle());
            ed_content.setText(data.getContent());
            //读取图片，对图片路径进行分割
            data.setPicturePathArr();
            Log.i("******size*****",""+data.getPicturePathArr().size());
            //最多加载3张图片（最后拍的3张）
            for(int i=data.getPicturePathArr().size();i>data.getPicturePathArr().size()-3;i--){
                try {
                    getImg(i-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }else{
            data=new Data(0,"","","","","");
        }


        //录音操作
        if (!dir.exists()) {
            dir.mkdirs();
        }
        voicePath = dir.getAbsolutePath();

        //绑定监听事件
        //开始录音点击事件
        StartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartR();
            }
        });
        //结束录音点击事件
        StopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopR();
            }
        });
        //播放录音点击事件
        PlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayR();
            }
        });
        //为悬浮按钮设置监听事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /*
        录音部分
     */
    //开始录音，保存为amr格式
    private void StartR () {
        if (recorder == null) {
            //存放录音文件的名为NoteBlocksAudio的文件夹，，可存放多个音频
            File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksAudio");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //创建录音文件需要用到时间字符串
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            String currentTime = simpleDateFormat.format(date);

            //音频文件以当前时间命名，路径为soundFile.getAbsolutePath()
            File soundFile = new File(dir, currentTime + ".amr");
            if (!soundFile.exists()) {
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Toast.makeText(New_note.this, soundFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            //取出data中原有路径，对新路径加?，将原有路径与新路径拼接起来
            String newAudioPath = data.getAudioPath()+soundFile.getAbsolutePath()+"?";
            //拼接的路径重新存入data中
            data.setAudioPath(newAudioPath);

            Toast.makeText(New_note.this, newAudioPath, Toast.LENGTH_SHORT).show();

            Log.i("AudioPath*", newAudioPath);


            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源，设置麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            recorder.setOutputFile(soundFile.getAbsolutePath());  //输出路径

            voicePath = dir.getAbsolutePath();
            try {
                recorder.prepare();  //准备
                recorder.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        time = System.currentTimeMillis();  //获取开始录音时间
    }
    //结束录音
    private void StopR () {
        recorder.stop();
        long time2 = System.currentTimeMillis() - time; //计算录音时长ms
        if (time2 < 1000) {      //时长短语1秒则删除
            File file = new File(voicePath+Integer.toString(ids));
            if (file.exists()) {
                file.delete();
                Toast.makeText(New_note.this, "录音时间过短", Toast.LENGTH_SHORT).show();
            }
        }
        //将data中的路径进行分割并存入data中的audioPathArr中
        data.setAudioPathArr();
        //String audioPath1 = data.getAudioPath();
        String audioPath = "";
        //获取audioPathArr中的单个音频路径个数
        int i = data.getAudioPathArr().size()-1;
        //尝试最后一个音频
        audioPath = data.getAudioPathArr().get(i);
        Log.i("AudioPath**", audioPath);
        Toast.makeText(New_note.this, "录音保存成功,时长：" + time2 + "ms" + audioPath, Toast.LENGTH_SHORT).show();

        //重置
        recorder.release();
        if (recorder != null) {
            recorder.release();
            recorder = null;
            System.gc();
        }
    }

    //播放录音
    private void PlayR () {
        if(!data.getAudioPath().equals("")) {
            if (player != null) {
                player.reset();
                try {

                    //String audioPath1 = data.getAudioPath();
                    String audioPath = "";
                    //尝试播放最后一个音频
                    int i = data.getAudioPathArr().size() - 1;
                    audioPath = data.getAudioPathArr().get(i);
                    Toast.makeText(New_note.this, audioPath, Toast.LENGTH_SHORT).show();

                    player.setDataSource(audioPath); //获取录音文件
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /*
        拍照部分
     */
    //初始化图片拍照存储
    private void initView() {
        mResultContainer = this.findViewById(R.id.take_photo_container1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSET_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                //说明成功返回
                Bitmap result = data.getParcelableExtra("data");
                if (result != null) {
                    int id = mIndex;
                    createPhoto();
                    mResultContainer.setImageBitmap(result);

                    //创建图片文件需要用到时间字符串
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date date = new Date(System.currentTimeMillis());
                    String currentTime = simpleDateFormat.format(date);

                    saveImg(result,currentTime+".jpg",this);
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                //说明取消或失败了
                Toast.makeText(this,"您取消了拍照！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPhoto() {
        mResultContainer = this.findViewById(box[mIndex]);
        mIndex++;
        if (mIndex>2){
            mIndex=0;
        }
    }
    public void saveImg(Bitmap bitmap, String name, Context context) {
        try {
            //存放图片文件的名为NoteBlocksPicture的文件夹，，可存放多张图片
            File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksPicture");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //图片文件以当前时间命名，路径为pictureFile.getAbsolutePath()
            File pictureFile = new File(dir, name);
            if (!pictureFile.exists()) {
                try {
                    pictureFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String newPicturePath = data.getPicturePath()+pictureFile.getAbsolutePath()+"?";
            //拼接的路径重新存入data中
            data.setPicturePath(newPicturePath);

            Toast.makeText(New_note.this, newPicturePath, Toast.LENGTH_SHORT).show();

            Log.i("PicturePath&&&&", newPicturePath);
            data.setPicturePathArr();
            Log.i("******size22*****",""+data.getPicturePathArr().size());
            //////////////////////////////////////////
            //String sdcardPath = System.getenv("EXTERNAL_STORAGE");      //获得sd卡路径
            //String dir = sdcardPath + "/Download/ ";
            //File file = new File(dir+name);
            FileOutputStream out = null;
            //////////////////////////////////////////

            int checkWriteStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//获取系统是否被授予该种权限
            if(checkWriteStoragePermission != PackageManager.PERMISSION_GRANTED){//如果没有被授予
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return ;//请求获取该种权限
            }else{
                Toast.makeText(context,"请授权1~",Toast.LENGTH_SHORT).show();//定义好的获取权限后的处理的事件
            }
            //////////////////////////////////////////
            //if (!file.exists()) {
                // 先得到文件的上级目录，并创建上级目录，在创建文件
            //   Log.i("SaveImg", "file no have" );
            //    file.getParentFile().mkdir();
            //    file.createNewFile();
            //    Log.i("SaveImg", "file create" );
            //}
            //////////////////////////////////////////
            Log.i("SaveImg", "file had" );
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bos);  //compress到输出outputStream
            Uri uri = Uri.fromFile(pictureFile);                                  //获得图片的uri
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); //发送广播通知更新图库，这样系统图库可以找到这张图片
            bos.flush();
            bos.close();
            Log.i("SaveImg", "file ok" );
            return ;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }


    /*
        显示图片
     */
    public void getImg(int i) throws IOException {
        //根据图片路径和i的值判断是否加载图片
        if(!data.getPicturePath().equals("")&&i>=0) {
            //data.setPicturePathArr();
            String firstPicturePath = data.getPicturePathArr().get(i);
            Log.i("PicturePath**&&&&", firstPicturePath);

            File file = new File(firstPicturePath);

            //////////////////////////////////////////
            //String sdcardPath = System.getenv("EXTERNAL_STORAGE");      //获得sd卡路径
            //String dir = sdcardPath + "/Download/ ";
            //File file = new File(dir+name);
            //////////////////////////////////////////
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            createPhoto();
            mResultContainer.setImageBitmap(bitmap);
        }
    }


    /*
        系统界面点击功能等
     */
    //重写返回建方法，如果是属于新建则插入数据表并返回主页面，如果是修改，修改表中数据并返回主页面
    @Override
    public void onBackPressed() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm");//编辑便签的时间，格式化
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        //给当前data更新数据,如果有录音和拍照数据，应该在对应的过程中调用data.setXXX
        data.setTimes(time);
        data.setTitle(ed_title.getText().toString());
        data.setContent(ed_content.getText().toString());
        Log.d("backIDS", "onBack: "+ids);
        if(ids!=0){
            //根据data修改数据库
            dataDao.fullChangeDataByData(data);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
        //新建日记
        else{
            //根据data在数据库新建
            dataDao.addNewDataByData(data);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_lo,menu);
        return true;
    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_share :  //分享功能
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,//分享类型设置为文本型
                        "标题："+ed_title.getText().toString()+"    " +
                                "内容："+ed_content.getText().toString());
                startActivity(intent);
                break;
            case R.id.add_Photo:    //添加照片功能
                Intent intentAddPhoto =new Intent();
                intentAddPhoto.setAction("android.media.action.IMAGE_CAPTURE");
                intentAddPhoto.addCategory("android.intent.category.DEFAULT");
                startActivityForResult(intentAddPhoto, REQUSET_CODE);
                break;
            case R.id.add_clock:    //添加闹钟功能
                addclock();         //函数包装
                break;
            default:
                break;
        }
        return false;

    }

    //添加闹钟功能函数
    private void addclock() {
        times++;    //唯一标识改变
        //设置日期数据
        final Calendar calendar = Calendar.getInstance() ;//取得Calender对象
        calendar.setTimeInMillis(System.currentTimeMillis());
        //临时变量
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        //获取现在时间，单位;毫秒
        final long   nowTime  = calendar.getTimeInMillis();//这是当前的时间
        //用户选择的日期数据
        final Calendar hh = Calendar.getInstance();

        //年月日选择工具
        DatePickerDialog datePickerDialog = new DatePickerDialog(New_note.this,THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                //将选择数值传给日期数据 hh
                hh.set(Calendar.YEAR,year);
                hh.set(Calendar.MONTH,month);
                hh.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                //小时，分钟选择工具
                TimePickerDialog dialog = new TimePickerDialog(New_note.this,THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        hh.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        hh.set(Calendar.MINUTE, minute);

                        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm").format(new java.util.Date(hh.getTimeInMillis()));

                        //↑已获取完用户输入

                        //创建AlarmManager提醒器
                        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);//获取AlarmManager实例
                        //建立广播对象
                        Intent intent = new Intent(New_note.this,AlarmReceiver.class);
                        //传送标题，在提醒时显示
                        intent.putExtra("title","标题："+ed_title.getText().toString());
                        //Log.d(TAG,data.getTitle() + ed_title.getText().toString()); 调试
                        //传送事件
                        PendingIntent pi = null;
                        if(data.getIds()==0){
                            pi = PendingIntent.getBroadcast(MyApplication.getContext(), dataDao.getDatabaseMaxIds(), intent, 0);
                            System.out.print( dataDao.getDatabaseMaxIds());
                        }else {
                            pi = PendingIntent.getBroadcast(New_note.this, data.getIds(), intent, 0);
                            Log.d(TAG,""+data.getIds());
                        }

                        //判断时间是否是未来
                        long a = hh.getTimeInMillis()-nowTime;
                        if (a>0){
                            Toast.makeText(getApplicationContext(),"成功设置闹钟提醒",Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+a, pi);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                alarm.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+a, pi);//用户设置的时间hh.getTimeInMillis()
                                //Toast.makeText(getApplicationContext(),"测试",Toast.LENGTH_SHORT).show(); 高版本
                            } else
                                alarm.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+a, pi);//开启提醒,立即测试：System.currentTimeMillis()
                        } else
                            Toast.makeText(getApplicationContext(),"失败，提醒时间要在将来哟~",Toast.LENGTH_SHORT).show();

                    }
                },hour,minute,true);
                dialog.show();

            }
        },mYear,mMonth,mDay);
        //选择窗口显示
        datePickerDialog.show();
    }

}
