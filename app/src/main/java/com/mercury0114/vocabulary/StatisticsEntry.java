package com.mercury0114.vocabulary;

import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StatisticsEntry {
  private final String question;
  private int correctCount = 0;
  private int closeCount = 0;
  private int wrongCount = 0;

  StatisticsEntry(String question, int correctCount, int closeCount, int wrongCount) {
    this.question = question;
    this.correctCount = correctCount;
    this.closeCount = closeCount;
    this.wrongCount = wrongCount;
  }

  static StatisticsEntry createStatisticsEntry(String fileLine) {
    Pattern pattern = Pattern.compile("(.+) \\| correct=(\\d+), close=(\\d+), wrong=(\\d+)");
    Matcher matcher = pattern.matcher(fileLine);
    assert (matcher.find());
    String question = matcher.group(1);
    int correctCount = Integer.parseInt(matcher.group(2));
    int closeCount = Integer.parseInt(matcher.group(3));
    int wrongCount = Integer.parseInt(matcher.group(4));
    return new StatisticsEntry(question, correctCount, closeCount, wrongCount);
  }

  String convertToFileLine() {
    return String.format(
        "%s | correct=%d, close=%d, wrong=%d", question, correctCount, closeCount, wrongCount);
  }

  String question() {
    return question;
  }

  int correctCount() {
    return correctCount;
  }

  int closeCount() {
    return closeCount;
  }

  int wrongCount() {
    return wrongCount;
  }

  void incrementCounter(AnswerStatus answerStatus) {
    switch (answerStatus) {
      case CORRECT:
        correctCount++;
        return;
      case CLOSE:
        closeCount++;
        return;
      case WRONG:
        wrongCount++;
        return;
    }
  }
}
