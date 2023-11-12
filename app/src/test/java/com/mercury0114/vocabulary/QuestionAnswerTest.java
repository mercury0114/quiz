package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.QuestionAnswer.WronglyFormattedLineException;
import static com.mercury0114.vocabulary.QuestionAnswer.extractQuestionAnswer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import org.junit.Test;

public class QuestionAnswerTest {
  @Test
  public void answerStatus_correctAnswer_returnsCorrectStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("answer"), AnswerStatus.CORRECT);
  }

  @Test
  public void answerStatus_wrongAnswer_returnsWrongStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("wrong_answer"), AnswerStatus.WRONG);
  }

  @Test
  public void answerStatus_wrongFirstLetter_returnsCloseStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("answer"), AnswerStatus.CORRECT);
    assertEquals(qa.getAnswerStatus("Answer"), AnswerStatus.CLOSE);
  }

  @Test
  public void answerStatus_tooManyLetters_returnsCloseStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("answe"), AnswerStatus.CLOSE);
  }

  @Test
  public void answerStatus_tooFewLetters_returnsCloseStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("answerX"), AnswerStatus.CLOSE);
  }

  @Test
  public void answerStatus_wrongLetterInserted_returnsCloseStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("ansXwer"), AnswerStatus.CLOSE);
  }

  @Test
  public void answerStatus_twoWrongLetters_returnsCloseStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("AnSwer"), AnswerStatus.CLOSE);
  }

  @Test
  public void answerStatus_threeWrongLetters_returnsWrongStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("ANSwer"), AnswerStatus.WRONG);
  }

  @Test
  public void answerStatus_correctAnswerIgnoringSpace_returnsCorrectStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus("answer "), AnswerStatus.CORRECT);
  }

  @Test
  public void answerStatus_emptyAnswer_returnsWrongStatus() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertEquals(qa.getAnswerStatus(""), AnswerStatus.WRONG);
  }

  @Test
  public void extractQuestionAnswer_lineWithEmptyAnswer_throwsException() {
    assertThrows(
        WronglyFormattedLineException.class,
        () -> extractQuestionAnswer("question | ", Column.LEFT));
  }

  @Test
  public void extractQuestionAnswer_lineWithEmptyQuestion_throwsException() {
    assertThrows(
        WronglyFormattedLineException.class, () -> extractQuestionAnswer(" | answer", Column.LEFT));
  }

  @Test
  public void extractQuestionAnswer_noDelimiter_throwsException() {
    assertThrows(
        WronglyFormattedLineException.class,
        () -> extractQuestionAnswer("question, answer, more", Column.LEFT));
  }

  @Test
  public void extractQuestionAnswer_commaInQuestion_constructsRightQuestionAnswer() {
    QuestionAnswer qa = extractQuestionAnswer("hey, Jack | hello", Column.LEFT);
    assertEquals("hey, Jack", qa.question);
    assertEquals("hello", qa.answer);
  }

  @Test
  public void extractQuestionAnswer_rightColumn_firstPhraseIsAnswerSecondPhraseIsQuestion() {
    QuestionAnswer qa = extractQuestionAnswer("first phrase | second phrase", Column.RIGHT);
    assertEquals(qa.answer, "first phrase");
    assertEquals(qa.question, "second phrase");
  }

  @Test
  public void extractQuestionAnswer_bothColumns_throwsInvalidArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> extractQuestionAnswer("question | answer", Column.BOTH));
  }

  @Test
  public void equals_returnsTrueOnSameQuestionAnswer() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertTrue(qa.equals(new QuestionAnswer("question", "answer")));
  }

  @Test
  public void equals_returnsFalseOnDifferentQuestion() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertFalse(qa.equals(new QuestionAnswer("question2", "answer")));
  }

  @Test
  public void equals_returnsFalseOnDifferentAnswer() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertFalse(qa.equals(new QuestionAnswer("question", "answer2")));
  }
}
