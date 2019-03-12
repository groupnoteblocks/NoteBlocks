package presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import geishaproject.demonote.PublicContext;
import model.Data;

public class PhotoTool extends Activity{
    private static final String TAG = "PhotoTool";
    /**
     * 照片数组,放置照片的特殊字符（啊斌）,相机url,data数据,长，宽
     */
    public static java.util.List<Bitmap> bitmaplist = new java.util.ArrayList<Bitmap>();
    ArrayList<String> pictureName = new ArrayList<String>();     //存放分割好的图片路径
    private String specialchar = "a";
    private Uri photoUri;
    private Data data;
    private int width;
    private int height;
    /**
     * 构造函数
     */
    public PhotoTool(Data data){ this.data = data; }
    /**
     * 加载data数据
     */
    public void adddata(Data data){
        this.data = data;
    }
    /**
     * 加载,长宽
     */
    public void addsize(int width,int height ){ this.width = width;this.height = height;Log.d(TAG,"长宽："+width+" "+height); }

    /**
     * 返回图片数组大小
     */
    public int BitmapAdressSize(){
        return pictureName.size();
    }
    /**
     * 返回图片
     */
    public Bitmap GetBitmap(int i) {
        return bitmaplist.get(i);
    }
    /**
     * 返回图片名称
     */
    public String GetBitmapNmae(int i) {
        return pictureName.get(i);
    }
    /**
     * 清理缓存，每次打开便签先清，后开
     */
    public void doclear() {
        pictureName.clear();
    }
    /**
     * 清理图片
     */
    public void delete(int i){
        pictureName.remove(i);
        Log.d(TAG,"成功删除 "+i);
    }
    /**
     * 准备图片地址
     */
    public void readyAdress() {
        for (int i = 0; i < data.getPicturePathArr().size(); i++) {
            if (!data.getPicturePath().equals("")) {
                //data.setPicturePathArr();
                String firstPicturePath = data.getPicturePathArr().get(i);
                Log.i("PicturePath**&&&&", firstPicturePath);
                pictureName.add(firstPicturePath);      //加载地址
                Log.d(TAG, "diaonimadadadada:" + pictureName.size());       //观察成功载入否
            }
        }
    }
    /**
     * 通过地址获取图片
     */
    public Bitmap getBitmapForAdress(String adress){
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = 2;
        Bitmap bitmap =  BitmapFactory.decodeFile(adress);
        return bitmap;
    }
    /**
     * 通过地址删除图片
     */
    public void deleteBitmapForAdress(String adress){
        deleteSingleFile(adress);
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
     * 获取文件前缀
     */
    public String getAdress() {
        String name = GetcurrentTime()+".jpg" ;
        File dir = new File(Environment.getExternalStorageDirectory(), "NoteBlocks");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dir1 = new File(dir, "picture");
        if (!dir1.exists()) {
            dir1.mkdirs();
        }
        File pictureFile = new File(dir1, name);
        String adress=pictureFile.toString();
        return adress;
    }
    /**
     *  保存图片
     */
    public void saveImg(Bitmap bitmap,Context context) {
        //清除原本的记录，按最后的结果来
        //data.getPicturePathArr().clear();
        //打开输入流
        try {
                //图片文件以当前时间命名，路径为pictureFile.getAbsolutePath()
                File pictureFile = new File(getAdress());      //dir1, name
                pictureName.add(pictureFile.toString());
                if (!pictureFile.exists()) {
                    try {
                        pictureFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                BufferedOutputStream bos = null;
                //拼接的路径重新存入data中
                Log.i("******size22*****",""+data.getPicturePathArr().size());
                Log.i("SaveImg", "file had" );
                bos = new BufferedOutputStream(new FileOutputStream(pictureFile));      //BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bos);           //compress到输出outputStream
                Uri uri = Uri.fromFile(pictureFile);                                    //获得图片的uri
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); //发送广播通知更新图库，这样系统图库可以找到这张图片
                Log.i("SaveImg", "file ok" );
                bos.flush();
                bos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }
    /**
     * 保存图片地址到数据库
     */
    public void saveToData() {
        //清除原本的记录，按最后的结果来
        data.getPicturePathArr().clear();
        Log.d(TAG, "保存图片时，大小：" + pictureName.size());
        for (int i = 0; i < pictureName.size(); i++) {
            //图片文件以当前时间命名，路径为pictureFile.getAbsolutePath()
            File pictureFile = new File(pictureName.get(i));
            if (!pictureFile.exists()) {
                try {
                    pictureFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String newPicturePath = data.getPicturePath() + pictureFile.getAbsolutePath() + "?";
            //拼接的路径重新存入data中
            data.setPicturePath(newPicturePath);
            data.cutPicturePath();
            Log.i("PicturePath&&&&", newPicturePath);

        }
    }
    /**
     * 删除实际本地文件
     */
    private static void deleteSingleFile(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }
    /**
     * 为斌哥存bug----text view
     * android:paddingTop="4dp"
     android:paddingBottom="50dp"
     android:paddingLeft="7dp"
     android:paddingRight="4dp"
     android:paddingBottom="10dp"
     android:paddingLeft="7dp"
     android:paddingRight="4dp"
     android:paddingTop="4dp"
     */
}
