package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.VocabularyChecker.NoQuestionsException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class VocabularyCheckerTest {

  @Test
  public void prepareQuestions_twoLines_creates4QuestionsAnswers() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer1", "question2 | answer2");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.questionsRemaining(), 4);
  }

  @Test
  public void currentQuestion_asksQuestionFooInList() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question_foo | answer_foo");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    String question = checker.currentQuestion();
    assertEquals(question, "question_foo");
  }

  @Test
  public void currentQuestion_asksQuestionBarInList() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question_bar | answer_bar");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    String question = checker.currentQuestion();
    assertEquals(question, "question_bar");
  }

  @Test
  public void currentQuestion_methodIsIdempotent() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question0 | answer0", "question1 | answer1");
    VocabularyChecker checker = prepareVocabularyChecker(lines);

    assertEquals(checker.currentQuestion(), checker.currentQuestion());
  }

  @Test
  public void currentQuestion_noQuestions_throwsException() throws NoQuestionsException {
    VocabularyChecker checker = new VocabularyChecker(Column.LEFT);
    assertThrows(NoQuestionsException.class, () -> checker.currentQuestion());
  }

  @Test
  public void currentQuestion_previousAnsweredCorrectly_asksDifferentQuestion() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | 0", "1 | 1");
    for (int i = 0; i < 200; i++) {
      VocabularyChecker checker = prepareVocabularyChecker(lines);
      String question = checker.currentQuestion();
      String correctAnswer = question; // answer in this test is always the same as question
      assertEquals(AnswerStatus.CORRECT, checker.checkAnswer(correctAnswer));
      assertNotEquals(checker.currentQuestion(), question);
    }
  }

  @Test
  public void currentQuestion_asksDifferentQuestion_whenRequestedAnswerForPreviousQuestion() {
    ImmutableList<String> lines = ImmutableList.of("question0 | answer0", "question1 | answer1");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    for (int i = 0; i < 200; i++) {
      String question = checker.currentQuestion();
      checker.revealAnswer();
      assertNotEquals(checker.currentQuestion(), question);
    }
  }

  @Test
  public void currentQuestion_choosesRandomlyFirstQuestion() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("0 | answer0", "1 | answer1");
    boolean[] asked = {false, false};
    int counter = 150;
    while (!asked[0] || !asked[1]) {
      VocabularyChecker checker = new VocabularyChecker(Column.LEFT);
      checker.prepareQuestions(lines);
      int questionIndex = Integer.parseInt(checker.currentQuestion());
      asked[questionIndex] = true;
      assertNotEquals(counter--, 0);
    }
  }

  @Test
  public void currentQuestion_sameQuestionAfterWrongAnswer() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question0 | answer0", "question1 | answer1");
    for (int i = 0; i < 150; i++) {
      VocabularyChecker checker = new VocabularyChecker(Column.LEFT);
      checker.prepareQuestions(lines);
      String question = checker.currentQuestion();
      checker.checkAnswer("wrong_answer");
      assertEquals(checker.currentQuestion(), question);
    }
  }

  @Test
  public void checkAnswer_rightAnswer_returnsCorrect() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.currentQuestion(), "question");
    assertEquals(checker.checkAnswer("answer"), AnswerStatus.CORRECT);
  }

  @Test
  public void checkAnswer_wrongAnswer_returnsWrong() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.currentQuestion(), "question");
    assertEquals(checker.checkAnswer("wrong_answer"), AnswerStatus.WRONG);
  }

  @Test
  public void checkAnswer_pops1Adds1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.currentQuestion(), "question");
    checker.checkAnswer("answer");
    assertEquals(checker.questionsRemaining(), 1);
    assertEquals(checker.currentQuestion(), "question");
    checker.checkAnswer("wrong_answer");
    assertEquals(checker.questionsRemaining(), 2);
  }

  @Test
  public void checkAnswer_pops1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | correct_answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.questionsRemaining(), 2);
    assertEquals(checker.currentQuestion(), "question");
    checker.checkAnswer("correct_answer");
    assertEquals(checker.questionsRemaining(), 1);
  }

  @Test
  public void checkAnswer_adds1_pops1_pops1() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer1");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    checker.currentQuestion();
    checker.checkAnswer("wrong_answer");
    assertEquals(checker.questionsRemaining(), 3);
    checker.checkAnswer("answer1");
    assertEquals(checker.questionsRemaining(), 2);
    checker.currentQuestion();
    checker.checkAnswer("answer1");
    assertEquals(checker.questionsRemaining(), 1);
  }

  @Test
  public void checkAnswer_worksInAlwaysRightMode() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("answer | question");
    VocabularyChecker checker = prepareVocabularyChecker(lines, Column.RIGHT);
    assertEquals(checker.currentQuestion(), "question");
    checker.checkAnswer("question");
    assertEquals(checker.questionsRemaining(), 3);
    checker.checkAnswer("answer");
    assertEquals(checker.questionsRemaining(), 2);
  }

  @Test
  public void revealAnswer_returnsAnswer() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.revealAnswer(), "answer");
  }

  @Test
  public void revealAnswer_returnsAnswer2() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer2");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.revealAnswer(), "answer2");
  }

  @Test
  public void revealAnswer_addsPenalty2() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question | answer");
    VocabularyChecker checker = prepareVocabularyChecker(lines);
    assertEquals(checker.questionsRemaining(), 2);
    checker.revealAnswer();
    assertEquals(checker.questionsRemaining(), 4);
  }

  @Test
  public void revealAnswer_givesDifferentQuestionNext() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("question1 | answer1", "question2 | answer2");
    for (int i = 0; i < 150; i++) {
      VocabularyChecker checker = prepareVocabularyChecker(lines);
      String currentQuestion = checker.currentQuestion();
      checker.revealAnswer();
      assertNotEquals(checker.currentQuestion(), currentQuestion);
    }
  }

  @Test
  public void vocabularyChecker_askQuestionsFromAnotherColumn() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("left_column | right_column");
    VocabularyChecker checker = prepareVocabularyChecker(lines, Column.RIGHT);
    assertEquals(checker.currentQuestion(), "right_column");
    assertEquals(checker.revealAnswer(), "left_column");
  }

  @Test
  public void vocabularyChecker_askQuestionsFromBothColumns() throws IOException {
    ImmutableList<String> lines = ImmutableList.of("left | right");
    VocabularyChecker checker = prepareVocabularyChecker(lines, Column.BOTH);
    Set<String> questionsAsked = new HashSet();
    while (checker.questionsRemaining() > 0) {
      String question = checker.currentQuestion();
      questionsAsked.add(question);
      checker.checkAnswer(question.equals("left") ? "right" : "left");
    }
    assertTrue(questionsAsked.contains("left"));
    assertTrue(questionsAsked.contains("right"));
  }

  private void assertQuestionAnswerEquals(QuestionAnswer qa, String question, String answer) {
    assertEquals(qa.question, question);
    assertEquals(qa.answer, answer);
  }

  private VocabularyChecker prepareVocabularyChecker(ImmutableList<String> lines) {
    return prepareVocabularyChecker(lines, Column.LEFT);
  }

  private VocabularyChecker prepareVocabularyChecker(ImmutableList<String> lines, Column column) {
    VocabularyChecker checker = new VocabularyChecker(column);
    checker.prepareQuestions(lines);
    return checker;
  }
}
