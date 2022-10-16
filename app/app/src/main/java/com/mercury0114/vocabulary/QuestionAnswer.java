package com.mercury0114.vocabulary;

import org.apache.commons.lang3.StringUtils;

public class QuestionAnswer {
  enum Column {
    LEFT,
    RIGHT,
  }

  enum AnswerStatus { CORRECT, CLOSE, WRONG }

  final String question;
  final String answer;

  public QuestionAnswer(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }

  public AnswerStatus getAnswerStatus(String answer) {
    if (answer.equals(this.answer)) {
      return AnswerStatus.CORRECT;
    }
    if (StringUtils.getLevenshteinDistance(answer, this.answer) <= 2) {
      return AnswerStatus.CLOSE;
    }
    return AnswerStatus.WRONG;
  }
}
