package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.mercury0114.vocabulary.QuestionAnswer.Column;
import static java.util.Collections.sort;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FilesReader {
  public static final String VOCABULARY_PATH =
      "/storage/emulated/0/Download/com.mercury0114.vocabulary/vocabulary/";

  public static class FileNotFolderException extends RuntimeException {
    private FileNotFolderException(String filePath) {
      super(filePath);
    }
  }

  public static class EmptyFileException extends RuntimeException {
    private EmptyFileException(String filePath) {
      super(filePath);
    }
  }

  static final ImmutableList<String> GetFilesNames(String name) {
    return GetFilesNames(new File(name));
  }

  static final ImmutableList<String> GetFilesNames(File folder) {
    if (!folder.isDirectory()) {
      throw new FileNotFolderException(folder.getName());
    }
    return Stream.of(folder.listFiles()).map(File::getName).sorted().collect(toImmutableList());
  }

  static ImmutableList<String> readLinesAndSort(File file) {
    List<String> lines = readFileContent(file.getPath());
    sort(lines);
    return ImmutableList.copyOf(lines);
  }

  static String computeStatisticsFilePath(String vocabularyFilePath, Column column) {
    return String.format("%s_%s_%s", vocabularyFilePath, "statistics", column.toString());
  }

  private static List<String> readFileContent(String filePath) {
    Path path = Paths.get(filePath);
    try {
      return Files.readAllLines(path, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }
}
