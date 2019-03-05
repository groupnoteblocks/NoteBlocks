package presenter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import geishaproject.demonote.MainActivity;
import model.Data;

public class DataDao {
    Context context;

    ArrayList<Data> allDataArr;

    MyDatabase myDatabase;

    public DataDao(Context context ){
        this.myDatabase = new MyDatabase(context);
        allDataArr = myDatabase.getAllDataArray();
    }

    /*
        异常处理Data模型
     */
    public Data getErrorData(String errorText){
        Data errorData = new Data(-1,"error","error","error:"+errorText,"error","error");
        return errorData;

    }

    /*
        返回一个完整数据库属性的数组
     */
    public ArrayList<Data> getAllDataArr(){
        return allDataArr;
    }


    /*
        指定Id获取对应的Data
     */
    public Data getDataByIds(int ids){
        //遍历数组取得对应id值的Data
        for(int i=0 ; i< allDataArr.size() ; i++){
            if(ids == allDataArr.get(i).getIds()){
                //若取到了则返回对应Data
                return allDataArr.get(i);
            }
        }
        //若取不到则返回异常标志Data
        return getErrorData("没有找到对应ids值的数据");
    }

    /*
        指定Id获取对应的音频路径
     */
    public String getAudioPathByIds(int ids){
        String audioPath = myDatabase.getAudioPathByIds(ids);
        return audioPath;
    }

    /*
        根据Data完全修改
     */
    public boolean fullChangeDataByData(Data data){
        myDatabase.fullUpdateDataByData(data);
        return true;
    }
    /*
        根据ids修改标题
     */
    public boolean singleSetDataTitleByIds(int ids,String newTitle){
        Data tmpData =  getDataByIds(ids);
        if(tmpData.getIds()!=-1) {
            tmpData.setTitle(newTitle);
            myDatabase.fullUpdateDataByData(tmpData);
            return true;
        }else{
            Log.d("SetDataTitleByIds", "单独修改标题失败，没有找到对应ID值的数据");
            return false;
        }
    }

    /*
        根据ids修改内容
     */
    public boolean singleSetDataContentByIds(int ids,String newContent){
        Data tmpData =  getDataByIds(ids);
        if(tmpData.getIds()!=-1) {
            tmpData.setContent(newContent);
            myDatabase.fullUpdateDataByData(tmpData);
            return true;
        }else{
            Log.d("SetDataContentByIds", "单独修改内容失败，没有找到对应ID值的数据");
            return false;
        }
    }

    /*
        根据ids修改时间
     */
    public boolean singleSetDataTimesByIds(int ids,String newTimes){
        Data tmpData =  getDataByIds(ids);
        if(tmpData.getIds()!=-1) {
            tmpData.setTimes(newTimes);
            myDatabase.fullUpdateDataByData(tmpData);
            return true;
        }else{
            Log.d("SetDataTimesByIds", "单独修改时间失败，没有找到对应ID值的数据");
            return false;
        }
    }

    /*
        根据ids修改录音路径
     */
    public boolean singleSetDataAudioathByIds(int ids,String newAudioPath){
        Data tmpData =  getDataByIds(ids);
        if(tmpData.getIds()!=-1) {
            tmpData.setAudioPath(newAudioPath);
            myDatabase.singleUpdateDataApByData(tmpData);
            return true;
        }else{
            Log.d("setDataAudioathByIds", "单独修改录音路径失败，没有找到对应ID值的数据");
            return false;
        }
    }

    /*
        根据ids修改图片路径
     */
    public boolean singleSetDataPicturePathByIds(int ids,String newPicturePath){
        Data tmpData =  getDataByIds(ids);
        if(tmpData.getIds()!=-1) {
            tmpData.setPicturePath(newPicturePath);
            myDatabase.fullUpdateDataByData(tmpData);
            return true;
        }else{
            Log.d("SetDataPicturePathByIds", "单独修改图片失败，没有找到对应ID值的数据");
            return false;
        }
    }

    /*
        添加数据:根据Data中title等内容插入数据库后返回ids值，设定ids值后添加进arr里面
     */
    public boolean addNewDataByData(Data newData){
        int newDataIds;
        newDataIds = myDatabase.insertNewDataByData(newData);
        newData.setIds(newDataIds);
        Log.d("DataDao.setText",newData.toString());
        allDataArr.add(0,newData);
        return true;
    }

    /*
        删除数据:根据ids删除数据库后删除arr
     */
    public boolean deleteDataByIds(int ids){
        myDatabase.deleteDataByIds(ids);
        for(int i=0 ; i< allDataArr.size() ; i++){
            if(ids == allDataArr.get(i).getIds()){
                Log.d("del","getids="+allDataArr.get(i).getIds()+";ids="+ids+";i="+i);
                //若查到对应ids则删除对应Data
                allDataArr.remove(i);
                break;
            }
        }
        return true;
    }
    public int getDatabaseMaxIds(){
        return myDatabase.getDatabaseCurrentMaxIds();
    }

}
