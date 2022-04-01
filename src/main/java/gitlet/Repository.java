package gitlet;

import java.io.File;
import java.util.Map;
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

    public void add() {

    }


    /* TODO: fill in the rest of this class. */
}
