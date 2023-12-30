package com.mercury0114.vocabulary;

import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.Column;

public class VocabularyCheckerModel extends ViewModel {
  private VocabularyChecker vocabularyChecker = null;

  public VocabularyChecker createOrGetChecker(Column column, ImmutableList<String> texts) {
    if (vocabularyChecker != null) {
      return vocabularyChecker;
    }
    vocabularyChecker = createChecker(column, texts);
    return vocabularyChecker;
  }

  private VocabularyChecker createChecker(Column column, ImmutableList<String> texts) {
    VocabularyChecker checker = new VocabularyChecker(column);
    checker.prepareQuestions(texts);
    return checker;
  }
}
