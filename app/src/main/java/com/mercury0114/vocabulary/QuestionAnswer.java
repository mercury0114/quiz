package com.mercury0114.vocabulary;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

public class QuestionAnswer {
  public static class WronglyFormattedLineException extends RuntimeException {
    private WronglyFormattedLineException(String line) {
      super(line);
    }
  }

  enum Column {
    LEFT,
    RIGHT,
    BOTH,
  }

  enum AnswerStatus {
    CORRECT,
    CLOSE,
    WRONG
  }

  final String question;
  final String answer;

  public static QuestionAnswer extractQuestionAnswer(String line, Column column) {
    ImmutableList<String> twoStrings = splitIntoTwoStrings(line);
    switch (column) {
      case LEFT:
        return new QuestionAnswer(twoStrings.get(0), twoStrings.get(1));
      case RIGHT:
        return new QuestionAnswer(twoStrings.get(1), twoStrings.get(0));
      case BOTH:
        throw new IllegalArgumentException("Only LEFT or RIGHT column supported, not BOTH");
    }
    throw new AssertionError("Impossible code path reached");
  }

  // Throws an exception, if line is not correctly formatted.
  public static ImmutableList<String> splitIntoTwoStrings(String line) {
    if (!line.contains(" | ")) {
      throw new WronglyFormattedLineException(line);
    }
    String[] twoStrings = line.split(" \\| ");
    if (twoStrings.length != 2 || twoStrings[0].isEmpty() || twoStrings[1].isEmpty()) {
      throw new WronglyFormattedLineException(line);
    }
    return ImmutableList.copyOf(twoStrings);
  }

  public QuestionAnswer(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }

  public AnswerStatus getAnswerStatus(String answer) {
    if (answer.isEmpty()) {
      return AnswerStatus.WRONG;
    }
    int distance = StringUtils.getLevenshteinDistance(answer, this.answer);
    if (answer.charAt(answer.length() - 1) == ' ') {
      distance--;
    }
    if (distance == 0) {
      return AnswerStatus.CORRECT;
    }
    if (distance <= 2) {
      return AnswerStatus.CLOSE;
    }
    return AnswerStatus.WRONG;
  }

  @Override
  public boolean equals(Object other) {
    return this.question.equals(((QuestionAnswer) other).question)
        && this.answer.equals(((QuestionAnswer) other).answer);
  }

  @Override
  public int hashCode() {
    return (this.question + this.answer).hashCode();
  }
}
