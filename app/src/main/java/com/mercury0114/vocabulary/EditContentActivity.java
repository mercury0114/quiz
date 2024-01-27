package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesManager.readLinesAndSort;
import static com.mercury0114.vocabulary.FilesManager.writeToFile;
import static com.mercury0114.vocabulary.QuestionAnswer.Column;
import static com.mercury0114.vocabulary.QuestionAnswer.WronglyFormattedLineException;
import static com.mercury0114.vocabulary.QuestionAnswer.splitIntoTwoStrings;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import java.io.File;

public class EditContentActivity extends AppCompatActivity {
  private static final int EXTRA_BLANK_LINES = 10;

  private ImmutableList<String> initialLinesFromFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_content_layout);

    File initialFile = new File(getIntent().getStringExtra("PATH"));
    initialLinesFromFile = readLinesAndSort(initialFile);

    configureContentTextViews();
  }

  @Override
  protected void onStop() {
    ImmutableList<String> newLines = getNonEmptyLines();
    if (!newLines.equals(initialLinesFromFile)) {
      writeToFile(getIntent().getStringExtra("PATH"), newLines, Column.BOTH);
    }
    super.onStop();
  }

  private void configureContentTextViews() {
    LinearLayout layout = (LinearLayout) findViewById(R.id.edit_content_id);
    for (int qaNumber = 1; qaNumber <= initialLinesFromFile.size(); qaNumber++) {
      String line = initialLinesFromFile.get(qaNumber - 1);
      ImmutableList<String> twoStrings;
      try {
        twoStrings = splitIntoTwoStrings(line);
      } catch (WronglyFormattedLineException exception) {
        twoStrings = ImmutableList.of(line, "");
      }
      addTwoStringsToLayout(layout, twoStrings, qaNumber);
    }
    for (int blankQaNumber = 1; blankQaNumber <= EXTRA_BLANK_LINES; blankQaNumber++) {
      int blankLineIndex = initialLinesFromFile.size() + blankQaNumber;
      addTwoStringsToLayout(layout, ImmutableList.of("", ""), blankLineIndex);
    }
  }

  private void addTwoStringsToLayout(
      LinearLayout layout, ImmutableList<String> twoStrings, int index) {
    layout.addView(createQuestionAnswerInfoView(index));
    layout.addView(createEditText(twoStrings.get(0), Color.LTGRAY));
    layout.addView(createEditText(twoStrings.get(1), Color.GRAY));
  }

  private TextView createQuestionAnswerInfoView(int questionAnswerNumber) {
    TextView textView = new TextView(this);
    textView.setPadding(10, 50, 0, 0);
    textView.setText(String.format("Question-answer pair %d:", questionAnswerNumber));
    return textView;
  }

  private ImmutableList<String> getNonEmptyLines() {
    LinearLayout linesLayout = (LinearLayout) findViewById(R.id.edit_content_id);
    ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
    for (int i = 0; i < linesLayout.getChildCount(); i += 3) {
      String leftText = ((EditText) linesLayout.getChildAt(i + 1)).getText().toString();
      String rightText = ((EditText) linesLayout.getChildAt(i + 2)).getText().toString();
      if (leftText.isEmpty() && rightText.isEmpty()) {
        continue;
      }
      linesBuilder.add(
          String.format("%s | %s", addWarningIfEmpty(leftText), addWarningIfEmpty(rightText)));
    }
    return linesBuilder.build();
  }

  private static String addWarningIfEmpty(String s) {
    return s.isEmpty() ? "FILL OR CLEAR ROW" : s;
  }

  private EditText createEditText(String line, int colorCode) {
    EditText editText = new EditText(this);
    editText.setText(line);
    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    editText.setBackgroundColor(colorCode);
    editText.setOnKeyListener(
        new OnKeyListener() {
          @Override
          public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)) {
              // Returning true so that pressing enter wouldn't create a new line.
              return true;
            }
            return false;
          }
        });
    return editText;
  }
}
