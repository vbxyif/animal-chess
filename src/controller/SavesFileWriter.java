package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SavesFileWriter extends FileWriter {

    public SavesFileWriter(File file) throws IOException {
        super(file);
    }

}
