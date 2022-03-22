package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*; 

import java.util.Date; 


import static gitlet.Utils.readObject;
import static gitlet.Utils.sha1; 


/** the gitlet commit object.
 *  @author Elaraby
 */
public class Commit {  

    // the commit message 
    private final String message; 
    
    // date of the commit 
    private final Date date; 

    // the parent commit 
    private final List<String> parents; 

    // key value pair of tracked files, K file path V SHA-1 id
    private final Map<String, String> tracked; 
    
    // SHA-1 id of the commit 
    private final String id;  
    // the file of this commit with the path generated from the SHA-1 id
    private final File file; 

    /** Constructs a commit with the given fields. */

    public Commit(String message, List<String> parents, Map<String, String> tracked) { 
        date = new Date(); 
        this.message = message; 
        this.parents = parents;
        this.tracked = tracked;
        id = generateId(); 
        file = getObjectFile(id); 

    } 

    // Ininitial commit 
    public Commit() { 
        date = new Date(0); 
        message = "initial commit";
        parents = new ArrayList<>(); 
        tracked = new HashMap<>();
        id = generateId(); 
        file = getObjectFile(id);
    }

    // takes sha id of file and returns instance of commit
    public static Commit getCommit(String id) { 
        return readObject(getObjectFile(id), Commit.class);); 
    }

    // generates SHA-1 id of commit
    private String generateId() { 
        return sha1(message, getTimestamp(), parents.toString(), tracked.toString());  
    } 

    public String getTimestamp() { 
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH); 
        return dateFormat.format(date);
    }

    // serializes commit object and save to file
    public void save() { 
        saveObjectFile(file, this);
    }

    // returns the commit date 
    public Date getDate() { 
        return date;
    }

    public String getMessage() { 
        return message;
    }


}
