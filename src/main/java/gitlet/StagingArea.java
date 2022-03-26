package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.MyUtils.rm;
import static gitlet.Utils.readObject;
import static gitlet.Utils.writeContents;

public class StagingArea implements Serializable {
    // added files to the staging area
    private final Map<String, String> added = new HashMap<>();

    // files removed from staging area
    private final Set<String> removed = new HashSet<>();

    // tracked files key file path, value sha id
    private transient Map<String, String> tracked;

    // get staging area instance from file index
    public static StagingArea formFile() {
        return readObject(Repository.INDEX, StagingArea.class);
    }

    public void save() {
        writeContents(Repository.INDEX, this);
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public void setTracked(Map<String, String> filesMap) {
        tracked = filesMap;
    }

    // is clean when added and removed is empty
    public boolean isClean() {
        return added.isEmpty() && removed.isEmpty();
    }

    public void clear() {
        added.clear();
        removed.clear();
    }

    public Map<String, String> commit(){
        tracked.putAll(added);
        for(String filePath : removed)
            tracked.remove(filePath);

        clear();
        return tracked;
    }

    public boolean add(File file){
        String filePath = file.getPath();
        Blob blob = new Blob(file);
        String blobId = blob.getId();
        String trackedBlobId = tracked.get(blobId);

        if(trackedBlobId != null) {
            if(trackedBlobId.equals(blobId)) {
                if(added.remove(filePath) != null) {
                    return true;
                }
                return removed.remove(filePath);
            }
        }

        String prevBlobId = added.put(filePath, blobId);
        if(prevBlobId != null && prevBlobId.equals(blobId)) {
            return  false;
        }
        if(!blob.getFile().exists()) {
            blob.save();
        }
        return true;
    }

    public boolean remove(File file) {
        String filePath = file.getPath();
        String addedBlobId = added.remove(filePath);

        if(addedBlobId != null) {
            return true;
        }

        if(tracked.get(filePath) != null) {
            if (file.exists()) {
                rm(file);
            }
            return removed.add(filePath);
        }
        return false;
    }
}
