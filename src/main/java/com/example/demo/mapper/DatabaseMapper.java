package com.example.demo.mapper;

import com.example.demo.model.Note;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Map;

public interface DatabaseMapper {

    /**
     * 无参数
     * @return 查询到的表数量，>=1则说明存在表
     */
    public int isTableExist();

    /**
     * 建表
     */
    public void createTable();

    /**
     * 通过user和ids两个参数找到并删除数据
     * @param map 需要包含(user,ids)
     */
    public void delDataByIds(Map map);

    /**
     * 通过id找到并删除数据
     * @param id id 和 ids是不同的，id是主键，ids是手机上的主键
     */
    public void delDataById(@Param("id") int id);

    /**
     * 通过user和ids两个参数找到并设置标题
     * @param map 需要包含(user,ids,title)
     */
    public void setTitleByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置内容
     * @param map 需要包含(user,ids,content)
     */
    public void setContentByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置时间
     * @param map 需要包含(user,ids,times)
     */
    public void setTimesByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置音频地址
     * @param map 需要包含(user,ids,audioPath)
     */
    public void setAudioPathByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置图片地址
     * @param map 需要包含(user,ids,picture)
     */
    public void setPicturePathByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置开始分享时间
     * @param map 需要包含(user,ids,bgShareTime)
     */
    public void setBgShareTimeByIds(Map map);

    /**
     * 通过user和ids两个参数找到并设置结束分享时间
     * @param map 需要包含(user,ids,edShareTime)
     */
    public void setEdShareTimeByIds(Map map);

    /**
     * 传入一个完整的Note对象，更新除了id，ids，user外的所有值
     * @param note
     */
    public void fullUpdateData(Note note);

    /**
     * 传入一个完整的Note对象，自动在数据库获取id，将除id外的所有值全部存入数据库
     * @param newNote
     */
    public void addData(Note newNote);

    /**
     * 根据 user 和 ids 找到并返回数据
     * @param map 需要包含(user,ids)
     * @return 完整的Note数据
     */
    public Note searchDataByIds(Map map);

    /**
     * 根据 id 找到并返回数据
     * @param ids
     * @return 完整的Note数据
     */
    public Note searchDataById(@Param("id") int ids);

    /**
     * 传入user获得数据库所有数据
     * @return 带有所有数据的ArrayList
     */
    public ArrayList<Note> getDatas(@Param("user")String user);

    /**
     * 获取指定user在数据库中ids的最大值
     * @param user
     * @return
     */
    public int getMaxIds(@Param("user")String user);

}
