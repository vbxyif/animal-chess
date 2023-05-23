package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SavesFileWriter extends FileWriter {

    private SavesFileWriter(String fileName, boolean append) throws IOException {
        super(fileName, append);
    }

    public SavesFileWriter(File file) throws IOException {
        super(file);
    }

    public SavesFileWriter(File file, boolean append) throws IOException {
        super(file, append);
    }

    public static SavesFileWriter createSavesFileWriter(String fileName, boolean append) throws IOException {
        return new SavesFileWriter(fileName, append);
    }
}
