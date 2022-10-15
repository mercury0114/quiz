package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.VocabularyChecker.EmptyFileException;
import static com.mercury0114.vocabulary.VocabularyChecker.NoQuestionsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class VocabularyCheckerTest {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private File file;

  @Before
  public void setUp() throws IOException {
    file = temporaryFolder.newFile("file.txt");
  }

  @Test
  public void prepareQuestions_emptyFile_throwsException() throws IOException {
    VocabularyChecker checker = new VocabularyChecker(2);
    assertThrows(EmptyFileException.class,
                 () -> checker.prepareQuestions(file));
  }

  @Test
  public void constructor_penaltyFactor0_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
                 () -> new VocabularyChecker(0));
  }

  @Test
  public void prepareQuestions_twoLines_penaltyFactor3_6Questions()
      throws IOException {
    VocabularyChecker checker = new VocabularyChecker(3);
    String[] lines = {"question1, answer1", "question2, answer2"};
    writeLines(file, lines);
    checker.prepareQuestions(file);
    assertEquals(checker.questionsRemaining(), 6);
  }

  @Test
  public void nextQuestion_asksQuestionFooInList() throws IOException {
    String[] lines = {"question_foo, answer_foo"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(1);
    checker.prepareQuestions(file);
    String question = checker.nextQuestion();
    assertEquals(question, "question_foo");
  }

  @Test
  public void nextQuestion_asksQuestionBarInList() throws IOException {
    String[] lines = {"question_bar, answer_bar"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(1);
    checker.prepareQuestions(file);
    String question = checker.nextQuestion();
    assertEquals(question, "question_bar");
  }

  @Test
  public void nextQuestion_noQuestions_throwsException()
      throws NoQuestionsException {
    VocabularyChecker checker = new VocabularyChecker(1);
    assertThrows(NoQuestionsException.class, () -> checker.nextQuestion());
  }

  @Test
  public void updateList_pops1Adds2() throws IOException {
    String[] lines = {"question, answer"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(2);
    checker.prepareQuestions(file);
    assertEquals(checker.nextQuestion(), "question");
    checker.updateList("answer");
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.nextQuestion(), "question");
    checker.updateList("wrong_answer");
    assertEquals(checker.questionsRemaining(), 3);
  }

  @Test
  public void updateList_pops1() throws IOException {
    String[] lines = {"question, correct_answer"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(1);
    checker.prepareQuestions(file);
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.nextQuestion(), "question");
    checker.updateList("correct_answer");
    assertEquals(checker.questionsRemaining(), 0);
  }

  @Test
  public void updateList_adds1_pops1_pops1() throws IOException {
    String[] lines = {"question1, answer1"};
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(1);
    checker.prepareQuestions(file);
    checker.nextQuestion();
    checker.updateList("wrong_answer");
    assertEquals(checker.questionsRemaining(), 2);
    checker.updateList("answer1");
    assertEquals(checker.questionsRemaining(), 1);
    checker.nextQuestion();
    checker.updateList("answer1");
    assertEquals(checker.questionsRemaining(), 0);
  }

  @Test
  public void nextQuestion_choosesRandomlyFirstQuestion() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    boolean[] asked = {false, false};
    int counter = 150;
    while (!asked[0] || !asked[1]) {
      VocabularyChecker checker = new VocabularyChecker(1);
      checker.prepareQuestions(file);
      int questionIndex = Integer.parseInt(checker.nextQuestion());
      asked[questionIndex] = true;
      assertNotEquals(counter--, 0);
    }
  }

  @Test
  public void nextQuestion_choosesRandomlyAllQuestions() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    boolean[] asked = {false, false};
    VocabularyChecker checker = new VocabularyChecker(1);
    checker.prepareQuestions(file);
    int counter = 150;
    while (!asked[0] || !asked[1]) {
      int questionIndex = Integer.parseInt(checker.nextQuestion());
      asked[questionIndex] = true;
      assertNotEquals(counter--, 0);
    }
  }

  @Test
  public void updateList_popsCorrectQuestion() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    int counter = 150;
    while (counter > 0) {
      VocabularyChecker checker = new VocabularyChecker(1);
      checker.prepareQuestions(file);
      boolean[] asked = {false, false};
      int question_index = Integer.parseInt(checker.nextQuestion());
      checker.updateList(String.format("answer%d", question_index));
      assertEquals(checker.questionsRemaining(), 1);
      counter--;
    }
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
