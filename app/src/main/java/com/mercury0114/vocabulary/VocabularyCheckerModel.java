package com.mercury0114.vocabulary;

import androidx.lifecycle.ViewModel;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;
import java.io.IOException;

public class VocabularyCheckerModel extends ViewModel {
  private VocabularyChecker vocabularyChecker = null;

  public VocabularyChecker createOrGetChecker(int penaltyFactor, Column column, String filePath) {
    if (vocabularyChecker != null) {
      return vocabularyChecker;
    }
    vocabularyChecker = createChecker(penaltyFactor, column, filePath);
    return vocabularyChecker;
  }

  private VocabularyChecker createChecker(int penaltyFactor, Column column, String filePath) {
    VocabularyChecker checker = new VocabularyChecker(penaltyFactor, column);
    try {
      checker.prepareQuestions(new File(filePath));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    return checker;
  }
}
