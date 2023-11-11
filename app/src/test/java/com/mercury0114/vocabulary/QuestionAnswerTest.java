package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.QuestionAnswer.WronglyFormattedLineException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
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
  public void constructor_severalCommas_throwsException() {
    assertThrows(
        WronglyFormattedLineException.class, () -> new QuestionAnswer("question, answer, more"));
  }

  @Test
  public void constructor_commaInQuestion_constructsRightQuestionAnswer() {
    QuestionAnswer qa = new QuestionAnswer("hey, Jack | hello");
    assertEquals("hey, Jack", qa.question);
    assertEquals("hello", qa.answer);
  }

  @Test
  public void constructor_correctLine_constructsRightQuestionAnswer() {
    QuestionAnswer qa = new QuestionAnswer("question | answer");
    assertEquals(qa.question, "question");
    assertEquals(qa.answer, "answer");
  }

  @Test
  public void constructor_phrase_constructsPhrase() {
    QuestionAnswer qa = new QuestionAnswer("multiple words question | multiple words answer");
    assertEquals(qa.question, "multiple words question");
    assertEquals(qa.answer, "multiple words answer");
  }

  @Test
  public void same_returnsTrueOnSameQuestionAnswer() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertTrue(qa.same(new QuestionAnswer("question", "answer")));
  }

  @Test
  public void same_returnsFalseOnDifferentQuestion() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertFalse(qa.same(new QuestionAnswer("question2", "answer")));
  }

  @Test
  public void same_returnsFalseOnDifferentAnswer() {
    QuestionAnswer qa = new QuestionAnswer("question", "answer");
    assertFalse(qa.same(new QuestionAnswer("question", "answer2")));
  }
}
