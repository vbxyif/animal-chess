package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SavesFileWriter extends FileWriter {

    private static String temporaryArchive;

    public SavesFileWriter(String fileName) throws IOException {
        super(fileName);
    }

    public SavesFileWriter(String fileName, boolean append) throws IOException {
        super(fileName, append);
    }

    public SavesFileWriter(File file) throws IOException {
        super(file);
    }

    public SavesFileWriter(File file, boolean append) throws IOException {
        super(file, append);
    }

}
