package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.LinesCreator.createLinesFromPhrases;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

public class LinesCreatorTest {

  @Test
  public void createLinesFromPhrases_unequalNumberOfLeftAndRightPhrases_throwsException() {
    ImmutableList<String> leftPhrases = ImmutableList.of("left_phrase1");
    ImmutableList<String> rightPhrases = ImmutableList.of("right_phrase1", "right_phrase2");

    assertThrows(
        IllegalArgumentException.class, () -> createLinesFromPhrases(leftPhrases, rightPhrases));
  }

  @Test
  public void createLinesFromPhrases_leftPhraseExistsButRightEmpty_throwsException() {
    ImmutableList<String> leftPhrases = ImmutableList.of("left_phrase");
    ImmutableList<String> rightPhrases = ImmutableList.of("");

    assertThrows(
        IllegalStateException.class, () -> createLinesFromPhrases(leftPhrases, rightPhrases));
  }

  @Test
  public void createLinesFromPhrases_leftPhraseEmptyButRightExists_throwsException() {
    ImmutableList<String> leftPhrases = ImmutableList.of("");
    ImmutableList<String> rightPhrases = ImmutableList.of("right_phrase");

    assertThrows(
        IllegalStateException.class, () -> createLinesFromPhrases(leftPhrases, rightPhrases));
  }

  @Test
  public void createLinesFromPhrases_noLinesCreated_throwsException() {
    ImmutableList<String> leftPhrases = ImmutableList.of("");
    ImmutableList<String> rightPhrases = ImmutableList.of("");

    assertThrows(
        IllegalStateException.class, () -> createLinesFromPhrases(leftPhrases, rightPhrases));
  }

  @Test
  public void createLinesFromPhrases_skipsEmptyLine() {
    ImmutableList<String> leftPhrases = ImmutableList.of("", "left_phrase");
    ImmutableList<String> rightPhrases = ImmutableList.of("", "right_phrase");

    assertEquals(
        ImmutableList.of("left_phrase | right_phrase"),
        createLinesFromPhrases(leftPhrases, rightPhrases));
  }

  @Test
  public void createLinesFromPhrases_forTwoPhrasesPairs_createsTwoLines() {
    ImmutableList<String> leftPhrases = ImmutableList.of("left_phrase1", "left_phrase2");
    ImmutableList<String> rightPhrases = ImmutableList.of("right_phrase1", "right_phrase2");

    assertEquals(
        ImmutableList.of("left_phrase1 | right_phrase1", "left_phrase2 | right_phrase2"),
        createLinesFromPhrases(leftPhrases, rightPhrases));
  }
}
