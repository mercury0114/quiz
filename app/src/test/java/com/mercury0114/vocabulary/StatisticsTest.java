package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.Statistics.createStatistics;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class StatisticsTest {

  @Test
  public void getHardestQuestions_requestedTooManyQuestions_throwsIllegalStateException() {
    QuestionAnswer questionAnswer = new QuestionAnswer("question", "answer");
    Statistics statistics = createStatistics(ImmutableList.of(questionAnswer));

    assertThrows(
        AssertionError.class, () -> statistics.getHardestQuestions(/* requestedNumber= */ 2));
  }

  @Test
  public void getHardestQuestions_requestedAllQuestions_returnsAllQuestions() {
    QuestionAnswer questionAnswer1 = new QuestionAnswer("question1", "answer1");
    QuestionAnswer questionAnswer2 = new QuestionAnswer("question2", "answer2");
    Statistics statistics = createStatistics(ImmutableList.of(questionAnswer1, questionAnswer2));

    assertEquals(
        statistics.getHardestQuestions(/* requestedNumber= */ 2),
        ImmutableList.of(questionAnswer1, questionAnswer2));
  }

  @Test
  public void getHardestQuestions_moreQuestionsThanRequested_returnsRequestedNumber() {
    QuestionAnswer questionAnswer1 = new QuestionAnswer("question1", "answer1");
    QuestionAnswer questionAnswer2 = new QuestionAnswer("question2", "answer2");
    Statistics statistics = createStatistics(ImmutableList.of(questionAnswer1, questionAnswer2));

    assertEquals(statistics.getHardestQuestions(/* requestedNumber= */ 1).size(), 1);
  }
}
