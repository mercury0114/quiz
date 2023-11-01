package com.mercury0114.vocabulary;

import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;

public class VocabularyCheckerModel extends ViewModel {
  private VocabularyChecker vocabularyChecker = null;

  public VocabularyChecker createOrGetChecker(int penaltyFactor, Column column, String filePath) {
    if (vocabularyChecker != null) {
      return vocabularyChecker;
    }
    vocabularyChecker = createChecker(penaltyFactor, column, filePath);
    return vocabularyChecker;
  }

  public VocabularyChecker createOrGet(
      int penaltyFactor, Column column, ImmutableList<String> texts) {
    if (vocabularyChecker != null) {
      return vocabularyChecker;
    }
    vocabularyChecker = createChecker(penaltyFactor, column, texts);
    return vocabularyChecker;
  }

  private VocabularyChecker createChecker(
      int penaltyFactor, Column column, ImmutableList<String> texts) {
    VocabularyChecker checker = new VocabularyChecker(penaltyFactor, column);
    checker.prepareQuestions(texts);
    return checker;
  }

  private VocabularyChecker createChecker(int penaltyFactor, Column column, String filePath) {
    VocabularyChecker checker = new VocabularyChecker(penaltyFactor, column);
    checker.prepareQuestions(new File(filePath));
    return checker;
  }
}
