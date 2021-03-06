package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static sun.security.util.Debug.args;

/** 
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
    // map sha id to commit object
    private TreeMap<String, Commit> commits;
    // points to the head of each branch
    private File BRANCHES;
    // store sha 1 of head of current branch
    private File HEAD;
    // store head commit of master Bracnh
    private File MASTER;
    // storing blobs
    private File BLOBS;
    private String fileName;


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

    // Return an object of type StagingArea read from FILE STAGING, casting it to StagingArea class.
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

        Commit newCommit = new Commit(message, getHead().getSha1(), mergeParent);
        for(String key: getHead().getBlobs().keySet()) { 
            if(!stage.getRemoved().contains(key)) { 
                newCommit.getBlobs().put(key, getHead().getBlobs().get(key)); 
            }
        }

        for(String key : stage.getAdded().keySet()) { 
            String shaId = stage.getAdded().get(key); 
            if(!newCommit.getBlobs().containsKey(shaId)) {
                newCommit.getBlobs().put(key, shaId);
            }
        }

        commits = getCommits(); 
        commits.put(newCommit.getSha1(), newCommit);
        writeObject(COMMITS, commits); 
        updateActiveBranch(newCommit.getSha1());
        stage.clear(); 
        writeObject(STAGING, stage);
    }

    private void updateActiveBranch(String sha1) { 
        String activeBranch = readContentsAsString(HEAD); 
        File activeBranchFile = join(BRANCHES, activeBranch);
        writeContents(activeBranchFile. sha1);
    }

    public TreeMap<String, Commit> getCommits() { 
        readObject(COMMITS, TreeMap.class);
    }


    public void rm(String fileName) { 
        stage = getStage(); 
        boolean tracked = false;
        boolean staged = false; 

        if(stage.getAdded().containsKey(fileName)) { 
            staged = true; 
            stage.unStage(fileName); 
            writeObject(STAGING, stage);
        }

        if(getHead().getBlobs().containsKey(fileName) { 
            tracked = true;
            stage.unStage(fileName); 
            stage.remove(fileName); 
            restrictedDelete(fileName);
            writeObject(STAGING, stage);
        } 

        if(!tracked && !staged) { 
            throw new GitletException("No reason to remove the file.");
        }
    }

    // ============================================================================= 
    // Log 
    public void log() { 
        commits = getCommits(); 
        Commit head = getHead(); 
        while(head != null) { 
            printLog(head); 
            if(head.getParent() != null) { 
                head = commits.get(head.getParent());
            } else { 
                break;
            }
        }
    }

    public void printLog(Commit commit) { 
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        System.out.println("==="); 
        System.out.println("Commit: "+ commit.getSha1());
        System.out.println("Date: "+ dateFormat.commit.getDate());
        System.out.println("Message: "+ commit.getMessage());
        System.out.println("\n"); 
    } 

    public void globalLog() { 
        commits = getCommits();
        List<String> keys = new ArrayList<>(commits.keySet());
        Collections.reverse(keys);
        for(String id : keys) { 
            Commit c = commits.get(id); 
            printLog(c);
        }
    }

    // ==================================================================================
    // print the shaid for commits with the given message 
    
    public void find(String message) { 
        commits = getCommits(); 
        boolean contains = false; 
        for(String key : commits.keySet()) { 
            if(commits.get(key).getMessage().equals(message)) { 
                System.out.println(commits.get(key).getSha1());
                contains = true;
            }

            if(!contains) { 
                throw new GitletException("Found no commit with that message.");
            }
        }
    }

    // ===================================================================================== 

    public String status() {
        // enumerate on all branches 
        ArrayList<String> branches = new ArrayList<>();

        // add * if it is the master branch else add it to branches 
        // master branch found in Branches/HEAD
        for(String branch : plainFilenamesIn(BRANCHES)) {
            if(!branch.equals("HEAD")) {
                if(branch.equals(readContentsAsString(HEAD))) {
                    branches.add("*" + branch);
                } else {
                    branches.add(branch);
                }
            }
        }

        // print staged files
        ArrayList<String> staged = new ArrayList<>();
        stage = getStage();
        for(String file : stage.getAdded().keySet()) {
            staged.add(file);
        }

        // 
        ArrayList<String> removed = stage.getRemoved();
        ArrayList<String> unstaged = new ArrayList<>();

        for(String file : plainFilenamesIn(CWD)) {
            byte[] cwdContents = readContents(join(CWD, file);

            if(getHead().getBlobs().containsKey(file) && join(BLOBS, getHead().getBlobs().get(file)).exists()) {
                byte[] commitContents = readContents(join(BLOBS, getHead().getBlobs().get(file));
                // unstaged and commit contents does not match cwd content.
                if(!Arrays.equals(cwdContents, commitContents) && !stage.getAdded().containsKey(file)/*unstaged */) {
                    unstaged.add(file + " (modified)");
                }
            }

            // staged but the content is diffrent of the current working dir
            if(stage.getAdded().containsKey(file) && !staged.contains(file)
                    && !cwdContents.equals(readContents(join(BLOBS, stage.getAdded().get(file)))))) {
                unstaged.add(file + " (modified)");
            }

        }

        // staged for addition but deleted from cwd 
        for(String file : stage.getAdded().keySet()) {
            if(!plainFilenamesIn(CWD).contains(file)) {
                unstaged.add(file + " (deleted)");
            }
        }
        // not staged for removal, but tracked in the current commit and deleted from cwd
        for(String file : getHead().getBlobs().keySet()) {
            if(!plainFilenamesIn(CWD).contains(file) && !stage.getRemoved().contains(file)) {) {
                unstaged.add(file + " (deleted)");
            }
        }

        // untracked files is present in cwd but not staged or trcked<saved in blobs>
        ArrayList<String> untracked = new ArrayList<>();
        for(String file : plainFilenamesIn(CWD)) {
            if(!getHead().getBlobs().containsKey(file) && !stage.getAdded().containsKey(file)) {
                untracked.add(file);
            }
        }

        statusOutput(branches, staged, removed, unstaged, untracked);
    }


    public void statusOutput(ArrayList<String> branches,
                             ArrayList<String> staged,
                             ArrayList<String> removed,
                             ArrayList<String> unstaged,
                             ArrayList<String> untracked) {


        System.out.println("==== Branches ======");
        for(String branch : branches) {
            System.out.println(branch);
        }

        System.out.print("\n");
        System.out.println("=== Staged Files === ");
        for(String file : staged) {
            System.out.println(file);
        }


        System.out.print("\n");
        System.out.println("=== Removed Files === ");
        for(String file : removed) {
            System.out.println(file);
        }


        System.out.print("\n");
        System.out.println("=== Modifications Not Staged For Commit ===");
        for(String file : unstaged) {
            System.out.println(file);
        }


        System.out.print("\n");
        System.out.println("=== Untracked Files === ");
        for(String file : untracked) {
            System.out.println(file);
        }

        System.out.println("\n");

    } 

    public void checkout(String... args) { 
        if(args.length == 3)  
            checkOut1(args); 
        
        if(args.length == 4) 
            checkOut2(args); 
        
        if(args.length == 2) 
            checkOut3(args);
    }

    // takes nultiple args chech third arg filename
    // overwrite the file present in the WD with content of file in the head commit
    public void checkOut1(String... args) {
        String fileName = args[2];
        Commit headCommit = getHead();
        if(!headCommit.getBlobs().containsKey(fileName)) {
            throw new GitletException("file does not exit in that commit");
        }

        if(headCommit.getBlobs().containsKey(fileName)) {
            overWriteBlob(fileName, headCommit);
        }

    }

    // takes commit id and file name
    public void checkOut2(String... args) {
        String commitId = abbrevatedSha(args[1]);
        String fileName = args[3];
        if(!args[2].equals("--")) {
            throw new GitletException("incorrect operand");
        }

        commits = getCommits();
        Commit targetedCommit = commits.get(commitId);

        if(targetedCommit == null) {
            throw new GitletException("no commit with taha id exists");
        }

        if(targetedCommit.getBlobs().containsKey(fileName)) {
            overWriteBlob(fileName, targetedCommit);
        } else {
            throw new GitletException("file does not exist in that commit")
        }


    }

    // params branch name, checkout all files in the head of the specified branch.
    public void checkOut3(String... args) { 
        String branchName = args[1]; 
        File branch = join(BRANCHES, branchName); 
        commits = getCommits(); 

        if(!join(BRANCHES, branchName).exists()) { 
            throw new GitletException("no such branch exists"); 
        }

        Commit branchHead = commits.get(readContentsAsString(branch)); 
        Commit currentHead = getHead();

        // If a working file is untracked in the current branch and would be overwritten by the checkout 
        for(String file : plainFilenamesIn(CWD)) { 
            if(!currentHead.getBlobs().containsKey(file)) { 
                if(branchHead.getBlobs().containsKey(file)) { 
                    byte[] cwdContents = readContents(join(CWD, file)); 
                    byte[] overWriteContents = readContents(join(BLOBS, branchHead.getBlobs().get(file))); 
                    if(!cwdContents.equals(overWriteContents)) { 
                        throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
                    }
                }
            }
        } 

        // check if the the current branch is the head branch 
        if(branchName.equals(readContentsAsString(HEAD)) { 
            throw new GitletException("no need to checkout the current branch"); 
        }

        // overwrite files in the cwd with the files in the given branch. 
        for(String file : branchHead.getBlobs().keySet()) { 
            overWriteBlob(file, branchHead);
        } 

        // files tracked in the current branch but not presnted in the checked-out branch are deleted.
        for(String file : getHead().getBlobs().keySet()) { 
            if(!branchHead.getBlobs().keySet().contains(file)) 
                restrictedDelete(file);
        }
        // clear the staging area.
        stage.clear(); 

        // update the head branch
        writeContents(HEAD, branchName); 


    } 
    // overwrite the content in the CWD with the content of the commit 
    public void overWriteBlob(File fileName, Commit commit) {
        String blobSha = commit.getBlobs().get(fileName);
        File blobPath = join(BLOBS, blobSha);
        byte[] writtenBlob = readContents(blobPath);
        File overwrittenFile = join(CWD, fileName);
        writeContents(overwrittenFile, writtenBlob);
    }

    
    public String abbrevatedSha(String id) {
        final int len = 40;

        if(id.length() == len)
            return id;

        commits = getCommits();
        for(String key : commits.keySet()) {
            if(key.startsWith(id))
                return key;
        }

        throw new GitletException("commit doesn't exists");
    }

    // create new branch and points it at the current head commit
    public void branch(String branchName) { 
        for(String branch : plainFilenamesIn(BRANCHES)) { 
            if(branch.equals(branchName)) { 
                throw new GitletException("A branch with that name is already exists");
            }
        }

        File newBranch = join(BRANCHES, branchName); 
        writeContents(newBranch, getHead().getSHA());
    }

    // remove the HEAD pointer of the specified branch.  
    // 
    public void rmvBranch(String branchName) {  
        // if the branch is the current active branch do not remove
        if(branchName.equalsIgnoreCase(readContentsAsString(HEAD))) { 
            throw new GitletException("can not remove the current branch."); 
        } else if(join(BRANCHES, branchName).exists()) { 
            join(BRANCHES, branchName).delete();
        } else { 
            throw new GitletException("a Branch with that name does'nt exists");
        }
    }

    public void reset(String commitId) { 
        String currBranch = readContentsAsString(HEAD); 
        commits = getCommits(); 
        stage = getStage(); 

        if(!commits.contains(commitId)) { 
            throw new GitletException("no commit with that id exists"); 
        }

        Commit newHead = commits.get(commitId); 
        // file not tracked in the current head and will be overwritten.
        for(String file : plainFilenamesIn(CWD)) { 
            if(!getHead().getBlobs().containsKey(file) && newHead.getBlobs().containsKey(file)) { 
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
        // overwrite each file in the newHead
        for(String file : newHead.getBlobs().keySet()) { 
            overWriteBlob(file, newHead);
        } 

        // delete file presents in the cwd and not founded in the newHead
        for(String file : plainFilenamesIn(CWD)) { 
            if(!newHead.getBlobs().containsKey(file)) { 
                restrictedDelete(file);
            }
        }   
        // clear stage
        stage.clear();
        // update stage state 
        writeObject(STAGING, stage);  
        // update the current branch
        writeContents(join(BRANCHES, currBranch), newHead.getSHA()); 
        // move the head pointer 
        writeContents(HEAD, currBranch);
    }  


    public void merge(String branchName) { 
        if(!join(BRANCHES, branchName).exists()) { 
            throw new GitletException("A branch with that name does not exist.");
        }
        if(branchName.equals(readContentsAsString(HEAD))) { 
            throw new GitletException("can not merge  branch with itself");
        }
        stage = getStage();
        if(!stage.getAdded().keySet().isEmpty() || stage.getRemoved().isEmpty()) { 
            throw new GitletException("there is not committed work"); 
        }
        boolean conflict = false;
        commits = getCommits(); 
        Commit head = getHead(); 
        Commit mergeHead = commits.get(readContentsAsString(join(BRANCHES, branchName))); 
        Commit commonAncestor = commits.get(splitCommit(head, mergeHead));

        if(commonAncestor.getSHA().equals(mergeHead.getSHA())) { 
            throw new GitletException("Given branch is an ancestor of the current branch");
        }

        if(whoIsYourParent(head).contains(mergeHead.getSHA()) || whoIsYourParent(mergeHead).contains(head.getSHA())) { 
            checkout("checkout", branchName); 
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        for(String file: plainFilenamesIn(CWD)) { 
            if(!getHead().getBlobs().containsKey(file) && mergeHead.getBlobs().containsKey(file)) { 
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first");
            } 
        }

        Set<String> allFiles = new TreeSet<>(head.getBlobs().keySet()); 
        allFiles.add(mergeHead.getBlobs().keySet()); 
        allFiles.add(commonAncestor.getBlobs().keySet());
        int flag = 0; 
        for(String file : allFiles) { 
            boolean conflictOcuured = mergeLogic(commonAncestor, head, mergeHead, file); 
            if(conflictOcuured) { 
                flag++;
            }
        } 

        if(flag > 0) { 
            conflict = true;
        }

        if(stage.getAdded().isEmpty() && stage.getRemoved().isEmpty()) { 
            throw new GitletException("No changes added to the commit.");
        } 
        commit("Merged" + branchName +"into"+ readContentsAsString(HEAD)+".", mergeHead.getSHA()); 
        if(conflict) { 
            System.out.println("encounterd a merge conflict")
        }

    }


    // logic for merging algorithm, specifying what happen to files in branches when you merge them  
    public boolean mergeLogic(Commit split, Commit headBranch, Commit otherBranch, String file) { 
        HashMap<String, String> splitBlobs = split.getBlobs(); 
        HashMap<String, String> headBlobs = headBranch.getBlobs(); 
        HashMap<String, String> otherBlobs = otherBranch.getBlobs(); 

        stage = getStage();

        // if split contains file and this file unchanged in headBranch and changed in other branch check to other branch 
        if(splitBlobs.containsKey(file)) { 
            if(splitBlobs.get(file).equals(headBlobs.get(file)) && !splitBlobs.get(file).equals(otherBlobs.get(file)) 
                && otherBlobs.containsKey(file)) { 
                checkout("checkout", otherBranch.getSHA(), "--", file); 
                stage.add(file, otherBranch.get(file));
                writeObject(STAGING, stage);
                return false;
            }

            // if file in split changed in headBranch and and not changed in other keep it.
            if(splitBlobs.get(file).equals(otherBlobs.get(file)) && !splitBlobs.get(file).equals(headBlobs.get(file)) 
                && headBlobs.containsKey(file)) { 
                    return false;
            }

            // if file presents in boths commits and  equals or not presnts in both keep it, and no conflict.
            if(headBlobs.containsKey(file) && otherBlobs.containsKey(file) && otherBlobs.get(file).equals(headBlobs.get(file)) 
                || !headBlobs.containsKey(file) && otherBlobs.containsKey(file)) { 
                    return false;
            }

            // if file in head equal file in split and not presnt in other delet it. stage file and update.
            if(splitBlobs.get(file).equals(headBlobs.get(file)) && !otherBlobs.containsKey(file)) { 
                rm(file); 
                writeObject(STAGING, stage);
                return false; 
            }

            // file in split equals file in other and not presnts in head branch not doing anything.
            if(splitBlobs.get(file).equals(otherBlobs.get(file) && !headBranch.containsKey(file))) { 
                return false;
            }
        }

        // file not presnt in split commit 
        if(!splitBlobs.containsKey(file)) { 
            // file presnts in head commit not in other commit
            if(headBlobs.containsKey(file) && !otherBlobs.containsKey(file)) { 
                return false;
            }

            // file not in head and presents in other commit checkout to other
            if(!headBlobs.containsKey(file) && otherBlobs.containsKey(file)) { 
                checkout("checkout", other.getSHA(), "--", file); 
                stage.add(file, otherBlobs.get(file));
                writeObject(STAGING, stage); 
                return false;
            }
        }

        // merge conflict
        // file in split and not equals other and head, and head and other not equal 
        // file in head and split and not equals, and not found in other 
        // file in other and split and not equals, and not found head
        //  not in split and othe and head not equals
        if(splitBlobs.containsKey(file) && !splitBlobs.get(file).equals(headBlobs.get(file)) 
            && !splitBlobs.get(file).equals(otherBlobs.get(file))
            && headBlobs.containsKey(file) && otherBlobs.containsKey(file)
            && !headBlobs.get(file).equals(otherBlobs.get(file))  
            || splitBlobs.containsKey(file) && headBlobs.containsKey(file) 
            && !splitBlobs.get(file).equals(headBlobs.get(file)) && !otherBlobs.containsKey(file) 
            || splitBlobs.containsKey(file) && otherBlobs.containsKey(file) 
            && !splitBlobs.get(file).equals(otherBlobs.get(file)) && !headBlobs.containsKey(file) 
            || !splitBlobs.containsKey(file) && !otherBlobs.get(file).equals(headBlobs.get(file))) { 

            mergeConflict(headBlobs, otherBlobs, file);
        }
        writeObject(STAGING, stage);
        return false;
    }

    // list of commit parents
    public String whoIsYourParent(Commit c) { 
        ArrayList<String> ancestry = new ArrayList<>(); 
        ancestry.add(c.getSHA()); 

        while(c != null) { 
            if (c.getMergeParent() != null) { 
                ancestry.add(c.getMergeParent());
            }

            ancestry.add(c.getParent()); 
            if(c != null) { 
                c = commits.get(c.getParent());
            } else { 
                break;
            }
        }
        return ancestry;
    } 

    // find the split commit of two commits
    public String splitCommit(Commit current, Commit other) { 
        commits = getCommits(); 

        ArrayList<String> headAncestors = whoIsYourParent(current); 
        ArrayList<String> otherAncestors = whoIsYourParent(other); 

        for(String ancestor : headAncestors) { 
            if(otherAncestors.contains(ancestor)) { 
                return ancestor;
            }
        }
        return null;
    }

    // handle merge conflict
    public boolean mergeConflict(HashMap<String, String> head, HashMap<String, String> other, File file) { 
        if(head.contains(file)) { 
            headContent = readContentsAsString(join(BLOBS, head.get(file)));
        } else { 
            headContent = " ";
        }

        if(other.contains(file)) { 
            otherContent = readContentsAsString(join(BLOBS, other.get(file)));
        } else { 
            otherContent = " "; 
        }

        File fileConflicted = join(CWD, file); 
         String contents = "<<<<<<< HEAD" + "\n"
                + headContents
                + "=======" + "\n"
                + otherContents
                + ">>>>>>>" + "\n"; 
        
        writeContents(fileConflicted, content); 
        Blob newBlob = new Blob(fileConflicted); 
        stage.add(file, newBlob.getSHA()); 
        writeObject(STAGING, stage);
        return true;
    }


    

    public File getRepository() { 
        return GITLETREPO;
    }
}
