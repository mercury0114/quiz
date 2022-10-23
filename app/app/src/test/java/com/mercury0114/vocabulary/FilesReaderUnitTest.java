package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.EmptyFileException;
import static com.mercury0114.vocabulary.FilesReader.FileNotFolderException;
import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;
import static com.mercury0114.vocabulary.FilesReader.readFileContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesReaderUnitTest {
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
  public void readFileContent_emptyFile_throwsException() throws IOException {
    File file = temporaryFolder.newFile("file.txt");
    assertThrows(EmptyFileException.class, () -> readFileContent(file));
  }

  @Test
  public void readFileContent_readsLines() throws IOException {
    String line0 = "question0, answer0";
    String line1 = "question1, answer1";
    File file = temporaryFolder.newFile("file.txt");
    ArrayList<String> linesList = new ArrayList();
    linesList.add(line0);
    linesList.add(line1);
    Files.write(Paths.get(file.getPath()), linesList, StandardCharsets.UTF_8);

    List<String> lines = readFileContent(file);
    assertEquals(lines.get(0), line0);
    assertEquals(lines.get(1), line1);
  }
}
