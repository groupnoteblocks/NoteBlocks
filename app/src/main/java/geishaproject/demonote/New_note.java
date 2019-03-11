
package geishaproject.demonote;



import android.Manifest;
import android.annotation.TargetApi;
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
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.view.ViewTreeObserver;
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


import manager.AudioRecordButton;
import manager.MediaManager;
import model.Data;

import presenter.DataDao;
import presenter.PhotoTool;
import utils.PermissionHelper;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;


public class New_note extends AppCompatActivity implements ViewTreeObserver.OnPreDrawListener {
    /*  界面部分  */
    private static final String TAG = "New_note";//Log调试
    EditText ed_title;    //标题和内容文本框
    EditText ed_content;
    FloatingActionButton floatingActionButton;  //右下角按钮
    Data data;    //当前界面读取到的数据
    PhotoTool mPhotoTool;       //图像工具类
    static java.util.List<Bitmap> bitmaplist = new java.util.ArrayList<Bitmap>();       ////照片数组
    String specialchar = "a";       //放置照片的特殊字符
    Uri photoUri;                   //相机url

    /*  相片部分  */
    private static int REQUSET_CODE = 1;//请求码，判断是哪个回传的请求
    private int mIndex = 0;

    /*  录音部分  */
    private Button StartRecord,StopRecord,PlayRecord;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String voicePath;  //录音文件路径（旧版本）
    private long time;   //录音时长（旧版本）
    File dir; //文件操作（旧版本）
    private AudioRecordButton mEmTvBtn;
    Record mRecords;
    PermissionHelper mHelper; //录音权限

    /*  闹钟部分  */
    Random rand = new Random();
    int times = rand.nextInt(10000);//闹钟唯一标识


