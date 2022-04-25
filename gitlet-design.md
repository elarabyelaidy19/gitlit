# Gitlet Design Document

![uml diagram](Main.png)

**Name**: Elaraby Elaidy

# Classes and Data Structures

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

#### Fields

1. Field 1
2. Field 2



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
    - 







## Algorithms

## Persistence
