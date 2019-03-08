
package presenter;



import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;


import model.Data;



public class MyDatabase {

    Context context;

    MyOpenHelper myOpenHelper;

    SQLiteDatabase mydatabase;

    public MyDatabase(Context context){

        this.context = context;
        myOpenHelper = new MyOpenHelper(context);
        mydatabase = myOpenHelper.getWritableDatabase();

    }

    /*
        获取完整的数据库对应数组列表
     */
    public ArrayList<Data> getAllDataArray(){
        //创建与数据库对应的临时变量
        int ids;
        String times,title,content,audioPath,picturePath;
        //创建一个动态数组用于返回给DataDao对象
        ArrayList<Data> allDataArray = new ArrayList<Data>();
        //构建数据库SQL语句
        mydatabase = myOpenHelper.getWritableDatabase();
        Cursor cursor = mydatabase.rawQuery("select ids,times,title,content,audioPath,picturePath from mybook",null);
        // 数据库读取初始化
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            //读取数据库数据到临时变量
            ids = cursor.getInt(cursor.getColumnIndex("ids"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            times = cursor.getString(cursor.getColumnIndex("times"));
            audioPath = cursor.getString(cursor.getColumnIndex("audioPath"));
            picturePath = cursor.getString(cursor.getColumnIndex("picturePath"));
            //将数据构建成Data对象
            Data data = new Data(ids, times, title, content, audioPath, picturePath);
            //将对象存入数据
            allDataArray.add(data);
            //数据库读取下一行
            cursor.moveToNext();
        }
        Collections.reverse(allDataArray);
        return allDataArray;
    }

    /*
        根据Data修改表中所有属性值
     */
    public void fullUpdateDataByData(Data data){           //修改表中所有数据
        mydatabase = myOpenHelper.getWritableDatabase();
        mydatabase.execSQL(
                "update mybook set title='"    + data.getTitle()
                        + "',times='"         + data.getTimes()
                        + "',content='"       + data.getContent()
                        + "',audioPath='"     + data.getAudioPath()
                        + "',picturePath='"   + data.getPicturePath()
                        + "'where ids='"     + data.getIds()
                        + "'"
        );
        mydatabase.close();
    }

    /*
        根据ids来获取音频路径
     */
    public String getAudioPathByIds(int ids){
        mydatabase = myOpenHelper.getWritableDatabase();
        Cursor cursor=mydatabase.rawQuery("select audioPath from mybook where ids='"+ids+"'" , null);
        cursor.moveToFirst();
        String audioPath = cursor.getString(cursor.getColumnIndex("audioPath"));
        mydatabase.close();
        return audioPath;
    }

    /*
        根据Data修改表中音频存储路径
     */
    public void singleUpdateDataApByData(Data data){
        /*
        String OldAudioPath;    //存储数据库中原有的音频路径
        String NewAudioPath;    //创建的新的音频路径
        */
        mydatabase = myOpenHelper.getWritableDatabase();
        /*
        Cursor cursor = mydatabase.rawQuery("select audioPath from mybook where ids='"+data.getIds()+"'" , null);
        // 数据库读取初始化
        cursor.moveToFirst();
        //读取数据库数据中原有的音频路径
        OldAudioPath = cursor.getString(cursor.getColumnIndex("audioPath"));
        NewAudioPath = OldAudioPath + data.getAudioPath();
        */
        mydatabase.execSQL(
                "update mybook set audioPath='" + data.getAudioPath()
                        + "'where ids='"      + data.getIds()
                        + "'"
        );
        mydatabase.close();
    }

    /*
       根据Data对象在表中创建数据
    */
    public int insertNewDataByData (Data data){
        mydatabase = myOpenHelper.getWritableDatabase();
        mydatabase.execSQL(
                " insert into mybook(title,content,times,audioPath,picturePath)values('"
                        + data.getTitle()   +"','"
                        + data.getContent() +"','"
                        + data.getTimes()   +"','"
                        + data.getAudioPath()   +"','"
                        + data.getPicturePath()
                        + "')"
        );







        mydatabase.close();
        return getDatabaseCurrentMaxIds();
    }

    /*
        根据id在表中删除数据
     */
    public void deleteDataByIds(int ids) {
        mydatabase = myOpenHelper.getWritableDatabase();
        mydatabase.execSQL(" delete from mybook where ids=" + ids);
        mydatabase.close();

    }

    /*
        根据数组重写数据库数据
     */
    public void resetTable(ArrayList<Data> newArr) {
        mydatabase = myOpenHelper.getWritableDatabase();
        mydatabase.execSQL("delete from mybook;");
        mydatabase.execSQL("update sqlite_sequence SET seq = 0 where name ='mybook'");
        mydatabase.close();
        for(int i=newArr.size()-1;i>=0;i--) {
            insertNewDataByData(newArr.get(i));
        }
    }
    public int getDatabaseCurrentMaxIds(){
        int maxIds=-1;
        mydatabase = myOpenHelper.getWritableDatabase();
        Cursor cursor = mydatabase.rawQuery("select max(ids) from mybook",null);
        cursor.moveToFirst();
        maxIds = cursor.getInt(cursor.getColumnIndex("max(ids)"));
        Log.d("max(ids)","getDatabaseCurrentMaxIds: "+maxIds);
        mydatabase.close();
        return maxIds;
    }
}
