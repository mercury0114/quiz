package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readFileContent;
import static java.util.Collections.sort;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
  private static final int EXTRA_BLANK_LINES = 10;

  private final ArrayList<EditText> contentTextViews = new ArrayList();

  private String filePath;
  private EditText editFileNameText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_layout);
    filePath = getIntent().getStringExtra("PATH");
    editFileNameText = (EditText) findViewById(R.id.edit_file_name_text_id);
    editFileNameText.setText(new File(filePath).getName());
    configureContentTextViews();
  }

  @Override
  protected void onStop() {
    ArrayList<String> lines = getNonEmptyLines();
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

  private ArrayList<String> getNonEmptyLines() {
    ArrayList<String> lines = new ArrayList();
    for (EditText editText : contentTextViews) {
      String line = editText.getText().toString();
      if (!line.isEmpty()) {
        lines.add(editText.getText().toString());
      }
    }
    return lines;
  }

  private void configureContentTextViews() {
    List<String> lines;
    try {
      lines = readFileContent(new File(filePath));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    sort(lines);
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_view_id);
    for (String line : lines) {
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
