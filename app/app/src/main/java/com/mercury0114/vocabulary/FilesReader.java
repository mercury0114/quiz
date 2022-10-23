package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FilesReader {
  public static final String VOCABULARY_PATH =
      "/storage/emulated/0/Download/com.mercury0114.vocabulary/vocabulary/";

  public static class FileNotFolderException extends RuntimeException {
    private FileNotFolderException(String filePath) { super(filePath); }
  }
  public static class EmptyFileException extends RuntimeException {
    private EmptyFileException(String filePath) { super(filePath); }
  }

  public static final ImmutableList<String> GetFilesNames(String name) {
    return GetFilesNames(new File(name));
  }

  public static final ImmutableList<String> GetFilesNames(File folder) {
    if (!folder.isDirectory()) {
      throw new FileNotFolderException(folder.getName());
    }
    return Stream.of(folder.listFiles())
        .map(File::getName).sorted()
        .collect(toImmutableList());
  }

  public static List<String> readFileContent(File file) throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
    if (lines.isEmpty()) {
      throw new EmptyFileException(file.getPath());
    }
    return lines;
  }
}
