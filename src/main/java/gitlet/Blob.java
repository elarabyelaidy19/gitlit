package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;


public class Blob implements Serializable {

    public String getSHA;
    // content of source file
    private  byte[] content;

    // instance of blob generated from sha id
    private String fileName;

    public Blob(File sourceFile) {
        fileName = sourceFile.getName();
        content = readContents(sourceFile);
    }

    public byte[] getContent() {
        return content;
    }


    // generate sha1 id
    public String getSHA() {

        return sha1(serialize(this), "blob");
    }

    public String getFileName() {

        return fileName;
    }






}
