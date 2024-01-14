package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import java.util.NoSuchElementException;
import org.junit.Test;

public class StatisticsTest {

  @Test
  public void getHardestQuestions_requestedTooManyQuestions_throwsIllegalStateException() {
    Statistics statistics = new Statistics(ImmutableList.of(createEntry("question1")));

    assertThrows(
        AssertionError.class, () -> statistics.getHardestQuestions(/* requestedNumber= */ 2));
  }

  @Test
  public void getHardestQuestions_requestedAllQuestions_returnsAllQuestions() {
    Statistics statistics =
        new Statistics(ImmutableList.of(createEntry("question1"), createEntry("question2")));

    assertEquals(
        statistics.getHardestQuestions(/* requestedNumber= */ 2),
        ImmutableList.of("question1", "question2"));
  }

  @Test
  public void getHardestQuestions_statisticsHasMoreQuestionsThanRequested_returnsRequestedNumber() {
    Statistics statistics =
        new Statistics(ImmutableList.of(createEntry("question1"), createEntry("question2")));

    assertEquals(statistics.getHardestQuestions(/* requestedNumber= */ 1).size(), 1);
  }

  @Test
  public void updateOneStatisticsEntry_requestingToUpdateNonExistingEntry_throwsException() {
    Statistics statistics = new Statistics(ImmutableList.of(createEntry("question")));

    assertThrows(
        NoSuchElementException.class,
        () -> statistics.updateOneStatisticsEntry("non_existing_question", AnswerStatus.CORRECT));
  }

  private StatisticsEntry createEntry(String question) {
    return createStatisticsEntry(String.format("%s | correct=0 | close=1 | wrong=2", question));
  }
}
