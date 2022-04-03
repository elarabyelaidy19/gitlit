package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;


import static gitlet.Utils.readObject;
import static gitlet.Utils.writeContents;

public class StagingArea implements Serializable {
    // added files to the staging area
    private HashMap<String, String> added;

    // files removed from staging area
    private ArrayList<String> removed;

    public StagingArea() {
        added = new HashMap<>();
        removed = new ArrayList<>();
    }

    public void clear() {
        added = new HashMap<>();
        removed = new ArrayList<>();
    }

    public void add(String fileName, String sha1) {
        added.put(fileName, sha1);
    }

    public void remove(String fileName) {
        removed.add(fileName);
    }

    public HashMap<String, String> getAdded() {
        return added;
    }
    public ArrayList<String> getRemoved() {
        return removed;
    }

    public void unStage(String fileName) {
        added.remove(fileName);
    }

}
