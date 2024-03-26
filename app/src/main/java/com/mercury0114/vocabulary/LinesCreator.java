package com.mercury0114.vocabulary;


import com.google.common.collect.ImmutableList;

class LinesCreator {

  static ImmutableList<String> createLinesFromPhrases(
      ImmutableList<String> leftPhrases, ImmutableList<String> rightPhrases) {
    if (leftPhrases.size() != rightPhrases.size()) {
        throw new IllegalArgumentException("Both lists should contain same number of phrases");
    }
      
    ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
    for (int i = 0; i < leftPhrases.size(); i++) {
      String leftPhrase = leftPhrases.get(i);
      String rightPhrase = rightPhrases.get(i);
      if (leftPhrase.isEmpty() && rightPhrase.isEmpty()) {
        continue;
      }
      if (leftPhrase.isEmpty() || rightPhrase.isEmpty()) {
        throw new IllegalStateException("Both phrases should be non-empty");
      }
      linesBuilder.add(leftPhrase + " | " + rightPhrase);
    }
    ImmutableList<String> lines = linesBuilder.build();
    if (lines.size() == 0) {
        throw new IllegalStateException("Expecting at least one phrase");
    }
    return lines;
  }
}
