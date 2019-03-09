package utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

//常量管理类
public class Constant {

    //文件路径管理类
    public static class FilePath {
        //音频文件存放于 NoteBlocks/record/ 下
        public static final String ROOT_PATH = "NoteBlocks/";
        public static final String RECORD_DIR = "record/";
        public static final String RECORD_PATH = ROOT_PATH + RECORD_DIR;
    }

    /**
     * 复制文件夹及其中的文件
     *
     * @param oldPath String 原文件夹路径 如：data/user/0/com.test/files
     * @param newPath String 复制后的路径 如：data/user/0/com.test/cache
     * @return <code>true</code> if and only if the directory and files were copied;
     * <code>false</code> otherwise
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e("Method**", "copyFolder: cannot create directory.");
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }
                //如果是子文件夹
                if (temp.isDirectory()) {
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e("Method**", "copyFolder:  oldFile not exist.");
                } else if (!temp.isFile()) {
                    Log.e("Method**", "copyFolder:  oldFile not file.");
                } else if (!temp.canRead()) {
                    Log.e("Method**", "copyFolder:  oldFile cannot read.");
                } else {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }

            /* 如果不需要打log，可以使用下面的语句
            if (temp.isDirectory()) {   //如果是子文件夹
                copyFolder(oldPath + "/" + file, newPath + "/" + file);
            } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                FileInputStream fileInputStream = new FileInputStream(temp);
                FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                byte[] buffer = new byte[1024];
                int byteRead;
                while ((byteRead = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, byteRead);
                }
                fileInputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 要删除的文件夹的所在位置
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            //如要保留文件夹，只删除文件，请注释这行
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
    }

}
