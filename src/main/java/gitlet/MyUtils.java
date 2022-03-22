package gitlet;
import java.io.Serializable; 
import java.io.File; 
import java.io.FileInputStream; 
import java.util.function.Supplier; 
import java.util.ArrayList;

import static gitlet.Utils.*;   

public class MyUtils {
    

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
