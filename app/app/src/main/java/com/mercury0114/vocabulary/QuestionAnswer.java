package com.mercury0114.vocabulary;

public class QuestionAnswer {
  final String question;
  final String answer;

  public QuestionAnswer(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }

  public boolean correctAnswer(String answer) {
    return answer.equals(this.answer);
  }
}
