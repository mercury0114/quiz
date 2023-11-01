package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import com.google.common.collect.ImmutableList;

final class TextSelector {
  public static class NoChosenTextException extends RuntimeException {}

  static int CHOSEN_COLOR_CODE = Color.GREEN;
  static int DEFAULT_COLOR_CODE = Color.BLUE;

  static ImmutableList<String> extractChosenTexts(
      ImmutableList<Button> buttonsCorrespondingToTexts) {
    checkAllColorsAreValid(buttonsCorrespondingToTexts);
    ImmutableList<Button> chosenButtons = selectOnlyChosenButtons(buttonsCorrespondingToTexts);
    if (chosenButtons.isEmpty()) {
      throw new NoChosenTextException();
    }
    return extractAllTexts(chosenButtons);
  }

  private static void checkAllColorsAreValid(ImmutableList<Button> buttons) {
    for (Button button : buttons) {
      int colorCode = getColorCode(button);
      if (!ImmutableList.of(CHOSEN_COLOR_CODE, DEFAULT_COLOR_CODE).contains(colorCode)) {
        throw new IllegalArgumentException(
            String.format("Button color %d not supported\n", colorCode));
      }
    }
  }

  private static ImmutableList<Button> selectOnlyChosenButtons(ImmutableList<Button> buttons) {
    return buttons.stream()
        .filter(button -> getColorCode(button) == CHOSEN_COLOR_CODE)
        .collect(toImmutableList());
  }

  private static int getColorCode(Button button) {
    return ((ColorDrawable) button.getBackground()).getColor();
  }

  private static ImmutableList<String> extractAllTexts(ImmutableList<Button> chosenButtons) {
    return chosenButtons.stream()
        .map(button -> button.getText().toString())
        .collect(toImmutableList());
  }
}
