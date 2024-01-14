package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.onlyElement;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;

public class Statistics {

  private final ImmutableList<StatisticsEntry> statisticsEntries;

  Statistics(ImmutableList<StatisticsEntry> statisticsEntries) {
    this.statisticsEntries = statisticsEntries;
  }

  ImmutableList<String> getHardestQuestions(int requestedNumber) {
    assert (statisticsEntries.size() >= requestedNumber) : "Requested more questions than we have";
    ImmutableList<String> questions =
        statisticsEntries.stream().map(entry -> entry.question()).collect(toImmutableList());
    return questions.subList(0, requestedNumber);
  }

  void updateOneStatisticsEntry(String question, AnswerStatus answerStatus) {
    StatisticsEntry entryMatchingQuestion =
        this.statisticsEntries.stream()
            .filter(entry -> entry.question().equals(question))
            .collect(onlyElement());
    entryMatchingQuestion.incrementCounter(answerStatus);
  }
}
