package com.example.demo.api;

import com.example.demo.utils.DbDao;
import com.example.demo.model.Note;
import com.example.demo.utils.NoteDao;
import com.example.demo.utils.DealHtml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class API {
    public API(){
    }
    // http接口
    /*
        获取所有数据的接口（当用户名和密码一致返回所有便签的内容）
        需要参数：1.用户ID

//     */
//    @GetMapping("/getAllNotes")
//    public ArrayList getNote(String username){
//        DbDao.setTableName(username);
//        if(DbDao.isExistTable()){
//            NoteDao.reloadNotes();
//            return NoteDao.getAllNotes();
//        }else {
//            return new ArrayList<Note>();
//        }
//    }
//    @PostMapping("/backUpNotes")
//    public boolean backUpNotes(@RequestBody UserNotesModel uploadNotes){
//        return NoteDao.backUpNotes(uploadNotes);
//    }

//    @GetMapping("/getNote")
//    public String getNote(int ids){
//        String a ;
//        try{
//            Document document = Jsoup.connect("http://localhost:8080/share").get();
//            DealHtml.getUserHtml(document,ids);
//            a = document.toString();
//        }catch (IOException e){
//            e.printStackTrace();
//            a = "error";
//        }
//        return a;
//    }

//
//    @GetMapping("A204")
//    public String a204(){
//        return "A204NB";
//    }
//    @GetMapping("getAllNote")
//    public ArrayList getNote(){
//        return noteDao.getAllNotes();
//    }
//    @GetMapping("getNoteByName")
//    public Note getNoteByName(int i){
//        return noteDao.getAllNotes().get(i);
//    }
//    @PostMapping("saveNote")
//    public Note saveNote(String title,String content){
//        noteDao.addNote(noteDao.getNewNote().setDate(new Date()).setTitle(title).setContent(content));
//        int len=noteDao.getAllNotes().size();
//        return noteDao.getAllNotes().get(len-1);
//    }
//    @GetMapping("delNoteByNumber")
//    public boolean delNoteByNumber(int number){
//        noteDao.removeNote(number);
//        return true;
//    }
    //http://10.252.30.30:8080/saveNote?title=%E6%B5%8B%E8%AF%95%E6%B5%8F%E8%A7%88%E5%99%A8getapi&content=%E8%BF%99%E6%98%AF%E7%AC%AC%E4%BA%94%E6%9D%A1%E6%95%B0%E6%8D%AE
}
