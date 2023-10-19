package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readFileContent;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VocabularyChecker {
  public static class EmptyFileException extends RuntimeException {
    private EmptyFileException(String filePath) {
      super(filePath);
    }
  }

  public static class NoQuestionsException extends RuntimeException {}

  private static final int FACTOR = 2011;
  private static final int MODULUS = 7919;

  private final ArrayList<QuestionAnswer> questionAnswerList = new ArrayList();
  private final int penaltyFactor;
  private int nextQuestionIndex;
  private int seed;
  private Column column;

  public VocabularyChecker(int penaltyFactor, Column column) {
    if (penaltyFactor <= 0) {
      throw new IllegalArgumentException();
    }
    this.penaltyFactor = penaltyFactor;
    this.seed = abs((int) System.currentTimeMillis()) % MODULUS;
    this.column = column;
  }

  public void prepareQuestions(File file) throws IOException {
    for (String line : readFileContent(file)) {
      String[] words = line.split(", ");
      QuestionAnswer left = new QuestionAnswer(words[0], words[1]);
      QuestionAnswer right = new QuestionAnswer(words[1], words[0]);
      for (int i = 0; i < penaltyFactor; i++) {
        switch (column) {
          case LEFT:
            questionAnswerList.add(left);
            break;
          case RIGHT:
            questionAnswerList.add(right);
            break;
          case BOTH:
            questionAnswerList.add(left);
            questionAnswerList.add(right);
            break;
          default:
            throw new RuntimeException("Wrong Column enum value");
        }
      }
    }
    nextQuestionIndex = seed % questionAnswerList.size();
  }

  public AnswerStatus checkAnswer(String answer) {
    QuestionAnswer questionAnswer = questionAnswerList.get(nextQuestionIndex);
    AnswerStatus answerStatus = questionAnswer.getAnswerStatus(answer);
    switch (answerStatus) {
      case CORRECT:
        questionAnswerList.remove(nextQuestionIndex);
        updateSeedAndQuestionIndex(questionAnswer);
        break;
      case CLOSE:
        break;
      case WRONG:
        updateQuestionAnswerList(1);
        break;
    }
    return answerStatus;
  }

  public int questionsRemaining() {
    return questionAnswerList.size();
  }

  public String nextQuestion() throws NoQuestionsException {
    if (questionAnswerList.size() == 0) {
      throw new NoQuestionsException();
    }
    return questionAnswerList.get(nextQuestionIndex).question;
  }

  public String revealAnswer() {
    QuestionAnswer qa = questionAnswerList.get(nextQuestionIndex);
    updateQuestionAnswerList(penaltyFactor);
    updateSeedAndQuestionIndex(qa);
    return qa.answer;
  }

  private void updateQuestionAnswerList(int penaltyFactor) {
    QuestionAnswer questionAnswer = questionAnswerList.get(nextQuestionIndex);
    for (int i = 0; i < penaltyFactor; i++) {
      questionAnswerList.add(questionAnswer);
    }
  }

  private void updateSeedAndQuestionIndex(QuestionAnswer previous) {
    seed = (seed * FACTOR + 101) % MODULUS;
    nextQuestionIndex = seed % max(1, questionAnswerList.size());
    for (int i = 0; i < questionAnswerList.size(); i++) {
      if (!questionAnswerList.get(nextQuestionIndex).same(previous)) {
        return;
      }
      nextQuestionIndex = (nextQuestionIndex + 1) % questionAnswerList.size();
    }
  }
}
