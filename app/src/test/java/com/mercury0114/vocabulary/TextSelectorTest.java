package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.TextSelector.CHOSEN_COLOR_CODE;
import static com.mercury0114.vocabulary.TextSelector.DEFAULT_COLOR_CODE;
import static com.mercury0114.vocabulary.TextSelector.extractChosenTexts;
import static com.mercury0114.vocabulary.TextSelector.getToggledColorCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.res.ColorStateList;
import android.widget.Button;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.TextSelector.NoChosenTextException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TextSelectorTest {
  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.LENIENT);

  private static final int UNSEEN_COLOR_CODE = 123;

  @Test
  public void getToggledColorCode_forDefaultButtonReturnsChosenColorCode() {
    Button button = createButton("question, answer", DEFAULT_COLOR_CODE);
    assertEquals(getToggledColorCode(button), CHOSEN_COLOR_CODE);
  }

  @Test
  public void getToggledColorCode_forChosenButtonReturnsDefaultColorCode() {
    Button button = createButton("question, answer", CHOSEN_COLOR_CODE);
    assertEquals(getToggledColorCode(button), DEFAULT_COLOR_CODE);
  }

  @Test
  public void getToggledColorCode_forButtonWithUnseenColor_throwsException() {
    Button button = createButton("question, answer", UNSEEN_COLOR_CODE);

    assertThrows(IllegalArgumentException.class, () -> getToggledColorCode(button));
  }

  @Test
  public void extractChosenTexts_emptyButtonsList_throwsException() {
    assertThrows(NoChosenTextException.class, () -> extractChosenTexts(ImmutableList.of()));
  }

  @Test
  public void extractChosenTexts_noChosenButtons_throwsException() {
    Button button = createButton("question, answer", DEFAULT_COLOR_CODE);

    assertThrows(NoChosenTextException.class, () -> extractChosenTexts(ImmutableList.of(button)));
  }

  @Test
  public void extractChosenTexts_singleChosenButton_returnsChosenButtonText() {
    Button button = createButton("question, answer", CHOSEN_COLOR_CODE);

    assertEquals(
        extractChosenTexts(ImmutableList.of(button)), ImmutableList.of("question, answer"));
  }

  @Test
  public void extractChosenTexts_oneChosenOneDefaultButton_returnsOnlyChosenButtonText() {
    Button chosenButton = createButton("chosen question, answer", CHOSEN_COLOR_CODE);
    Button defaultButton = createButton("default question, answer", DEFAULT_COLOR_CODE);

    assertEquals(
        extractChosenTexts(ImmutableList.of(chosenButton, defaultButton)),
        ImmutableList.of("chosen question, answer"));
  }

  @Test
  public void extractChosenTexts_buttonHasUnseenColor_throwsException() {
    Button button = createButton("question, answer", UNSEEN_COLOR_CODE);

    assertThrows(
        IllegalArgumentException.class, () -> extractChosenTexts(ImmutableList.of(button)));
  }

  private static Button createButton(String content, int colorCode) {
    ColorStateList colorStateList = mock(ColorStateList.class);
    when(colorStateList.getDefaultColor()).thenReturn(colorCode);
    Button button = mock(Button.class);
    when(button.getTextColors()).thenReturn(colorStateList);
    when(button.getText()).thenReturn(content);
    return button;
  }
}
