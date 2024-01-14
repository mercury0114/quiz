package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class StatisticsEntryTest {

  @Test
  public void createStatisticsEntry_setsCountersToAppropriateValues() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=1 | close=2 | wrong=3");

    assertEquals(entry.correctCount(), 1);
    assertEquals(entry.closeCount(), 2);
    assertEquals(entry.wrongCount(), 3);
  }

  @Test
  public void createStatisticsEntry_setsQuestionToAppropriateValue() {
    StatisticsEntry entry = createStatisticsEntry("question | correct=1 | close=2 | wrong=3");

    assertEquals(entry.question(), "question");
  }

  @Test
  public void createStatisticsEntry_lineMissesWrongCount_throwsException() {
    assertThrows(
        AssertionError.class, () -> createStatisticsEntry("question | correct=1 | close=2"));
  }

  @Test
  public void createStatisticsEntry_lineHasCountersInWrongOrder_throwsException() {
    assertThrows(
        AssertionError.class,
        () -> createStatisticsEntry("question | correct=1 | wrong=2 | close=3"));
  }
}
