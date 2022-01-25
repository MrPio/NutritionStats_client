package it.univpm.nutritionstats.utility.io;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serialization {
    private       String  path = "";
    private final String  fileName;
    private       Context context;


    public Serialization(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public Serialization(Context context, String path, String fileName) {
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


    public Object loadObject() {
        Object read=null;
        try (FileInputStream fis = context.openFileInput(fileName);) {
            ObjectInputStream is = new ObjectInputStream(fis);
            read = is.readObject();
            is.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return read;
    }

    public boolean existFile() {
        File file = new File(context.getFilesDir(), path + fileName);
        return file.exists();
    }

    public <T extends Serializable> void saveObject(T obj) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);) {
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
