package presenter;

import android.util.Log;

import java.util.ArrayList;

import geishaproject.demonote.PublicContext;
import model.Data;

public class DataDao {
    //逻辑处理时能够取到的数组，与数据库同步更新
    private static ArrayList<Data> allDataArr = MyDatabase.getAllDataArray();

    /**
     * 重新从数据库获取完整的数据集合
     */
    public static void RefreshDatas(){
        allDataArr = MyDatabase.getAllDataArray();
    }

    /**
     * 返回数据库数据同步ArrayList
     * @return ArrayList
     */
    public static ArrayList<Data> GetAllDatas(){
        return allDataArr;
    }

    /**
     * 指定Id获取对应的Data
     * @param ids
     * @return
     */
    public static Data GetDataByIds(int ids){
        //遍历数组取得对应id值的Data
        for(int i=0 ; i< allDataArr.size() ; i++){
            if(ids == allDataArr.get(i).getIds()){
                //若取到了则返回对应Data
                return allDataArr.get(i);
            }
        }
        //若取不到则返回异常标志Data
        return GetErrorData("没有找到对应ids值的数据");
    }

    /**
     * 根据Data中的数据更新数据库后同步更新ArrayList
     * @param data
     * @return
     */
    public static boolean ChangeData(Data data){
        boolean success = false;
        MyDatabase.UpdateData(data);
        for(int i=0 ; i< allDataArr.size() ; i++){
            //根据ids替换对应数据
            if(data.getIds() == allDataArr.get(i).getIds()){
                allDataArr.set(i,data);
                success = true;
                break;
            }
        }
        return success;
    }


    /**
     * 向数据库和ArrayList中同步添加数据
     * @param newData 一个带有新数据的Data模型对象
     * @return 是否成功
     */
    public static boolean AddNewData(Data newData){
        int newDataIds;//记录数据库返回的数据在数据库中自动获取的ids值用于同步设置DataDao类中的ids值
        newDataIds = MyDatabase.InsertNewData(newData);
        //获取ids后同步设置ids
        newData.setIds(newDataIds);
        //从头部插入保证新的标签在上端显示
        allDataArr.add(0,newData);
        return true;
    }

    /**
     * 根据ids删除数据库后删除DataDao内储存的ArrayList中对应的值
     * @param ids
     * @return success值代表是否成功
     */
    public static boolean DeleteDataByIds(int ids){
        boolean success = false;
        MyDatabase.DeleteDataByIds(ids);
        for(int i=0 ; i< allDataArr.size() ; i++){
            //若查到对应ids则删除对应Data
            if(ids == allDataArr.get(i).getIds()){
                allDataArr.remove(i);
                success = true;
                break;
            }
        }
        return success;
    }

    /**
     * 删除数据库中所有数据后删除DataDao内储存的ArrayList中的所有值
     */
    public static void DeleteDataAllData(){
        MyDatabase.DeleteAllData();
        allDataArr.clear();
        /*for(int i=0; i<allDataArr.size() ; i++){
            allDataArr.remove(i);
        }*/
    }


    /**
     * 返回数据库当前ids的最大值
     * @return 数据库当前ids的最大值
     */
    public static int GetMaxIds(){
        return MyDatabase.GetMaxIds();
    }

    /**
     * 异常Data模型，用于处理异常时返回带有异常标记的Data对象
     * @param errorText 异常参数
     * @return
     */
    public static Data GetErrorData(String errorText){
        Data errorData = new Data(-1,"error","error","error:"+errorText,"error","error");
        return errorData;
    }
}
