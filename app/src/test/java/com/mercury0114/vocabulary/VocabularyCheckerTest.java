package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.VocabularyChecker.NoQuestionsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.IOException;
import org.junit.Test;

public class VocabularyCheckerTest {

  @Test
  public void prepareQuestions_twoLines_penaltyFactor3_6Questions() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer1", "question2 | answer2");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 3);
    assertEquals(checker.questionsRemaining(), 6);
  }

  @Test
  public void nextQuestion_asksQuestionFooInList() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question_foo | answer_foo");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    String question = checker.nextQuestion();
    assertEquals(question, "question_foo");
  }

  @Test
  public void nextQuestion_asksQuestionBarInList() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question_bar | answer_bar");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    String question = checker.nextQuestion();
    assertEquals(question, "question_bar");
  }

  @Test
  public void nextQuestion_noQuestions_throwsException() throws NoQuestionsException {
    VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
    assertThrows(NoQuestionsException.class, () -> checker.nextQuestion());
  }

  @Test
  public void nextQuestion_previousAnsweredCorrectly_asksDifferentQuestion() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | 0", "1 | 1");
    for (int i = 0; i < 200; i++) {
      VocabularyChecker checker = prepareVocabularyChecker(lines, 3);
      String question = checker.nextQuestion();
      String correctAnswer = question; // answer in this test is always the same as question
      assertEquals(AnswerStatus.CORRECT, checker.checkAnswer(correctAnswer));
      assertNotEquals(checker.nextQuestion(), question);
    }
  }

  @Test
  public void nextQuestion_asksDifferentQuestion_whenRequestedAnswerForPreviousQuestion() {
    ImmutableList<String> lines = ImmutableList.of("question0 | answer0", "question1 | answer1");
    VocabularyChecker checker = prepareVocabularyChecker(lines, /* penaltyFactor= */ 1);
    for (int i = 0; i < 200; i++) {
      String question = checker.nextQuestion();
      checker.revealAnswer();
      assertNotEquals(checker.nextQuestion(), question);
    }
  }

  @Test
  public void checkAnswer_rightAnswer_returnsCorrect() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.nextQuestion(), "question");
    assertEquals(checker.checkAnswer("answer"), AnswerStatus.CORRECT);
  }

  @Test
  public void checkAnswer_wrongAnswer_returnsWrong() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.nextQuestion(), "question");
    assertEquals(checker.checkAnswer("wrong_answer"), AnswerStatus.WRONG);
  }

  @Test
  public void checkAnswer_pops1Adds1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
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
    ImmutableList<String> lines = ImmutableList.of("question | correct_answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("correct_answer");
    assertEquals(checker.questionsRemaining(), 0);
  }

  @Test
  public void checkAnswer_adds1_pops1_pops1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer1");
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
    ImmutableList<String> lines = ImmutableList.of("0 | answer0", "1 | answer1");
    boolean[] asked = {false, false};
    int counter = 150;
    while (!asked[0] || !asked[1]) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(lines);
      int questionIndex = Integer.parseInt(checker.nextQuestion());
      asked[questionIndex] = true;
      assertNotEquals(counter--, 0);
    }
  }

  @Test
  public void checkAnswer_popsCorrectQuestion() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | answer0", "1 | answer1");
    int counter = 150;
    while (counter > 0) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(lines);
      boolean[] asked = {false, false};
      int question_index = Integer.parseInt(checker.nextQuestion());
      checker.checkAnswer(String.format("answer%d", question_index));
      assertEquals(checker.questionsRemaining(), 1);
      counter--;
    }
  }

  @Test
  public void checkAnswer_worksInAlwaysRightMode() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("answer | question");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1, Column.RIGHT);
    assertEquals(checker.nextQuestion(), "question");
    checker.checkAnswer("question");
    assertEquals(checker.questionsRemaining(), 2);
    checker.checkAnswer("answer");
    assertEquals(checker.questionsRemaining(), 1);
  }

  @Test
  public void nextQuestion_sameQuestionAfterWrongAnswer() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | answer0", "1 | answer1");
    for (int i = 0; i < 150; i++) {
      VocabularyChecker checker = new VocabularyChecker(1, Column.LEFT);
      checker.prepareQuestions(lines);
      int question_index = Integer.parseInt(checker.nextQuestion());
      checker.checkAnswer("wrong_answer");
      assertEquals(checker.questionsRemaining(), 3);
      assertEquals(Integer.parseInt(checker.nextQuestion()), question_index);
      assertEquals(Integer.parseInt(checker.nextQuestion()), question_index);
    }
  }

  @Test
  public void revealAnswer_returnsAnswer() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.revealAnswer(), "answer");
  }

  @Test
  public void revealAnswer_returnsAnswer2() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer2");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.revealAnswer(), "answer2");
  }

  @Test
  public void revealAnswer_addsPenalty1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1);
    assertEquals(checker.questionsRemaining(), 1);
    checker.revealAnswer();
    assertEquals(checker.questionsRemaining(), 2);
  }

  @Test
  public void revealAnswer_addsPenalty2() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 2);
    assertEquals(checker.questionsRemaining(), 2);
    checker.revealAnswer();
    assertEquals(checker.questionsRemaining(), 4);
  }

  @Test
  public void revealAnswer_movesToNextQuestion() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | answer0", "1 | answer1");
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
  public void vocabularyChecker_askQuestionsFromAnotherColumn() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("left_column | right_column");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1, Column.RIGHT);
    assertEquals(checker.nextQuestion(), "right_column");
    assertEquals(checker.revealAnswer(), "left_column");
  }

  @Test
  public void vocabularyChecker_askQuestionsFromBothColumns() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | 1");
    VocabularyChecker checker = prepareVocabularyChecker(lines, 1, Column.BOTH);
    assertEquals(checker.questionsRemaining(), 2);
    int question = Integer.parseInt(checker.nextQuestion());
    String answer = new Integer((question + 1) % 2).toString();
    assertEquals(checker.checkAnswer(answer), AnswerStatus.CORRECT);
    assertEquals(checker.nextQuestion(), answer);
  }

  private void assertQuestionAnswerEquals(QuestionAnswer qa, String question, String answer) {
    assertEquals(qa.question, question);
    assertEquals(qa.answer, answer);
  }

  private VocabularyChecker prepareVocabularyChecker(
      ImmutableList<String> lines, int penaltyFactor) {
    return prepareVocabularyChecker(lines, penaltyFactor, Column.LEFT);
  }

  private VocabularyChecker prepareVocabularyChecker(
      ImmutableList<String> lines, int penaltyFactor, Column column) {
    VocabularyChecker checker = new VocabularyChecker(penaltyFactor, column);
    checker.prepareQuestions(lines);
    return checker;
  }
}
