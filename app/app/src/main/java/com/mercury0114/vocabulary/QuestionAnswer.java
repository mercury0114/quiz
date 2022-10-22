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
}
