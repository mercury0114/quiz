package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;

public class Statistics {

  private final ImmutableList<Entry> entries;

  static Statistics createStatistics(ImmutableList<QuestionAnswer> questions) {
    ImmutableList<Entry> entries =
        questions.stream().map(question -> new Entry(question)).collect(toImmutableList());
    return new Statistics(entries);
  }

  ImmutableList<QuestionAnswer> getHardestQuestions(int requestedNumber) {
    assert (entries.size() >= requestedNumber) : "Requested more questions than we have";
    ImmutableList<QuestionAnswer> questions =
        entries.stream().map(entry -> entry.questionAnswer).collect(toImmutableList());
    return questions.subList(0, requestedNumber);
  }

  private Statistics(ImmutableList<Entry> entries) {
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
