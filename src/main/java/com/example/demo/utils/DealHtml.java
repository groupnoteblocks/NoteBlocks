package com.example.demo.utils;

import com.example.demo.model.Note;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DealHtml {
    public static void getUserHtml(Document tmplate, int i){
        Note note = Code.getUserNote(i);
        Element navBar = tmplate.getElementsByClass("nav_inner_top_left").get(0);
        Element label_user = tmplate.getElementsByClass("user").get(0);
        Element nav_title = navBar.getElementsByClass("title").get(0);
        Element nav_time = navBar.getElementsByClass("time").get(0);
        Element content_time = tmplate.getElementsByClass("text_time").get(0);
        Element content_title = tmplate.getElementsByClass("text_title").get(0);
        Element content_text =tmplate.getElementsByClass("text_content").get(0);
        label_user.html("admin");
        nav_title.html(note.getTitle());
        nav_time.html(note.getTimes().toString());
        content_time.html(note.getTimes().toString());
        content_title.html(note.getTitle());
        content_text.html(note.getContent());
    }
}
