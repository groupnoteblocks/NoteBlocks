
package model;


import java.util.ArrayList;

public class Data {
    private int ids;        //编号
    private String title;   //标题
    private String content; //内容
    private String times;   //时间
    private String audioPath;   //音频路径
    private String picturePath; //图片路径
    ArrayList<String> picturePathArr; //存放分割好的图片路径
    ArrayList<String> audioPathArr;  //存放分割好的音频路径

    public Data(){
        this.ids=0;
        this.times="";
        this.title="";
        this.content="";
        this.audioPath = "";
        this.picturePath = "";
        this.picturePathArr = new ArrayList<>();
    }


    public Data(int id , String time , String title , String content , String aP , String pP){
        this.ids=id;
        this.times=time;
        this.title=title;
        this.content=content;
        this.audioPath = aP;
        this.picturePath = pP;
        this.picturePathArr = new ArrayList<>();
        //setAudioPathArr();
        cutPicturePath();
    }


    public int getIds() {

        return ids;

    }

    public String getTitle() {

        return title;

    }

    public String getContent() {

        return content;

    }

    public String getTimes() {

        return times;

    }

    public String getAudioPath(){

        return audioPath;

    }

    public String getPicturePath() {

        return picturePath;

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public void setIds(int ids){
        this.ids = ids;
    }

    @Override
    public String toString() {
        return getIds()+","+getTimes()+","+getTitle()+","+getContent()+","+getAudioPath()+","+getPicturePath();
    }

    /*
        对音频路径进行分割，分别存入audioPathArr中
     */
    /*
    public void setAudioPathArr(){
        audioPathArr = new ArrayList<>();
        String singleAudioPath = "";
        String oldAudioPath = audioPath;
        //去掉?
        int begin = 0,end = 0;
        boolean flag;
        for(int i=0;i<oldAudioPath.length();i++) {
            flag = true;
            if(oldAudioPath.charAt(i)=='?') {
                end = i;
                singleAudioPath = oldAudioPath.substring(begin, end);
                for(int j=0;j<audioPathArr.size();j++){
                    if(singleAudioPath.equals(audioPathArr.get(j))){
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    audioPathArr.add(singleAudioPath);
                }
                //System.out.println(audioPath);
                begin=end+1;
            }
        }
    }
    */

    /*
        对图片路径进行分割，分别存入picturePathArr中
     */
    public void cutPicturePath(){
        //picturePathArr = new ArrayList<>();
        String singlePicturePath = "";
        String oldPicturePath = picturePath;
        //去掉?
        int begin = 0,end = 0;
        boolean flag;
        for(int i=0;i<oldPicturePath.length();i++){
            flag = true;
            if(oldPicturePath.charAt(i)=='?'){
                end = i;
                singlePicturePath = oldPicturePath.substring(begin, end);
                for(int j=0;j<picturePathArr.size();j++){
                    if(singlePicturePath.equals(picturePathArr.get(j))){
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    picturePathArr.add(singlePicturePath);
                }
                begin=end+1;
            }
        }
    }

    /*
        获取分割好的音频路径数组audioPathArr
     */
    /*
    public ArrayList<String> getAudioPathArr() {
        return audioPathArr;
    }
    */

    /*
        获取分割好的图片路径数组picturePathArr
     */

    public ArrayList<String> getPicturePathArr() {
        return picturePathArr;
    }
}
