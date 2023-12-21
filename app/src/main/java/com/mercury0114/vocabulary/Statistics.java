package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Statistics {

  private final List<Entry> entries;

  static Statistics createStatistics(ImmutableList<QuestionAnswer> questions) {
    List<Entry> entries =
        questions.stream()
            .map(question -> new Entry(question))
            .collect(Collectors.toCollection(ArrayList::new));
    return new Statistics(entries);
  }

  ImmutableList<QuestionAnswer> getHardestQuestions(int requestedNumber) {
    assert (entries.size() >= requestedNumber) : "Requested more questions than we have";
    return entries.stream().map(entry -> entry.questionAnswer).collect(toImmutableList());
  }

  private Statistics(List<Entry> entries) {
    this.entries = entries;
  }

  private static class Entry {
    private final QuestionAnswer questionAnswer;
    private int correctCount = 0;
    private int closeCount = 0;
    private int wrongCount = 0;

    Entry(QuestionAnswer questionAnswer) {
      this.questionAnswer = questionAnswer;
    }

    int getCorrectCount() {
      return correctCount;
    }

    int getCloseCount() {
      return closeCount;
    }

    int getWrongCount() {
      return wrongCount;
    }
  }
}
