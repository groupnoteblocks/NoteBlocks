package com.example.demo.utils;

import com.example.demo.model.Note;

import java.util.ArrayList;
//数据集合以及数据操作类
public class NoteDao {
    private static ArrayList<Note> allNotes ;

    /**
     * 重新加载array数组
     */
    public static void reloadNotes(String user){
        allNotes = DbDao.getAllDatas(user);
    }
    /**
     * 返回全局的数据集合，需要参数:
     * @return ArrayList<Note>，存有所有的数据
     */
    public static ArrayList<Note> getAllNotes(){
        return allNotes;
    }

    /**
     * 往数据集合中添加数据。
     * @param newNote 1. 一个数据模型的实例
     */
    public static void addNote(Note newNote){
        DbDao.addData(newNote);
        allNotes.add(newNote);
        System.out.println("NoteDao:addNote -- "+"添加Note成功");

    }

    /**
     * 往数据集合中移除数据。
     * @param ids 一个数据模型的ids
     */
    public static void removeNoteByIds(int ids){
        for(int j =0;j<allNotes.size();j++){
            if(allNotes.get(j).getIds()==ids){
                DbDao.delDataByIds( allNotes.get(j).getIds(),allNotes.get(j).getUser());
                allNotes.remove(j);
                System.out.println("NoteDao:removeNoteByIds -- "+"成功移除数据");
                break;
            }
        }
    }

    /**
     *
     从数据集合中获取数据。
     * @param ids
     * @return 返回值：指定ids值的数据
     */
    public static Note getNoteByIds(int ids){
        for(int j =0;j<allNotes.size();j++){
            if(allNotes.get(j).getIds()==ids){
                System.out.println("NoteDao:getNoteByIds -- "+"成功根据ids获得数据");
                return allNotes.get(j);
            }
        }
        //当没有找到对应值的数据时返回一个带错误标识的数据
        return getErrorNode("没有找到对应ids的数据");
    }


    /**
     * 从数据集合中修改数据
     * @param newNote
     */
    public static void changeNote(Note newNote){
        DbDao.changeData(newNote);
        for(int j =0;j<allNotes.size();j++){
            if(allNotes.get(j).getIds()==newNote.getIds()){
                allNotes.set(j,newNote);
                System.out.println("NoteDao:changeNote -- "+"修改数据成功");
                break;
            }
        }
    }

    /**
     * 返回一个带有错误标识的数据模型。
     * @param errorDes 错误描述
     * @return 带错误标识的Node实例，ids为-1，其余参数全为errorDes
     */
    public static Note getErrorNode(String errorDes){
        Note errorNote = new Note(-1);
        errorNote.setIds(-1);
        errorNote.setTitle(errorDes);
        errorNote.setContent(errorDes);
        errorNote.setTimes(null);
        errorNote.setAudioPath(errorDes);
        errorNote.setPicturePath(errorDes);
        return errorNote;
    }

    /**
     * 得到当前数据库的最大值。
     * @return 最大的ids值
     */
    public static int getMaxIds(String user){
        return DbDao.getMaxIds(user);
    }

    /*
        根据用户名建表备份
     */
//
//    public static boolean backUpNotes(UserNotesModel uploadNotes){
//        boolean success = false;
//        DbDao.setTableName(uploadNotes.name);
//        DbDao.creatTable();
//        NoteDao.reloadNotes();
//        for(int i=0;i<uploadNotes.getNotes().size();i++){
//            success = NoteDao.addNote(uploadNotes.getNotes().get(i));
//        }
//        return success;
//
//    }

}
