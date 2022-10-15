package com.mercury0114.vocabulary;

import static java.lang.Math.abs;

import com.mercury0114.vocabulary.QuestionAnswer;
import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VocabularyChecker {
  public static class EmptyFileException extends RuntimeException {
    private EmptyFileException(String filePath) { super(filePath); }
  }
  public static class NoQuestionsException extends RuntimeException {}

  private final int penaltyFactor;
  private final ArrayList<QuestionAnswer> question_answer_list =
      new ArrayList();
  private int nextQuestionIndex;

  public VocabularyChecker(int penaltyFactor) {
    if (penaltyFactor <= 0) {
      throw new IllegalArgumentException();
    }
    this.penaltyFactor = penaltyFactor;
    this.nextQuestionIndex = abs((int)System.currentTimeMillis());
  }

  public void prepareQuestions(File file) throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
    if (lines.size() == 0) {
      throw new EmptyFileException(file.getPath());
    }
    for (String line : lines) {
      String[] words = line.split(", ");
      QuestionAnswer question_answer = new QuestionAnswer(words[0], words[1]);
      for (int i = 0; i < penaltyFactor; i++) {
        question_answer_list.add(question_answer);
      }
    }
    nextQuestionIndex %= question_answer_list.size();
  }

  public int questionsRemaining() { return question_answer_list.size(); }

  public String nextQuestion() throws NoQuestionsException {
    if (question_answer_list.size() == 0) {
      throw new NoQuestionsException();
    }
    nextQuestionIndex =
        (nextQuestionIndex * 7919 + 101) % (question_answer_list.size());
    return question_answer_list.get(nextQuestionIndex).question;
  }

  public void updateList(String answer) {
    QuestionAnswer questionAnswer = question_answer_list.get(nextQuestionIndex);
    if (answer.equals(questionAnswer.answer)) {
      question_answer_list.remove(nextQuestionIndex);
    } else {
      for (int i = 0; i < penaltyFactor; i++) {
        question_answer_list.add(questionAnswer);
      }
    }
  }
}
