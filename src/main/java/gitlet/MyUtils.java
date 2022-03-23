package gitlet;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.File; 
import java.io.FileInputStream; 
import java.util.function.Supplier; 
import java.util.ArrayList;

import static gitlet.Utils.*;   

public class MyUtils {
    
    public static <T> Lazy<T> lazy(Supplier<T> delegate) {
        return  new Lazy<>(delegate);
    }

    public static void mkdir(File dir) {
        if(!dir.mkdir())
            throw new IllegalArgumentException(String.format("mkdir: %s failed to create", dir.getPath()));
    }

    public  static void rm(File file) {
        if(!file.delete())
            throw new IllegalArgumentException(String.format("rm: %s failed to deletes", file.getPath()));
    }

    public static void exit(String message, Object... args) {
        message(message, args);
        System.exit(0);
    }

    public static boolean isFileInstanceOf(File file, Class<?> c) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return c.isInstance(in.readObject());
        } catch (Exception ignored) {
            return false;
        }
    }

    public static File getObjectFile(String id) {
        String dirName = getObjectDirName(id); 
        String fileName = getObjectFileName(id);
        return join(Repository.OBJECTS_DIR, dirName, fileName);
    }

    public static String getObjectDirName(String id) { 
        return id.substring(0, 2);
    } 

    public static String getObjectFileName(String id) { 
        return id.substring(2);
    }

    public static void saveObjectFile(File file, Serializable obj) { 
        File dir = file.getParentFile(); 
        if(!dir.exists()) { 
            dir.mkdirs();
        }
        writeObject(file, obj);
    }


}
