package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readLinesAndSort;
import static com.mercury0114.vocabulary.QuestionAnswer.WronglyFormattedLineException;
import static com.mercury0114.vocabulary.QuestionAnswer.splitIntoTwoStrings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EditContentActivity extends AppCompatActivity {
  private static final int EXTRA_BLANK_LINES = 10;

  private EditText editFileNameText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_content_layout);
    String filePath = getIntent().getStringExtra("PATH");
    editFileNameText = (EditText) findViewById(R.id.edit_file_name_text_id);
    editFileNameText.setText(new File(filePath).getName());
    configureContentTextViews(filePath);
  }

  @Override
  protected void onStop() {
    String filePath = getIntent().getStringExtra("PATH");
    ImmutableList<String> lines = getNonEmptyLines();
    String newName = editFileNameText.getText().toString();
    try {
      Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
      File oldFile = new File(filePath);
      File newFile = new File(new File(filePath).getParent() + "/" + newName);
      Files.move(oldFile.toPath(), newFile.toPath());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    } finally {
      super.onStop();
      if (!newName.equals(new File(filePath).getName())) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    }
  }

  private void configureContentTextViews(String filePath) {
    LinearLayout leftLayout = (LinearLayout) findViewById(R.id.edit_content_left_column_id);
    LinearLayout rightLayout = (LinearLayout) findViewById(R.id.edit_content_right_column_id);
    for (String line : readLinesAndSort(new File(filePath))) {
      ImmutableList<String> twoStrings;
      try {
        twoStrings = splitIntoTwoStrings(line);
      } catch (WronglyFormattedLineException exception) {
        twoStrings = ImmutableList.of(line, "");
      }
      leftLayout.addView(createEditView(twoStrings.get(0)));
      rightLayout.addView(createEditView(twoStrings.get(1)));
    }
    for (int i = 0; i < EXTRA_BLANK_LINES; i++) {
      leftLayout.addView(createEditView(""));
      rightLayout.addView(createEditView(""));
    }
  }

  private ImmutableList<String> getNonEmptyLines() {
    LinearLayout leftLayout = (LinearLayout) findViewById(R.id.edit_content_left_column_id);
    LinearLayout rightLayout = (LinearLayout) findViewById(R.id.edit_content_right_column_id);
    ImmutableList.Builder<String> linesBuilder = ImmutableList.builder();
    for (int i = 0; i < leftLayout.getChildCount(); i++) {
      EditText leftEditText = (EditText) leftLayout.getChildAt(i);
      EditText rightEditText = (EditText) rightLayout.getChildAt(i);
      String leftText = leftEditText.getText().toString();
      String rightText = rightEditText.getText().toString();
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

  private EditText createEditView(String line) {
    EditText editText = new EditText(this);
    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    editText.setText(line);
    editText.setLayoutParams(
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

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
