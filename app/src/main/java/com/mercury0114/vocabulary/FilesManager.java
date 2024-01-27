package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.mercury0114.vocabulary.QuestionAnswer.Column;
import static com.mercury0114.vocabulary.QuestionAnswer.extractQuestionAnswer;
import static java.util.Collections.sort;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FilesManager {
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

  public static class DuplicateStringException extends RuntimeException {
    private DuplicateStringException(String string) {
      super("Duplicate string found: " + string);
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

  static boolean isStatisticsFile(String fileName) {
    return fileName.contains("_statistics_LEFT") || fileName.contains("_statistics_RIGHT");
  }

  static void writeToFile(String filePath, ImmutableList<String> lines, Column column) {
    ImmutableList<QuestionAnswer> entries =
        lines.stream()
            .map(line -> extractQuestionAnswer(line, Column.LEFT))
            .collect(toImmutableList());

    ImmutableList<String> questions =
        entries.stream().map(entry -> entry.question).collect(toImmutableList());
    ImmutableList<String> answers =
        entries.stream().map(entry -> entry.answer).collect(toImmutableList());
    switch (column) {
      case LEFT:
        checkNoDuplicateStrings(questions);
        break;
      case RIGHT:
        checkNoDuplicateStrings(answers);
        break;
      case BOTH:
        checkNoDuplicateStrings(questions);
        checkNoDuplicateStrings(answers);
        break;
    }
    try {
      Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new RuntimeException("Failed to update the file contents", exception);
    }
  }

  private static void checkNoDuplicateStrings(ImmutableList<String> strings) {
    Set<String> uniqueStrings = new HashSet<>();
    for (String string : strings) {
      if (uniqueStrings.contains(string)) {
        throw new DuplicateStringException(string);
      }
      uniqueStrings.add(string);
    }
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