    /*  活动入口  */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        init();              //活动初始化
        addClickListenenr(); //添加活动的所有监听事件
    }




    /**
     * 活动相关初始化，在这里加入模块初始化
     */
    private void init(){
        floatingActionButton = (FloatingActionButton) findViewById(R.id.finish); //实例化右下角按钮控件
        initAudio(); //初始化录音功能模块
        initPhoto(); //初始化拍照功能模块
        initDataModel(); //初始化数据模型
        initEditText();  //初始化文本编辑框
        Log.d("initFinish","width:"+ed_content.getWidth()+",text: "+ed_content.getText());
    }

    /**
     * 添加活动的所有监听事件，在这里加入添加模块点击事件函数
     */
    private void addClickListenenr(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {  //为悬浮按钮设置监听事件
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        addAudioListener(); //添加录音点击事件

    }


    /**
     * 初始化数据模型
     */
    public void initDataModel(){
        Intent intent = this.getIntent();  //获取上一个活动传来的intent
        int idsFlag = intent.getIntExtra("ids", 0); //根据上一个活动传过来的intent中的数据判断新建与修改
        if (idsFlag != 0) {    //根据data的ids判断是新建还是读写，如果是读写，则显示对应数据
            data = DataDao.GetDataByIds(idsFlag);
            mPhotoTool = new PhotoTool(data);

        }else{                       //如果是新建，则创建一个新的数据模型
            data=new Data(0,"","","","","");
            mPhotoTool = new PhotoTool(data);
        }
    }

    /**
     * 初始化文本编辑框
     */
    private void initEditText(){
        ed_title = (EditText) findViewById(R.id.title);    //实例化文字编辑框
        ed_content = (EditText) findViewById(R.id.content);

        ed_title.setText(data.getTitle());  //获取对应的值
        mPhotoTool.doclear();
        mPhotoTool.initloadphoto();

        initifphotohave();
    }



    /**
     * 初始化有图片的文字/没图片处理在最下面的else
     */
    private void initifphotohave() {

        //用下面函数可以将控件宽高提前拿到
        ed_content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                ed_content.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                mPhotoTool.addsize(ed_content.getWidth(),ed_content.getHeight());
                String world = data.getContent();
                String now = world;         //还有的文本
                //全部文本
                int startindex=0;      //要替换图片的位置
                int endindex=0;          //要替换图片的位置
                Log.d(TAG,"btsize:"+mPhotoTool.BitmaplistSize());
                if (mPhotoTool.BitmaplistSize()>0)
                    for (int i=0; i<mPhotoTool.BitmaplistSize();i++){
                        //数据定义
                        Log.d(TAG,""+i);
                        String show;        //要放上去的文本
                        //找到要替换的特殊字符位置
                        endindex = now.indexOf(mPhotoTool.GetSpecialChar());
                        //切割子文本
                        show = now.substring(0,endindex);
                        now = now.substring(endindex+1);
                        //输出文本
                        ed_content.append(show);
                        //输出图片，GetSpannableString 富文本操作
                        SpannableString spannableString = GetSpannableString(mPhotoTool.GetBitmap(i),specialchar);
                        ed_content.append(spannableString);

                    }
                else
                    ed_content.setText(data.getContent());

            }
        });

    }

    /**
     * 将大图片窗口化压缩
     */
    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }


    /*  录音  */
    /**
     * 初始化录音模块
     */
    private void initAudio() {
        player = new MediaPlayer();   //实例化录音控件
        PlayRecord = (Button) findViewById(R.id.PlayBtn);  //播放录音按钮
        mEmTvBtn = (AudioRecordButton) findViewById(R.id.em_tv_btn);
    }

    /**
     * 添加录音点击事件监听
     */
    private void addAudioListener() {
        PlayRecord.setOnClickListener(new View.OnClickListener() {        //播放录音点击事件
            @Override
            public void onClick(View v) {
                PlayR();
            }
        });

        mEmTvBtn.setHasRecordPromission(false);
        //授权处理
        mHelper = new PermissionHelper(this);

        mHelper.requestPermissions("请授予[录音]、[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        mEmTvBtn.setHasRecordPromission(true);

                        mEmTvBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String filePath) {
                                Record recordModel = new Record();
                                recordModel.setSecond((int) seconds <= 0 ? 1 : (int) seconds);
                                recordModel.setPath(filePath);
                                recordModel.setPlayed(false);
                                mRecords = recordModel;
                                //如果当前便签存在音频文件，则先删除原有音频文件
                                if(!data.getAudioPath().equals("")){
                                    deleteSingleFile(data.getAudioPath());
                                }
                                //音频文件路径存入data中
                                data.setAudioPath(mRecords.getPath());
                                Toast.makeText(New_note.this, "录音保存成功！时长："+mRecords.getSecond()+"s", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        mEmTvBtn.setHasRecordPromission(false);
                        Toast.makeText(New_note.this, "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    /**
     * 直接把参数交给mHelper就行了
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 终止语音播放
     */
    @Override
    protected void onPause() {
        MediaManager.release();//保证在退出该页面时，终止语音播放
        super.onPause();
    }

    /**
     * 播放录音
     */
    private void PlayR () {
        if(!data.getAudioPath().equals("")) {
            if (player != null) {
                player.reset();
                try {
                    Toast.makeText(New_note.this, data.getAudioPath(), Toast.LENGTH_SHORT).show();
                    player.setDataSource(data.getAudioPath()); //获取录音文件
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        else{
            Toast.makeText(New_note.this, "还未曾录音", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据文件路径删除对应文件
     * @param filePath 文件路径
     */
    private static void deleteSingleFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }

    /*  拍照部分  */
    /**
     * 初始化拍照模块
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initPhoto() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    /**
     *  相机拍完照之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSET_CODE) {
            if (resultCode == Activity.RESULT_OK ) {    //&& data != null 旧版条件，data缩略图
                //说明成功返回
                Uri uri = null;
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                }
                if (uri == null) {
                    if (photoUri != null) {
                        uri = photoUri;
                    }
                }
                //图片的Uri转Bitmap
                Bitmap result = null;
                try {
                    result = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (result != null) {
                    SpannableString spannableString = GetSpannableString(result,specialchar);
                    ed_content.append(spannableString);
                    String currentTime = mPhotoTool.GetcurrentTime();
                    //保存图片
                    mPhotoTool.saveImg(result,currentTime+".jpg",this);
                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                //说明取消或失败了
                Toast.makeText(this,"您取消了拍照！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 富文本自定义函数
     */
    public SpannableString GetSpannableString(Bitmap bitmap,String specialchar){
        SpannableString spannableString = new SpannableString(specialchar);
        Log.d(TAG,"diaonma"+ed_content.getWidth()+"////"+ed_content.getHeight());
        Bitmap imgBitmap = imageScale(bitmap,ed_content.getWidth(),Math.round(bitmap.getHeight()*((float) ed_content.getWidth()/bitmap.getWidth())));

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        Log.d(TAG,"大小为（m）："+imgBitmap.getByteCount() / 1024 / 1024+"宽度为" + imgBitmap.getWidth() + "高度为" + imgBitmap.getHeight());
        ImageSpan imgSpan = new ImageSpan(PublicContext.getContext(),imgBitmap, DynamicDrawableSpan.ALIGN_BASELINE);
        spannableString.setSpan(imgSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /*  闹钟部分  */

    /**
     * 添加闹钟函数
     */
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
                            pi = PendingIntent.getBroadcast(PublicContext.getContext(), DataDao.GetMaxIds(), intent, 0);
                            System.out.print( DataDao.GetMaxIds());
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

    /*  系统界面点击功能等  */
    /**
     * 重写返回建方法，如果是属于新建则插入数据表并返回主页面，如果是修改，修改表中数据并返回主页面
     */
    @Override
    public void onBackPressed() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd   HH:mm");//编辑便签的时间，格式化
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);

        data.setTimes(time);    //给当前data更新数据,如果有录音和拍照数据，应该在对应的过程中调用data.setXXX
        data.setTitle(ed_title.getText().toString());
        data.setContent(ed_content.getText().toString());

        if(data.getIds()!=0){ //根据data修改数据库
            DataDao.ChangeData(data);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        } else{  //根据data新建数据
            DataDao.AddNewData(data);
            Intent intent=new Intent(New_note.this,MainActivity.class);
            startActivity(intent);
            New_note.this.finish();
        }
    }

    /**
     * 设置右上角菜单的点击事件
     * @param item
     * @return
     */
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
                //创建图片文件需要用到时间字符串
                /*File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksPicture");
                if (!dir.exists()) {
                    dir.mkdirs();
                }*/

                File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File dir1 = new File(dir, "picture");
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }

                //图片文件名
                String name = mPhotoTool.GetcurrentTime() + ".jpg";
                File pictureFile = new File(dir1, name);
                if (!pictureFile.exists()) {
                    try {
                        pictureFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoUri = Uri.fromFile(new File(String.valueOf(pictureFile)));
                Log.d(TAG," **!!**"+dir1 + String.valueOf(pictureFile));
                intents.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intents, REQUSET_CODE);

                break;
            case R.id.add_clock:    //添加闹钟功能
                addclock();         //函数包装
                break;
            default:
                break;
        }
        return false;
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_lo,menu);
        return true;
    }


    @Override
    public boolean onPreDraw() {
        return false;
    }
}
