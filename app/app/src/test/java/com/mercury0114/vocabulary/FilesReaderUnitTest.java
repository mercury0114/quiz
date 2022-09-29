package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;
import static com.mercury0114.vocabulary.FilesReader.FileNotFolderException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilesReaderUnitTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void test1() throws IOException {
        File emptyFolder = temporaryFolder.newFolder("empty_folder");
        assertEquals(GetFilesNames(emptyFolder).size(), 0);
    }

    @Test
    public void test2() throws IOException {
        File oneFileFolder = temporaryFolder.newFolder("one_file_folder");
        File file = temporaryFolder.newFile("one_file_folder/file.txt");
        assertEquals(GetFilesNames(oneFileFolder).size(), 1);
    }

    @Test
    public void test3() throws IOException {
        File file = temporaryFolder.newFile("file.txt");
        FileNotFolderException exception = 
            assertThrows(FileNotFolderException.class, () -> GetFilesNames(file));
        assertEquals("file.txt", exception.getMessage());
    }
}
