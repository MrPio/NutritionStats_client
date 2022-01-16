package it.univpm.nutritionstats.utility;

import android.content.Context;

import java.io.*;
import java.util.Scanner;

public class InputOutputImpl implements InputOutput {
    private String path = "";
    private String fileName;
    Context context;

    public InputOutputImpl(Context context,String fileName) {
        this.context=context;
        this.fileName = fileName;
    }

    public InputOutputImpl(Context context,String path, String fileName) {
        this.context=context;
        this.path = path;
        this.fileName = fileName;

        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullPath() {
        return path + fileName;
    }

    public String readFile() {
        String result = "";
        try {
            FileInputStream fin = new FileInputStream(new File(context.getFilesDir(),path + fileName));
            int c;

            while ((c = fin.read()) != -1) {
                result += (char) c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean existFile() {
        File file = new File(context.getFilesDir(),path + fileName);
        return file.exists();
    }

    public boolean writeFile(String msg) {
        File dir = new File(context.getFilesDir(), path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            File file = new File(dir, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(msg);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFile(){
        File file = new File(context.getFilesDir(),path + fileName);
        return file.delete();
    }
}
