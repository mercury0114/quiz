package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.stream.Stream;

public class FilesReader {

    public static class FileNotFolderException extends RuntimeException {
        private FileNotFolderException(String filePath) {
            super(filePath);
        }
    }

    public static final ImmutableList<String> GetFilesNames(String name) {
        return GetFilesNames(new File(name));
    }

    public static final ImmutableList<String> GetFilesNames(File folder) {
        if (!folder.isDirectory()) {
            throw new FileNotFolderException(folder.getName());
        }
        return Stream.of(folder.listFiles()).map(File::getName).collect(toImmutableList());
    }    
}
