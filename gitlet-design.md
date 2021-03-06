# Gitlet Design Document



**Name**: Elaraby Elaidy

# Classes and Data Structures

# Structur of working directory 



## Commit

### Fields
1. **Sha1:** The SHA-1 hash of the commit.
2. **message**: The commit message.
3. **parent**: sha of parent commit. 
4. **mergeParent**: The merge parent commit.
5. **timeStamp**: The time stamp of the commit.
6. **blobs** : hashmap conatins file name key and sha1 val of file of all blobs in the commit. 
7. **date** : the date of the commit.

### Methods
- **constructor**: constructor of the commit class, initial commit is made with empty parent and merge parent and date(0).
- **getSha1**: returns the sha1 of the commit, serialize the content of commit with "commit" distinct from other sha1s. 
- **getMessage**: returns the message of the commit.
- **getParent**: returns the parent of the commit.
- **getMergeParent**: returns the merge parent of the commit.
- **getTimeStamp**: returns the time stamp of the commit formatted.
- **getBlobs**: return blobs exisiting in the commit. 
- **getDate**: returns the date of the commit.


### Blob

### Fields

1. **SHA:** The SHA-1 hash of the blob. 
2. **content:** content of the source file as a stream of bytes. 
3. **fileName:** name of the source file.


### Methods
- **constructor**: constructor of the blob class, initial blob is made with empty content and fileName.
- **getSHA**: returns the sha1 of the blob, serialize the content of blob with "blob" distincted from other sha1s. 
- **getFileName**: returns the fileName of the blob. 
- **getContent**: returns the content bytes of the blob.



### StagingArea

#### Fields

1. Field 1
2. Field 2


## Repository

### Fields

1. Field 1
2. Field 2


## Methods
- **Status**: 
    - **Print Branches**find the master branch denoted it with * add rest of branches. 
    - **Print Staged files**: read content of stage file add all file to staged list. 
    - **Print Removed files**: files staged to remove. 
    - **Print Modified files**:. 
        - tracked in the current commit, changed in cwd, but not staged for commit. 
        - staged but with diffrent content than the WD. 
        - staged for addition, but deleted the WD. 
        - Not staged for removal, but tracked in the current commit and deleted from the working directory.  
    - **Print Untracked files**: files not tracked if it is not staged or tracked and present in WD .


- **Checkout**: all what it does is moving the head pointer to a specific commit or branch. 

- **CheckOut1**: back in time to the previous version of file.
    - overwrite the content of the file with the content of the file commit in the head commit. 
    - find file in head commit by name and get the sha1 of the file, read content of the path blob/sha. 
    - write the content of blob/sha in cwd/filename. 

- **CheckOut2**: overwrite content in the wd with the specfiied file in the specified commit.  
    - takes two args commit id and file name.
    - find the file name in the commit and overwrite the content of that file in the wd.
  
- **CheckOut3**: checkout to a specific branch. 
    - takes the branch name.
    - puts all files in the commit of the head of the branch in the wd, overwrite files if exist. 
    - any file tracked in the current branch but not in the commit of the branch is removed.
    - update the head branch to the specified branch. 

- **rmvBranch**: delete the head pointer of a specified branch.
    - if not find throw exception.
    - if it is the active branch throw exception.
    - delete the pointer only.

- **overWriteBlob**: overwrite the content of a file in the wd with the file in a specified commit. 
    - takes the commit id and file name. 
    - overwrite the content of the file in the wd with the content of the file in the commit.

- **merge**: 


- **mergeLogic**: specify what happens to files when we merge.
    - takes headCommit and mergeCommit and splitCommit and file. 
    -  if split contains file and this file unchanged in headBranch and changed in other branch check to other branch. 
    - if file in split changed in headBranch and and not changed in other keep it.
    - if file presents in boths commits and  equals or not presnts in both keep it, and no conflict. 
    - if file in head equal file in split and not presnt in other delet it. stage file and update. 
    - file in split equals file in other and not presnts in head branch not doing anything. 
    - file not presnt in split commit and file presnts in head commit not in other commit 
    - file not in head and presents in other commit checkout to other. 
    -  **encounterd merge conflict**: when we have merge conflict.
        - file in split and not equals other and head, and head and other not equal 
        - file in head and split and not equals, and not found in other 
        - file in other and split and not equals, and not found head
        - not in split and othe and head not equals

- **SplitCommit**: find split commit of two commits 
    -  find if there is a split commit between two commits. 

- **whoIsYourParent**: list parents of the current commit. 
    - return list of parents of the commit.


- **mergeConflict**: handle merge conflict.
    - takes the haedCommit and beanchCommit and file. 
    - write both conetent in the file. 
    - create new blob of the file and stage file. 



## Algorithms

## Persistence
