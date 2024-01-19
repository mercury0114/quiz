package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import org.junit.Test;

public class StatisticsEntryTest {

  @Test
  public void createStatisticsEntry_setsCountersToAppropriateValues() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=1, close=2, wrong=3");

    assertEquals(entry.correctCount(), 1);
    assertEquals(entry.closeCount(), 2);
    assertEquals(entry.wrongCount(), 3);
  }

  @Test
  public void createStatisticsEntry_setsQuestionToAppropriateValue() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=1, close=2, wrong=3");

    assertEquals(entry.question(), "question");
  }

  @Test
  public void createStatisticsEntry_lineMissesWrongCount_throwsException() {
    assertThrows(
        AssertionError.class, () -> createStatisticsEntry("question | correct=1, close=2"));
  }

  @Test
  public void createStatisticsEntry_lineHasCountersInWrongOrder_throwsException() {
    assertThrows(
        AssertionError.class,
        () -> createStatisticsEntry("question | correct=1, wrong=2, close=3"));
  }

  @Test
  public void convertToFileLine_putsPreciseCounters() {
    StatisticsEntry entry =
        new StatisticsEntry(
            "question", /* correctCount= */ 1, /* closeCount= */ 2, /* wrongCount= */ 3);
    assertEquals("question | correct=1, close=2, wrong=3", entry.convertToFileLine());
  }

  @Test
  public void convertToFileLine_putsPreciseQuestion() {
    StatisticsEntry entry =
        new StatisticsEntry(
            "different_question", /* correctCount= */ 0, /* closeCount= */ 0, /* wrongCount= */ 0);

    assertEquals("different_question | correct=0, close=0, wrong=0", entry.convertToFileLine());
  }

  @Test
  public void incrementCounter_answerStatusEnumEqualsCorrect_updatesCorrectCounter() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=10, close=20, wrong=30");

    entry.incrementCounter(AnswerStatus.CORRECT);

    assertEquals(entry.correctCount(), 11);
  }

  @Test
  public void incrementCounter_answerStatusEnumEqualsClose_updatesCloseCounter() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=10, close=20, wrong=30");

    entry.incrementCounter(AnswerStatus.CLOSE);

    assertEquals(entry.closeCount(), 21);
  }

  @Test
  public void incrementCounter_answerStatusEnumEqualsWrong_updatesWrongCounter() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=10, close=20, wrong=30");

    entry.incrementCounter(AnswerStatus.WRONG);

    assertEquals(entry.wrongCount(), 31);
  }
}
