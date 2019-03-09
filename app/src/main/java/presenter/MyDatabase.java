package presenter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Collections;
import geishaproject.demonote.PublicContext;
import model.Data;
public class MyDatabase {
    private static MyOpenHelper myOpenHelper = new MyOpenHelper(PublicContext.getContext());
    //数据库配置信息
    private static String DB_TABLE_NAME = "noteblocks";

    /**
     * 每次调用从数据库读取数据，返回数据库数据对应的ArrayList
     * @return 包含数据库所有数据对应Data模型对象的ArrayList
     */
    public static ArrayList<Data> getAllDataArray(){
        //创建与数据库对应的临时变量
        int ids;
        String times,title,content,audioPath,picturePath;
        ArrayList<Data> allDatas = new ArrayList<Data>();

        //获取数据库以及执行SQL语句
        String SQL_SEARCH_ALL_DATA = "select ids,times,title,content,audioPath,picturePath from "+DB_TABLE_NAME;
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        Cursor cursor = sqlDatabase.rawQuery(SQL_SEARCH_ALL_DATA,null);

        // 数据库循环读取
        cursor.moveToFirst(); //将游标指至开始
        while(!cursor.isAfterLast())
        {
            //读取数据库数据到临时变量
            ids = cursor.getInt(cursor.getColumnIndex("ids"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            times = cursor.getString(cursor.getColumnIndex("times"));
            audioPath = cursor.getString(cursor.getColumnIndex("audioPath"));
            picturePath = cursor.getString(cursor.getColumnIndex("picturePath"));

            //将数据存入数组
            Data data = new Data(ids, times, title, content, audioPath, picturePath);
            allDatas.add(data);

            cursor.moveToNext(); //将游标指至下一行
        }

        Collections.reverse(allDatas); //数组反转，因为便签显示逻辑与时间顺序相反

        return allDatas;
    }

    /**
     * 根据Data修改数据库表中所有属性值
     * @param data 一个Data模型的对象
     */
    public static void UpdateData(Data data){
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String SQL_UPDATE_DATA = "update "+DB_TABLE_NAME+" set title='"  + data.getTitle()
                                        + "',times='"         + data.getTimes()
                                        + "',content='"       + data.getContent()
                                        + "',audioPath='"     + data.getAudioPath()
                                        + "',picturePath='"   + data.getPicturePath()
                                        + "'where ids='"      + data.getIds()
                                        + "'";

        sqlDatabase.execSQL(SQL_UPDATE_DATA);
        sqlDatabase.close();
    }

    /**
     * 根据Data对象在数据库表中创建数据
     * @param data
     * @return 返回插入数据后当前数据库ids的最大值（也就是该对象从数据库自动获取的ids值）
     */
    public static int InsertNewData (Data data){
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String SQL_INSERT_DATA =  " insert into "+DB_TABLE_NAME+" (title,content,times,audioPath,picturePath)values('"
                                + data.getTitle()   +"','"
                                + data.getContent() +"','"
                                + data.getTimes()   +"','"
                                + data.getAudioPath()   +"','"
                                + data.getPicturePath()
                                + "')";

        sqlDatabase.execSQL(SQL_INSERT_DATA);
        sqlDatabase.close();

        return GetMaxIds();
    }

    /**
     * 根据id在表中删除数据
     * @param ids
     */
    public static void DeleteDataByIds(int ids) {
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String SQL_DELETE_BY_IDS = " delete from "+DB_TABLE_NAME+" where ids=" + ids;

        sqlDatabase.execSQL(SQL_DELETE_BY_IDS);
        sqlDatabase.close();
    }

    /**
     * 删除表中所有数据
     */
    public static void DeleteAllData(){
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String SQL_DELETE_ALL_DATA = " delete from " + DB_TABLE_NAME;

        sqlDatabase.execSQL(SQL_DELETE_ALL_DATA);
        sqlDatabase.close();
    }



    /**
     * 根据newArr中的Data将数据库中的表清空，并且更新值为成newArr中的值
     * @param newArr 储存Data模型的ArrayList
     */
    public static void ResetTable(ArrayList<Data> newArr) {
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String EMPTY_TABLE = "delete from "+DB_TABLE_NAME;
        String UPDATE_TABLE_ROWID = "update sqlite_sequence SET seq = 0 where name ='"+DB_TABLE_NAME+"'";
        //sqlite_sequence表也是SQLite的系统表。该表用来保存其他表的RowID的最大值。数据库被创建时,sqlite_sequence表会被自动创建


        sqlDatabase.execSQL(EMPTY_TABLE);
        sqlDatabase.execSQL(UPDATE_TABLE_ROWID);
        sqlDatabase.close();

        //将newArr中的数据全部插入数据库
        for(int i=newArr.size()-1;i>=0;i--) {
            InsertNewData(newArr.get(i));
        }
    }

    /**
     * 返回当前数据库中ids的最大值
     * @return 数据库中ids的最大值整数
     */
    public static int GetMaxIds(){
        int maxIds=0;
        SQLiteDatabase sqlDatabase = myOpenHelper.getWritableDatabase();
        String SQL_SEARCH_MAX_IDS = "select max(ids) from "+DB_TABLE_NAME;

        Cursor cursor = sqlDatabase.rawQuery("select max(ids) from "+DB_TABLE_NAME,null);
        cursor.moveToFirst();
        maxIds = cursor.getInt(cursor.getColumnIndex("max(ids)"));
        sqlDatabase.close();

        return maxIds;
    }

    /**
     * 修改当前表名，使所有数据库操作对应表改变
     * @param dbTableName 修改后的表名
     */
    public static void SetDbTableName(String dbTableName) {
        DB_TABLE_NAME = dbTableName;
    }

    /**
     * 获取当前表名
     * @return 返回当前表名
     */
    public static String GetDbTableName(){
        return DB_TABLE_NAME;
    }
}
