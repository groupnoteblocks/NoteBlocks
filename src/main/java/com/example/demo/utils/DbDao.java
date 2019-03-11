package com.example.demo.utils;

import com.example.demo.mapper.DatabaseMapper;
import com.example.demo.model.Note;
import com.oracle.tools.packager.Log;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbDao {
    // mybatis 相关
    private static String resource = "config/mybatis-config.xml";
    private static InputStream inputStream;
    static {
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    private static SqlSession session = sqlSessionFactory.openSession();
    private static DatabaseMapper myDB = session.getMapper(DatabaseMapper.class);
    private static ArrayList<Note> allDatas = new ArrayList<Note>();



    /**
     * 创建表。（当表不存在才创建，表存在时不创建）
     */
    public static void creatTable(){
        if(myDB.isTableExist()>=1){
            System.out.println("DbDao:creatTable -- " + "表已存在，创建完成。");
        }else{
            System.out.println("DbDao:creatTable -- " + "尚未初始化表，正在创建。");
            myDB.createTable();
        }
    }


    /**
     * 向表中插入一条数据。
     * @param newNote 数据模型Note的实例
     */
    public static void addData(Note newNote) {
        System.out.println("DbDao:addData -- " + "正在添加数据到数据库");
        myDB.addData(newNote);
        session.commit();//开启了jdbc事务，需要提交生效
    }

    /**
     * ids 和 user 共同定位到数据并删除数据
     * @param ids
     * @param user
     */
    public static void  delDataByIds(int ids,String user) {
        Map map = getMap(ids,user);   //构造需要的Map
        System.out.println("DbDao:delDataByIds -- " + "构造map完成，执行数据库删除操作");

        myDB.delDataByIds(map);
        session.commit();//开启了jdbc事务，需要提交生效
    }

    /*
        在表中查询对应ids的数据
        需要参数：1.ids值和user值
        返回数据：数据库中对应ids值得数据在Note中的实例
     */

    /**
     * ids 和 user 共同定位并返回数据
     * @param ids
     * @param user
     * @return
     */
    public static Note searchDataByIds(int ids,String user){
        Map map = getMap(ids,user);   //构造需要的Map
        System.out.println("DbDao:searchDataByIds -- " + "构造map完成，执行数据库查询操作");

        Note note  = myDB.searchDataByIds(map);
        return note;
    }

    /**
     * 在表中查询user所有的值
     * @return 数据库中所有数据在Note模型中的实例组成的数组ArrayList
     */
    public static ArrayList<Note> getAllDatas(String user){
        System.out.println("DbDao:getAllDatas -- " + "执行数据库查询操作");
        return myDB.getDatas(user);
    }

    /**
     * 在表中修改数据（根据Note实例中的ids和user,在数据库中修改除了id以外的其他属性值）
     * @param newNote
     */
    public static void changeData(Note newNote){
        System.out.println("DbDao:changeData -- " + "执行数据库更新操作");
        myDB.fullUpdateData(newNote);
    }

    /**
     * 获得数据库ids当前最大值
     * @return 数据库当前的ids的最大值
     */
    public static int getMaxIds(String user){
        int maxIds = 0;
        maxIds = myDB.getMaxIds(user);
        return maxIds;
    }

    /**
     * 获得带有基础属性的map
     * @param ids
     * @param user
     * @return map 包含了(ids,user)
     */
    public static Map getMap(int ids,String user){
        Map map = new HashMap();            //构造需要的map
        map.put("user",user);
        map.put("ids",ids);
        return map;
    }

}
