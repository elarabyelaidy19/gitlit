package gitlet;

import java.io.Serializable;
import java.sql.Blob;
import java.text.DateFormat;
import java.util.ArrayList;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*; 

import java.util.Date;

import static gitlet.Utils.*;


/** the gitlet commit object.
 *  @author Elaraby
 */
public class Commit implements Serializable {

    // the commit message 
    private String message;

    // date of the commit 
    private Date date;

    // key value pair of tracked files, K file path V SHA-1 id
    private HashMap<String, String> blobs;

    // SHA-1 id of the commit parent
    private String parent;
    // commit merge parent
    private String mergeParent;


    /**
     * Constructs a commit with the given fields.
     */

    public Commit(String message, String parent, String mergeParent) {
        date = new Date();
        this.message = message;
        this.parent = parent;
        blobs = new HashMap<>();
        this.mergeParent = mergeParent;
        if (Objects.equals(message, "initial commit")) {
            date = new Date(0);
            parent = null;
        }
    }

    public String getSha1() {
        return sha1(serialize(this), "commit");
    }

    public String getMessage() {
        return message;
    }


    public String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }


    // returns the commit date 
    public Date getDate() {
        return date;
    }

    // returns the tracked files Key file paths, Value SHA-1 id
    public Map<String, String> getBlobs() {
        return blobs;
    }

    public String getParent() {
        return parent;
    }

    public String getMergeParent() {
        return mergeParent;
    }

}
