package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.Statistics.createStatistics;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import org.junit.Test;

public class StatisticsTest {

  @Test
  public void getHardestQuestions_requestedTooManyQuestions_throwsIllegalStateException() {
    Statistics statistics = createStatistics(ImmutableList.of("question | answer"), Column.LEFT);

    assertThrows(
        AssertionError.class, () -> statistics.getHardestQuestions(/* requestedNumber= */ 2));
  }

  @Test
  public void getHardestQuestions_requestedAllQuestions_returnsAllQuestions() {
    QuestionAnswer questionAnswer1 = new QuestionAnswer("question1", "answer1");
    QuestionAnswer questionAnswer2 = new QuestionAnswer("question2", "answer2");
    Statistics statistics =
        createStatistics(
            ImmutableList.of("question1 | answer1", "question2 | answer2"), Column.LEFT);

    assertEquals(
        statistics.getHardestQuestions(/* requestedNumber= */ 2),
        ImmutableList.of(questionAnswer1, questionAnswer2));
  }

  @Test
  public void getHardestQuestions_moreQuestionsThanRequested_returnsRequestedNumber() {
    QuestionAnswer questionAnswer1 = new QuestionAnswer("question1", "answer1");
    QuestionAnswer questionAnswer2 = new QuestionAnswer("question2", "answer2");
    Statistics statistics =
        createStatistics(
            ImmutableList.of("question1 | answer1", "question2 | answer2"), Column.LEFT);

    assertEquals(statistics.getHardestQuestions(/* requestedNumber= */ 1).size(), 1);
  }
}
