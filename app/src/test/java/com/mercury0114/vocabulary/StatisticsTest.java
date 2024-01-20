package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.StatisticsEntry.createEmptyStatisticsEntry;
import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.util.NoSuchElementException;
import org.junit.Test;

public class StatisticsTest {

  @Test
  public void getHardestQuestions_requestedTooManyQuestions_throwsIllegalStateException() {
    Statistics statistics =
        new Statistics(ImmutableList.of(createEmptyStatisticsEntry("question1")));

    assertThrows(
        AssertionError.class, () -> statistics.getHardestQuestions(/* requestedNumber= */ 2));
  }

  @Test
  public void getHardestQuestions_requestedAllQuestions_returnsAllQuestions() {
    Statistics statistics =
        new Statistics(
            ImmutableList.of(
                createEmptyStatisticsEntry("question1"), createEmptyStatisticsEntry("question2")));

    assertEquals(
        statistics.getHardestQuestions(/* requestedNumber= */ 2),
        ImmutableList.of("question1", "question2"));
  }

  @Test
  public void getHardestQuestions_statisticsHasMoreQuestionsThanRequested_returnsRequestedNumber() {
    Statistics statistics =
        new Statistics(
            ImmutableList.of(
                createEmptyStatisticsEntry("question1"), createEmptyStatisticsEntry("question2")));

    assertEquals(statistics.getHardestQuestions(/* requestedNumber= */ 1).size(), 1);
  }

  @Test
  public void updateOneStatisticsEntry_requestingToUpdateNonExistingEntry_throwsException() {
    Statistics statistics =
        new Statistics(ImmutableList.of(createEmptyStatisticsEntry("question")));

    assertThrows(
        NoSuchElementException.class,
        () -> statistics.updateOneStatisticsEntry("non_existing_question", AnswerStatus.CORRECT));
  }

  @Test
  public void prepareUpdatedStatisticsFileLines_vocabAndStatsLinesMatch_returnsAllStatsEntries() {
    ImmutableList<String> vocabulary =
        ImmutableList.of("question1 | answer1", "question2 | answer2");
    ImmutableList<String> oldStatistics =
        ImmutableList.of(
            "question1 | correct=0, close=0, wrong=0", "question2 | correct=0, close=0, wrong=0");
    Statistics currentStatistics =
        new Statistics(
            ImmutableList.of(
                createStatisticsEntry("question1 | correct=10, close=11, wrong=12"),
                createStatisticsEntry("question2 | correct=20, close=21, wrong=22")));

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(Column.LEFT, vocabulary, oldStatistics);

    assertEquals(
        ImmutableList.of(
            "question1 | correct=10, close=11, wrong=12",
            "question2 | correct=20, close=21, wrong=22"),
        updatedStatistics);
  }

  @Test
  public void prepareUpdatedStatisticsFileLines_statsEntryNotInVocab_returnsOnlyVocabEntries() {
    ImmutableList<String> vocabulary = ImmutableList.of("question1 | answer1");
    ImmutableList<String> oldStatistics =
        ImmutableList.of(
            "question1 | correct=0, close=0, wrong=0", "question2 | correct=0, close=0, wrong=0");
    Statistics currentStatistics =
        new Statistics(
            ImmutableList.of(createStatisticsEntry("question1 | correct=10, close=11, wrong=12")));

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(Column.LEFT, vocabulary, oldStatistics);

    assertEquals(ImmutableList.of("question1 | correct=10, close=11, wrong=12"), updatedStatistics);
  }

  @Test
  public void prepareUpdatedStatisticsFileLines_noStatsOneVocabLine_returnsVocabLineAsEmptyStats() {
    ImmutableList<String> vocabulary = ImmutableList.of("question | answer");
    ImmutableList<String> oldStatistics = ImmutableList.of();
    Statistics currentStatistics = new Statistics(ImmutableList.of());

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(Column.LEFT, vocabulary, oldStatistics);

    assertEquals(ImmutableList.of("question | correct=0, close=0, wrong=0"), updatedStatistics);
  }

  @Test
  public void prepareUpdatedStatisticsFileLines_prepareForRightColumn_returnsVocabAnswerAsStats() {
    ImmutableList<String> vocabulary = ImmutableList.of("left | right");
    ImmutableList<String> oldStatistics = ImmutableList.of();
    Statistics currentStatistics = new Statistics(ImmutableList.of());

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(
            Column.RIGHT, vocabulary, oldStatistics);

    assertEquals(ImmutableList.of("right | correct=0, close=0, wrong=0"), updatedStatistics);
  }

  @Test
  public void prepareUpdatedStatisticsFileLines_onlyOldStatsHasVocabLine_returnsOldStats() {
    ImmutableList<String> vocabulary = ImmutableList.of("question | answer");
    ImmutableList<String> oldStatistics =
        ImmutableList.of("question | correct=1, close=2, wrong=3");
    Statistics currentStatistics = new Statistics(ImmutableList.of());

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(Column.LEFT, vocabulary, oldStatistics);

    assertEquals(ImmutableList.of("question | correct=1, close=2, wrong=3"), updatedStatistics);
  }

  @Test
  public void
      prepareUpdatedStatisticsFileLines_oldAndCurrentStatsHasVocabLine_returnsCurrentStat() {
    ImmutableList<String> vocabulary = ImmutableList.of("question | answer");
    ImmutableList<String> oldStatistics =
        ImmutableList.of("question | correct=1, close=2, wrong=3");
    Statistics currentStatistics =
        new Statistics(
            ImmutableList.of(createStatisticsEntry("question | correct=4, close=5, wrong=6")));

    ImmutableList<String> updatedStatistics =
        currentStatistics.prepareUpdatedStatisticsFileLines(Column.LEFT, vocabulary, oldStatistics);

    assertEquals(ImmutableList.of("question | correct=4, close=5, wrong=6"), updatedStatistics);
  }
}
