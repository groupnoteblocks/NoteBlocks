package utils;

import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import model.Data;
import presenter.DataDao;

/**
 * 导入和导出相关操作类
 */

public class Backup {
    /**
     * 创建NoteBlocks.json文件
     * @param file
     */
    public static void createJsonFile(File file){
        File JSONFile = new File(file,  "NoteBlocks.json");
        if (!JSONFile.exists()) {
            try {
                JSONFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String filePath = JSONFile.getAbsolutePath();
        //Log.d("JsonPath***",filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            //开始写json数据
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream,"UTF-8"));
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();

            //Log.d("*/*/*/*//*",""+arrayList.size());
            for(int i = DataDao.GetAllDatas().size()-1; i>=0; i--){
                jsonWriter.beginObject();
                jsonWriter.name("id").value(DataDao.GetAllDatas().get(i).getIds());
                jsonWriter.name("title").value(DataDao.GetAllDatas().get(i).getTitle());
                jsonWriter.name("content").value(DataDao.GetAllDatas().get(i).getContent());
                jsonWriter.name("audioPath").value(DataDao.GetAllDatas().get(i).getAudioPath());
                jsonWriter.name("picturePath").value(DataDao.GetAllDatas().get(i).getPicturePath());
                jsonWriter.name("times").value(DataDao.GetAllDatas().get(i).getTimes());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            //Toast.makeText(MainActivity.this, "成功！", Toast.LENGTH_SHORT).show();
            jsonWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 读取oteBlocks.json文件并将数据保存到DataDao和数据库中
     * @param JsonFilePath
     */
    public static void readJsonFile(String JsonFilePath){

        try {
            FileInputStream fileInputStream = new FileInputStream(JsonFilePath);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(fileInputStream,"UTF-8"));
            jsonReader.beginArray();
            Data data = new Data();
            while(jsonReader.hasNext()){
                jsonReader.beginObject();
                data = new Data();
                while(jsonReader.hasNext()){
                    String s = jsonReader.nextName();
                    if(s.equals("id")){
                        data.setIds(jsonReader.nextInt());
                    }
                    else if(s.equals("title")){
                        data.setTitle(jsonReader.nextString());
                    }
                    else if(s.equals("content")){
                        data.setContent(jsonReader.nextString());
                    }
                    else if(s.equals("audioPath")){
                        data.setAudioPath(jsonReader.nextString());
                    }
                    else if(s.equals("picturePath")){
                        data.setPicturePath(jsonReader.nextString());
                    }
                    else if(s.equals("times")){
                        data.setTimes(jsonReader.nextString());
                    }
                    else{
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
                boolean flag = DataDao.AddNewData(data);
                //Log.d("testDataDao*1","data： "+data);
            }
            for(int i= 0; i<DataDao.GetAllDatas().size();i++){
                //Log.d("testDataDao*2","data： "+DataDao.GetAllDatas().get(i));
            }

            jsonReader.endArray();
            jsonReader.close();

            //Toast.makeText(MainActivity.this, arrayList.get(3).toString(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
