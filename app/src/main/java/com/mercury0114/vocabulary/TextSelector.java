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
    ImmutableList<Button> chosenButtons =
        buttonsCorrespondingToTexts.stream()
            .filter(button -> buttonIsChosen(button))
            .collect(toImmutableList());
    if (chosenButtons.isEmpty()) {
      throw new NoChosenTextException();
    }
    return extractAllTexts(chosenButtons);
  }

  private static boolean buttonIsChosen(Button button) {
    return ((ColorDrawable) button.getBackground()).getColor() == CHOSEN_COLOR_CODE;
  }

  private static ImmutableList<String> extractAllTexts(ImmutableList<Button> chosenButtons) {
    return chosenButtons.stream()
        .map(button -> button.getText().toString())
        .collect(toImmutableList());
  }
}
