package com.mercury0114.vocabulary;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VocabularyChecker {
  private final int penaltyFactor;

  public static class QuestionAnswer {
    public final String question;
    public final String answer;

    QuestionAnswer(String question, String answer) {
      this.question = question;
      this.answer = answer;
    }
  }

  public VocabularyChecker(int penaltyFactor) {
    this.penaltyFactor = penaltyFactor;
  }

  public ArrayList<QuestionAnswer> prepareInitialQuestions(File file)
      throws IOException {
    List<String> lines =
        Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
    ArrayList<QuestionAnswer> question_answer_list = new ArrayList();
    for (String line : lines) {
      String[] words = line.split(", ");
      QuestionAnswer question_answer = new QuestionAnswer(words[0], words[1]);
      for (int i = 0; i < penaltyFactor; i++) {
        question_answer_list.add(question_answer);
      }
    }
    return question_answer_list;
  }
}
