package com.mercury0114.vocabulary;

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
    String[] words = line.split(" \\| ");
    if (words.length != 2) {
      throw new WronglyFormattedLineException(line);
    }
    switch (column) {
      case LEFT:
        return new QuestionAnswer(words[0], words[1]);
      case RIGHT:
        return new QuestionAnswer(words[1], words[0]);
      case BOTH:
        throw new IllegalArgumentException("Only LEFT or RIGHT column supported, not BOTH");
    }
    throw new AssertionError("Impossible code path reached");
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

  public boolean equals(QuestionAnswer questionAnswer) {
    return this.question.equals(questionAnswer.question)
        && this.answer.equals(questionAnswer.answer);
  }
}
