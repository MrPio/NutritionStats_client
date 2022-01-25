package it.univpm.nutritionstats.utility.io;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class InputOutput {
    private String path = "";
    private String fileName;
    private Context context;

    public InputOutput(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public InputOutput(Context context, String path, String fileName) {
        this.context = context;
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
            FileInputStream fin =
                    new FileInputStream(new File(context.getFilesDir(), path + fileName));
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
        File file = new File(context.getFilesDir(), path + fileName);
        return file.exists();
    }

    public boolean writeFile(String msg, boolean... append) {
        File dir = new File(context.getFilesDir(), path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            File file = new File(dir, fileName);
            FileWriter writer = new FileWriter(file);
            if (append.length > 0 && append[0])
                writer.append(msg);
            else
                writer.write(msg);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFile() {
        File file = new File(context.getFilesDir(), path + fileName);
        return file.delete();
    }

    public ArrayList<String> readAndSplit(char delimiter){
        return new ArrayList<>(Arrays.asList(readFile().split(String.valueOf(delimiter))));
    }
}
