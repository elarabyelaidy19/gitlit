package gitlet;

import java.io.Serializable;
import java.sql.Blob;
import java.text.DateFormat;
import java.util.ArrayList;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*; 

import java.util.Date;


import static gitlet.MyUtils.*;
import static gitlet.Utils.readObject;
import static gitlet.Utils.sha1; 


/** the gitlet commit object.
 *  @author Elaraby
 */
public class Commit implements Serializable {

    // the commit message 
    private final String message;

    // date of the commit 
    private final Date date;

    // the parent commit 
    private final List<String> parents;

    // key value pair of tracked files, K file path V SHA-1 id
    private final Map<String, String> tracked;

    // SHA-1 id of the commit 
    private String id;
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
    public static Commit formFile(String id) {
        return readObject(getObjectFile(id), Commit.class);
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

    public List<String> getParents() {
        return parents;
    }
    // returns the tracked files Key file paths, Value SHA-1 id
    public Map<String, String> getTracked() {
        return tracked;
    }


    // restore tracked files 
    public boolean restoreTracked(String filepath) {
        String blobId = tracked.get(filepath);
        if(blobId == null) {
            return false;
        }
        Blob.fromFile(blobId).writContentToSource();
        return true;
    }

    public void restoreallTracked() {
        for(String blobId : tracked.values()) {
            Blob.formFile(blobId).writeContentToSource();
        }
    }

    public String getId() {
        return id;
    }

    public String getLog() {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("==========").append("\n");
        logBuilder.append("commit").append(" ").append(id).append("\n");
        if(getParents().size() > 1) {
            logBuilder.append("Merge");
            for (String parent : getParents()) {
                logBuilder.append(parent, 0, 7);
            }
            logBuilder.append("\n");
        }
        logBuilder.append("TimeStamp:").append(" ").append(getDate()).append("\n");
        logBuilder.append(getMessage()).append("\n");
        return logBuilder.toString();
    }
}
