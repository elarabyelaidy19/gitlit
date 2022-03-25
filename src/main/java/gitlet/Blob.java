package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.MyUtils.getObjectFile;
import static gitlet.MyUtils.saveObjectFile;
import static gitlet.Utils.*;
public class Blob implements Serializable {

    // Source file
    private final File source;

    // content of source file
    private final byte[] content;

    // sha1 id generated from source file
    private final String id;

    // instance of blob generated from sha id
    private final File file;

    public Blob(File sourceFile) {
        source = sourceFile;
        String filePath = sourceFile.getPath();
        content = readContents(sourceFile);
        id = sha1(filePath, content);
        file = getObjectFile(id);
    }

    // generate sha1 id
    public static String generateId(File sourceFile) {
        String filePath = sourceFile.getPath();
        byte[] fileContent = readContents(sourceFile);
        return sha1(filePath, fileContent);
    }

     // get Blob instance from id
    public static Blob formFile(String id) {
        return readObject(getObjectFile(id), Blob.class);
    }

    // save blob instance to file in objects dir
    public void save() {
        saveObjectFile(file, this);
    }

    // get blob content as String
    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    // wirit file content to source file
    public void writContentToSource() {
        writeContents(source, content);
    }

    public String getId() {
        return id;
    }

    public File getFile() {
        return file;
    }







}
