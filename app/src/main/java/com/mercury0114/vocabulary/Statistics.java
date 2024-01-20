package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static com.mercury0114.vocabulary.QuestionAnswer.extractQuestionAnswer;
import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;
import static com.mercury0114.vocabulary.StatisticsEntry.findEntryOrEmptyEntry;

import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.util.Optional;

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
    StatisticsEntry entryMatchingQuestion = findEntry(question).get();
    entryMatchingQuestion.incrementCounter(answerStatus);
  }

  ImmutableList<String> prepareUpdatedStatisticsFileLines(
      Column column,
      ImmutableList<String> currentVocabularyFileLines,
      ImmutableList<String> oldStatisticsFileLines) {
    ImmutableList<String> questions =
        currentVocabularyFileLines.stream()
            .map(line -> extractQuestionAnswer(line, column))
            .map(questionAnswer -> questionAnswer.question)
            .collect(toImmutableList());
    ImmutableList<StatisticsEntry> oldStatisticsEntries =
        oldStatisticsFileLines.stream()
            .map(line -> createStatisticsEntry(line))
            .collect(toImmutableList());
    ImmutableList<StatisticsEntry> upToDateEntries =
        questions.stream()
            .map(question -> returnMostUpToDateStatisticsEntry(question, oldStatisticsEntries))
            .collect(toImmutableList());
    return upToDateEntries.stream()
        .map(entry -> entry.convertToFileLine())
        .collect(toImmutableList());
  }

  private StatisticsEntry returnMostUpToDateStatisticsEntry(
      String question, ImmutableList<StatisticsEntry> oldStatisticsEntries) {
    return findEntry(question).orElse(findEntryOrEmptyEntry(question, oldStatisticsEntries));
  }

  private Optional<StatisticsEntry> findEntry(String question) {
    return this.statisticsEntries.stream()
        .filter(entry -> entry.question().equals(question))
        .collect(toOptional());
  }
}
