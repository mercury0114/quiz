package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.mercury0114.vocabulary.FilesReader.readLinesAndSort;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class EditContentActivity extends AppCompatActivity {
  private static final int EXTRA_BLANK_LINES = 10;

  private final ArrayList<EditText> contentTextViews = new ArrayList();

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
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.edit_content_id);
    for (String line : readLinesAndSort(new File(filePath))) {
      EditText editText = createEditView(line);
      linearLayout.addView(editText);
      contentTextViews.add(editText);
    }
    for (int i = 0; i < EXTRA_BLANK_LINES; i++) {
      EditText editText = createEditView("");
      linearLayout.addView(editText);
      contentTextViews.add(editText);
    }
  }

  private ImmutableList<String> getNonEmptyLines() {
    return contentTextViews.stream()
        .map(editText -> editText.getText().toString())
        .filter(line -> !line.isEmpty())
        .collect(toImmutableList());
  }

  private EditText createEditView(String line) {
    EditText editText = new EditText(this);
    editText.setText(line);
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
