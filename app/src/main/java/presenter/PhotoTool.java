package presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import geishaproject.demonote.New_note;
import geishaproject.demonote.PublicContext;
import model.Data;

public class PhotoTool extends Activity{
    private static final String TAG = "PhotoTool";
    /**
     * 照片数组,放置照片的特殊字符（啊斌）,相机url,data数据,长，宽
     */
    public static java.util.List<Bitmap> bitmaplist = new java.util.ArrayList<Bitmap>();
    private String specialchar = "a";
    private Uri photoUri;
    private Data data;
    private int width;
    private int height;
    /**
     * 构造函数
     */
    public PhotoTool(Data data){
        this.data = data;

    }
    /**
     * 加载data数据
     */
    public void adddata(Data data){
        this.data = data;
    }
    /**
     * 加载,长宽
     */
    public void addsize(int width,int height ){
        this.width = width;
        this.height = height;
        Log.d(TAG,"长宽："+width+" "+height);
    }

    /**
     * 返回图片数组大小
     */
    public int BitmaplistSize(){
        return bitmaplist.size();
    }
    /**
     * 返回图片数组大小
     */
    public String GetSpecialChar(){
        return specialchar;
    }
    /**
     * 返回图片
     */
    public Bitmap GetBitmap(int i) {
        return bitmaplist.get(i);
    }

    /**
     * 清理缓存，每次打开便签先清，后开
     */
    public void doclear() {
        bitmaplist.clear();
    }
    /**
     * 拿到图片加载进数组
     */
    public void initloadphoto() {
        Log.d(TAG,"getPicturePathArr().size() ："+ data.getPicturePathArr().size());
        for(int i=0;i<data.getPicturePathArr().size();i++){
            try {
                getImg(i);    //重点
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 加载图片进入数组
     */
    public void getImg(int i) throws IOException {
        //根据图片路径和i的值判断是否加载图片
        if(!data.getPicturePath().equals("")) {
            //data.setPicturePathArr();
            String firstPicturePath = data.getPicturePathArr().get(i);
            Log.i("PicturePath**&&&&", firstPicturePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap =  BitmapFactory.decodeFile(firstPicturePath,options);
            bitmaplist.add(bitmap);
            Log.d(TAG,"diaonimadadadada:"+bitmaplist.size());       //观察成功载入否

            /*File file = new File(firstPicturePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();*/
        }
    }

    /**
     * 富文本显示
     */
    public SpannableString GetSpannableString(Bitmap bitmap,String specialchar){
        SpannableString spannableString = new SpannableString(specialchar);
        Log.d(TAG,"diaonma"+width+"////"+height);
        Bitmap imgBitmap = imageScale(bitmap,width,Math.round(height*((float) width/bitmap.getWidth())));
        ImageSpan imgSpan = new ImageSpan(PublicContext.getContext(),imgBitmap, DynamicDrawableSpan.ALIGN_BASELINE);

        spannableString.setSpan(imgSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
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

    /**
     * 获取时间，用作命名
     */
    public String GetcurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = simpleDateFormat.format(date);
        return currentTime;
    }

    /**
     *  保存图片
     */
    public void saveImg(Bitmap bitmap, String name, Context context) {
        try {
            //存放图片文件的名为NoteBlocksPicture的文件夹，，可存放多张图片
            //File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocksPicture");
            //if (!dir.exists()) {
            //    dir.mkdirs();
            //}

            File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dir1 = new File(dir, "picture");
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            //图片文件以当前时间命名，路径为pictureFile.getAbsolutePath()
            File pictureFile = new File(dir1, name);
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
            //Toast.makeText(PhotoTool.this, newPicturePath, Toast.LENGTH_SHORT).show();
            Log.i("PicturePath&&&&", newPicturePath);
            data.cutPicturePath();
            Log.i("******size22*****",""+data.getPicturePathArr().size());

            int checkWriteStoragePermission = ContextCompat.checkSelfPermission(PublicContext.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);//获取系统是否被授予该种权限
            if(checkWriteStoragePermission != PackageManager.PERMISSION_GRANTED){//如果没有被授予
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return ;//请求获取该种权限
            }

            Log.i("SaveImg", "file had" );
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bos);           //compress到输出outputStream
            Uri uri = Uri.fromFile(pictureFile);                                    //获得图片的uri
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
}
