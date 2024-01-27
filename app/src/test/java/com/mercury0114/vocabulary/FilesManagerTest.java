package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesManager.DuplicateStringException;
import static com.mercury0114.vocabulary.FilesManager.FileNotFolderException;
import static com.mercury0114.vocabulary.FilesManager.GetFilesNames;
import static com.mercury0114.vocabulary.FilesManager.computeStatisticsFilePath;
import static com.mercury0114.vocabulary.FilesManager.isStatisticsFile;
import static com.mercury0114.vocabulary.FilesManager.readLinesAndSort;
import static com.mercury0114.vocabulary.FilesManager.writeToFile;
import static com.mercury0114.vocabulary.QuestionAnswer.Column;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesManagerTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void getFilesNames_emptyFolder_returnsEmptyList() throws IOException {
    File emptyFolder = temporaryFolder.newFolder("empty_folder");
    assertEquals(GetFilesNames(emptyFolder).size(), 0);
  }

  @Test
  public void getFilesNames_oneFile_returnsOneName() throws IOException {
    File oneFileFolder = temporaryFolder.newFolder("one_file_folder");
    File file = temporaryFolder.newFile("one_file_folder/file.txt");
    assertEquals(GetFilesNames(oneFileFolder).size(), 1);
  }

  @Test
  public void getFilesNames_notFolder_throwsException() throws IOException {
    File file = temporaryFolder.newFile("file.txt");
    FileNotFolderException exception =
        assertThrows(FileNotFolderException.class, () -> GetFilesNames(file));
    assertEquals("file.txt", exception.getMessage());
  }

  @Test
  public void getFilesNames_returnsSortedList() throws IOException {
    File folder = temporaryFolder.newFolder("folder");
    temporaryFolder.newFile("folder/b");
    temporaryFolder.newFile("folder/c");
    temporaryFolder.newFile("folder/a");
    List<String> names = GetFilesNames(folder);
    assertEquals(names.size(), 3);
    assertEquals(names.get(0), "a");
    assertEquals(names.get(1), "b");
    assertEquals(names.get(2), "c");
  }

  @Test
  public void readLinesAndSort_returnsSortedList() throws IOException {
    File file = temporaryFolder.newFile("file.txt");
    ImmutableList<String> fileContent =
        ImmutableList.of("question1, answer1", "question0, answer0");

    Files.write(Paths.get(file.getPath()), fileContent, StandardCharsets.UTF_8);

    ImmutableList<String> lines = readLinesAndSort(file);
    assertEquals(lines.get(0), "question0, answer0");
    assertEquals(lines.get(1), "question1, answer1");
  }

  @Test
  public void computeStatisticsFilePath_concatenatesStringsCorrectly() {
    assertEquals("file.txt_statistics_LEFT", computeStatisticsFilePath("file.txt", Column.LEFT));
    assertEquals("file.txt_statistics_RIGHT", computeStatisticsFilePath("file.txt", Column.RIGHT));
    assertEquals("other.txt_statistics_LEFT", computeStatisticsFilePath("other.txt", Column.LEFT));
  }

  @Test
  public void isStatisticsFile_returnsTrueForStatisticsFile() {
    assertEquals(true, isStatisticsFile("file.txt_statistics_LEFT"));
    assertEquals(true, isStatisticsFile("file.txt_statistics_RIGHT"));
    assertEquals(true, isStatisticsFile("other_file.txt_statistics_LEFT"));
    assertEquals(true, isStatisticsFile("other_file.txt_statistics_RIGHT"));
    assertEquals(true, isStatisticsFile("path/to/file.txt_statistics_LEFT"));
    assertEquals(true, isStatisticsFile("path/to/file.txt_statistics_RIGHT"));
  }

  @Test
  public void isStatisticsFile_returnsFalseForRegularFile() {
    assertEquals(false, isStatisticsFile("file.txt"));
    assertEquals(false, isStatisticsFile("path/to/file.txt"));
    assertEquals(false, isStatisticsFile("path/to/file_with_underscores.txt"));
  }

  @Test
  public void isStatisticsFile_returnsFalseToMalformedStatisticsFilePath() {
    assertEquals(false, isStatisticsFile("file.txt_statistics_BOTH"));
    assertEquals(false, isStatisticsFile("file.txt_stats_LEFT"));
    assertEquals(false, isStatisticsFile("file.txt_LEFT_statistics"));
    assertEquals(false, isStatisticsFile("path/to/file.txt_LEFT_statistic"));
  }

  @Test
  public void writeToFile_checkEnabledForLeftColumn_duplicateQuestions_throwsException() {
    ImmutableList<String> lines = ImmutableList.of("question | answer1", "question | answer2");
    assertThrows(
        DuplicateStringException.class, () -> writeToFile("/tmp/file.txt", lines, Column.LEFT));
  }

  @Test
  public void writeToFile_checkEnabledForRightColumn_duplicateAnswers_throwsException() {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer", "question2 | answer");
    assertThrows(
        DuplicateStringException.class, () -> writeToFile("/tmp/file.txt", lines, Column.RIGHT));
  }

  @Test
  public void writeToFile_checkEnabledForBothColumns_duplicateQuestions_throwsException() {
    ImmutableList<String> lines = ImmutableList.of("question | answer1", "question | answer2");
    assertThrows(
        DuplicateStringException.class, () -> writeToFile("/tmp/file.txt", lines, Column.BOTH));
  }

  @Test
  public void writeToFile_checkEnabledForBothColumns_duplicateAnswers_throwsException() {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer", "question2 | answer");
    assertThrows(
        DuplicateStringException.class, () -> writeToFile("/tmp/file.txt", lines, Column.BOTH));
  }
}
