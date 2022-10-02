package com.mercury0114.vocabulary;

import static org.junit.Assert.assertEquals;

import com.mercury0114.vocabulary.VocabularyChecker;
import com.mercury0114.vocabulary.VocabularyChecker.QuestionAnswer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class VocabularyCheckerUnitTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private File file;

  @Before
  public void setUp() throws IOException {
    file = temporaryFolder.newFile("file.txt");
  }

  @Test
  public void test0() throws IOException {
    VocabularyChecker checker = new VocabularyChecker(2);
    ArrayList<QuestionAnswer> questions = checker.prepareInitialQuestions(file);
    assertEquals(questions.size(), 0);
  }

  @Test
  public void test1() throws IOException {
    String[] lines = {"word1, translation1"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(2);
    ArrayList<QuestionAnswer> questions = checker.prepareInitialQuestions(file);
    assertEquals(questions.size(), 2);
  }

  @Test
  public void test2() throws IOException {
    String[] lines = {"word1, translation1", "word2, translation2"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(2);
    ArrayList<QuestionAnswer> questions = checker.prepareInitialQuestions(file);
    assertEquals(questions.size(), 4);
  }

  @Test
  public void test3() throws IOException {
    String[] lines = {"word1, translation1"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(2);
    ArrayList<QuestionAnswer> questions = checker.prepareInitialQuestions(file);
    assertEquals(questions.size(), 2);
    assertQuestionAnswerEquals(questions.get(0), "word1", "translation1");
    assertQuestionAnswerEquals(questions.get(1), "word1", "translation1");
  }

  @Test
  public void test4() throws IOException {
    String[] lines = {"word1, translation1", "word2, translation2"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(2);
    ArrayList<QuestionAnswer> questions = checker.prepareInitialQuestions(file);
    assertEquals(questions.size(), 4);
    assertQuestionAnswerEquals(questions.get(0), "word1", "translation1");
    assertQuestionAnswerEquals(questions.get(1), "word1", "translation1");
    assertQuestionAnswerEquals(questions.get(2), "word2", "translation2");
    assertQuestionAnswerEquals(questions.get(3), "word2", "translation2");
  }

  @Test
  public void test5() throws IOException {
    String[] lines = {"word1, translation1"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(3);
    assertEquals(checker.prepareInitialQuestions(file).size(), 3);
  }

  private void assertQuestionAnswerEquals(QuestionAnswer qa, String question,
                                          String answer) {
    assertEquals(qa.question, question);
    assertEquals(qa.answer, answer);
  }

  private void writeLines(File file, String[] lines) throws IOException {
    ArrayList<String> linesList = new ArrayList();
    for (String line : lines) {
      linesList.add(line);
    }
    Files.write(Paths.get(file.getPath()), linesList, StandardCharsets.UTF_8);
  }
}
