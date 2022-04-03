package gitlet;

import java.io.File;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Elaraby Elaidy
 */
public class Repository {

    // The current working directory.
    private File CWD;
    // .gitlet file
    private File GITLETREPO;
    // staging dir
    private File STAGING;
    // StagingArea object
    private StagingArea stage;
    // commits file
    private File COMMITS;
    // map sha id to commit
    private TreeMap<String, Commit> commits;
    // points to the head of each branch
    private File BRANCHES;
    // store sha 1 of head of current branch
    private File HEAD;
    // store head commit of master Bracnh
    private File MASTER;
    // storing blobs
    private File BLOBS;


    public Repository() {
        CWD = new File(System.getProperty("user.dir"));
        GITLETREPO = join(CWD, ".gitlet");
        COMMITS = join(GITLETREPO, "commits");
        BLOBS = join(GITLETREPO, "blobs");
        BRANCHES = join(GITLETREPO, "branches");
        HEAD = join(BRANCHES, "HEAD");
        MASTER = join(BRANCHES, "matser");
        STAGING = join(GITLETREPO, "staging" + ".txt");
        stage = new StagingArea();
        commits = new TreeMap<>();
    }

    public void init() {
        if(Utils.join(System.getProperty("user.dir"), ".gitlet").exists()) {
            String msg = "A Gitlet version-control system already exists in the current directory";
            message(msg);
            return;
        }
        GITLETREPO.mkdir();
        STAGING.mkdir();
        COMMITS.mkdir();
        writeContents(STAGING, stage);
        Commit initialCommit = new Commit("initial commit", null, null);
        commits.put(initialCommit.getSha1(), initialCommit);
        writeContents(COMMITS, initialCommit);
        writeContents(MASTER, initialCommit.getSha1());
        writeContents(HEAD, "master");
    }

    public void add(String filename) {
        if(!join(CWD, filename).exists()) {
            throw new GitletException("file does not exist");
        }
        File target = join(CWD, filename);
        Blob toAdd = new Blob(target);
        String toAddName = toAdd.getFileName();
        File toAddFile = join(BLOBS, toAdd.getSHA());
        stage = getStage(); // get instance of satgingArea reading from file catsing it
        writeContents(toAddFile, toAdd.getContent());

        Commit currentHead = getHead(); // most recent commit in the main branch
        // remove if it is found in staged to remove
        if(stage.getRemoved().contains(filename)) {
            stage.getRemoved().remove(filename);
            writeObject(STAGING, stage);
        }

        // if this file does'nt changed from previous commit do not add it to the staging area
        if(currentHead.getBlobs().containsValue(toAdd.getSHA())
            && currentHead.getBlobs().get(filename).equals(toAdd.getSHA())) {
            stage = getStage();
            stage.unStage(filename);
            writeObject(STAGING, stage);
            return;
        } 

        //add file to staging and update content
        stage.add(fileName, toAdd.getSHA); 
        writeContents(STAGING, stage);
    }

    public StagingArea getStage() {
        return readObject(STAGING, StagingArea.class);
    }

    public Commit getHead() {
        String headName = readContentsAsString(HEAD);
        File headFile = join(BRANCHES, headName);
        String headId = readContentsAsString(headFile);
        commits = readObject(COMMITS, TreeMap.class);
        Commit currHead = commits.get(headId);
        return  currHead;
    } 

    public void commit(String message, String mergeParent) { 
        if(message.length() <= 0) { 
            throw new GitletException("message not provided"); 
        }

        stage = getStage();
        if(stage.getAdded().isEmpty() && stage.getRemoved().isEmpty()) { 
            throw new GitletException("nothing to commits");
        }

        Commit newCommit = new Commit(message, getHead().getSHA, mergeParent);
        for(String key: getHead().getBlobs().keySet()) { 
            if(!stage.getRemoved().contains(key)) { 
                newCommit.getBlobs().put(key, getHead().getBlobs().get(key)); 
            }
        }

        for(String key : stage.getAdded().keySet()) { 
            String shaId = stage.getAdded().get(key); 
            if(!newCommit.getBlobs().contains(shaId)) { 
                newCommit.getBlobs().put(key, shaId);
            }
        }

        commits = getCommits(); 
        commits.put(newCommit.getSHA(), newCommit); 
        writeObject(COMMITS, commits); 
        updateActiveBranch(newCommit.getSHA());
        stage.clear(); 
        writeObject(STAGING, stage);
    }

    public TreeMap<String, Commit> getCommits() { 
        readObject(COMMITS, TreeMap.class);
    }
}
