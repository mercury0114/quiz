package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.VocabularyChecker.EmptyFileException;
import static com.mercury0114.vocabulary.VocabularyChecker.NoQuestionsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import com.mercury0114.vocabulary.QuestionAnswer.Column;
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
    VocabularyChecker checker = new VocabularyChecker(2, Column.LEFT);
    assertThrows(EmptyFileException.class,
                 () -> checker.prepareQuestions(file));
  }

  @Test
  public void constructor_penaltyFactor0_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
                 () -> new VocabularyChecker(0, Column.LEFT));
  }

  @Test
  public void prepareQuestions_twoLines_penaltyFactor3_6Questions()
      throws IOException {
    String[] lines = {"question1, answer1", "question2, answer2"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 3);
    assertEquals(checker.questionsRemaining(), 6);
  }

  @Test
  public void nextQuestion_asksQuestionFooInList() throws IOException {
    String[] lines = {"question_foo, answer_foo"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    String question = checker.nextQuestion();
    assertEquals(question, "question_foo");
  }

  @Test
  public void nextQuestion_asksQuestionBarInList() throws IOException {
    String[] lines = {"question_bar, answer_bar"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    String question = checker.nextQuestion();
    assertEquals(question, "question_bar");
  }

  @Test
  public void nextQuestion_noQuestions_throwsException()
      throws NoQuestionsException {
    VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
    assertThrows(NoQuestionsException.class, () -> checker.nextQuestion());
  }

  @Test
  public void checkAnswer_pops1Adds1() throws IOException {
    String[] lines = {"question, answer"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("answer");
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("wrong_answer");
    assertEquals(checker.questionsRemaining(), 2);
  }

  @Test
  public void checkAnswer_pops1() throws IOException {
    String[] lines = {"question, correct_answer"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("correct_answer");
    assertEquals(checker.questionsRemaining(), 0);
  }

  @Test
  public void checkAnswer_adds1_pops1_pops1() throws IOException {
    String[] lines = {"question1, answer1"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    checker.nextQuestion();
    checker.checkAnswer("wrong_answer");
    assertEquals(checker.questionsRemaining(), 2);
    checker.checkAnswer("answer1");
    assertEquals(checker.questionsRemaining(), 1);
    checker.nextQuestion();
    checker.checkAnswer("answer1");
    assertEquals(checker.questionsRemaining(), 0);
  }

  @Test
  public void nextQuestion_choosesRandomlyFirstQuestion() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    boolean[] asked = {false, false};
    int counter = 150;
    while (!asked[0] || !asked[1]) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(file);
      int questionIndex = Integer.parseInt(checker.nextQuestion());
      asked[questionIndex] = true;
      assertNotEquals(counter--, 0);
    }
  }

  @Test
  public void checkAnswer_popsCorrectQuestion() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    int counter = 150;
    while (counter > 0) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(file);
      boolean[] asked = {false, false};
      int question_index = Integer.parseInt(checker.nextQuestion());
      checker.checkAnswer(String.format("answer%d", question_index));
      assertEquals(checker.questionsRemaining(), 1);
      counter--;
    }
  }

  @Test
  public void checkAnswer_worksInAlwaysRightMode() throws IOException {
    String[] lines = {"answer, question"};
    VocabularyChecker checker =
        prepareVocabularyChecker(lines, 1, Column.RIGHT);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("question");
    assertEquals(checker.questionsRemaining(), 2);
    checker.checkAnswer("answer");
    assertEquals(checker.questionsRemaining(), 1);
  }

  @Test
  public void nextQuestion_sameQuestionAfterWrongAnswer() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    writeLines(file, lines);
    for (int i = 0; i < 150; i++) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(file);
      int question_index = Integer.parseInt(checker.nextQuestion());
      checker.checkAnswer("wrong_answer");
      assertEquals(checker.questionsRemaining(), 3);
      assertEquals(Integer.parseInt(checker.nextQuestion()), question_index);
      assertEquals(Integer.parseInt(checker.nextQuestion()), question_index);
    }
  }

  @Test
  public void revealAnswer_returnsAnswer() throws IOException {
    String[] lines = {"question, answer"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.revealAnswer(), "answer");
  }

  @Test
  public void revealAnswer_returnsAnswer2() throws IOException {
    String[] lines = {"question, answer2"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.revealAnswer(), "answer2");
  }

  @Test
  public void revealAnswer_addsPenalty1() throws IOException {
    String[] lines = {"question, answer"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.questionsRemaining(), 1);
    checker.revealAnswer();
    assertEquals(checker.questionsRemaining(), 2);
  }

  @Test
  public void revealAnswer_addsPenalty2() throws IOException {
    String[] lines = {"question, answer"};
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.questionsRemaining(), 2);
    checker.revealAnswer();
    assertEquals(checker.questionsRemaining(), 4);
  }

  @Test
  public void revealAnswer_movesToNextQuestion() throws IOException {
    String[] lines = {"0, answer0", "1, answer1"};
    int counter = 150;
    while (counter > 0) {
      VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
      assertEquals(checker.questionsRemaining(), 2);
      int index = Integer.parseInt(checker.nextQuestion());
      checker.revealAnswer();
      if (Integer.parseInt(checker.nextQuestion()) != index) {
        return;
      }
      counter--;
    }
  }

  @Test
  public void vocabularyChecker_askQuestionsFromAnotherColumn()
      throws IOException {
    String[] lines = {"left_column, right_column"};
    VocabularyChecker checker =
        prepareVocabularyChecker(lines, 1, Column.RIGHT);
    assertEquals(checker.nextQuestion(), "right_column");
    assertEquals(checker.revealAnswer(), "left_column");
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

  private VocabularyChecker prepareVocabularyChecker(String[] lines,
                                                     int penaltyFactor)
      throws IOException {
    return prepareVocabularyChecker(lines, penaltyFactor, Column.LEFT);
  }

  private VocabularyChecker
  prepareVocabularyChecker(String[] lines, int penaltyFactor, Column column)
      throws IOException {
    writeLines(file, lines);
    VocabularyChecker checker = new VocabularyChecker(penaltyFactor, column);
    checker.prepareQuestions(file);
    return checker;
  }
}
