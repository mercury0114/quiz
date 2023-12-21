package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;

import android.graphics.Color;
import android.widget.Button;
import com.google.common.collect.ImmutableList;

final class TextSelector {
  public static class NoChosenTextException extends RuntimeException {}

  static int CHOSEN_COLOR_CODE = Color.BLUE;
  static int DEFAULT_COLOR_CODE = Color.BLACK;

  static int getToggledColorCode(Button button) {
    checkButtonHasValidColor(button);
    return getColorCode(button) == DEFAULT_COLOR_CODE ? CHOSEN_COLOR_CODE : DEFAULT_COLOR_CODE;
  }

  static ImmutableList<String> extractChosenTexts(
      ImmutableList<Button> buttonsCorrespondingToTexts) {
    for (Button button : buttonsCorrespondingToTexts) {
      checkButtonHasValidColor(button);
    }
    ImmutableList<Button> chosenButtons = selectOnlyChosenButtons(buttonsCorrespondingToTexts);
    if (chosenButtons.isEmpty()) {
      throw new NoChosenTextException();
    }
    return extractAllTexts(chosenButtons);
  }

  private static void checkButtonHasValidColor(Button button) {
    int colorCode = getColorCode(button);
    if (!ImmutableList.of(CHOSEN_COLOR_CODE, DEFAULT_COLOR_CODE).contains(colorCode)) {
      throw new IllegalArgumentException(
          String.format("Button color %d not supported\n", colorCode));
    }
  }

  private static ImmutableList<Button> selectOnlyChosenButtons(ImmutableList<Button> buttons) {
    return buttons.stream()
        .filter(button -> getColorCode(button) == CHOSEN_COLOR_CODE)
        .collect(toImmutableList());
  }

  private static int getColorCode(Button button) {
    return button.getTextColors().getDefaultColor();
  }

  private static ImmutableList<String> extractAllTexts(ImmutableList<Button> chosenButtons) {
    return chosenButtons.stream()
        .map(button -> button.getContentDescription().toString())
        .collect(toImmutableList());
  }
}
