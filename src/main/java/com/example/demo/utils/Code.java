package com.example.demo.utils;

import com.example.demo.model.Note;

public class Code {
    public static Note getUserNote(int id){
        Note note = NoteDao.getNoteByIds(id);
        return note;
    }
}
