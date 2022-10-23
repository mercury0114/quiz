package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;
import static com.mercury0114.vocabulary.FilesReader.readFileContent;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
  private static final int MAX_LINES = 50;

  private final ArrayList<EditText> editTextViews = new ArrayList();

  private EditText editNameText;
  private String fileName;
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_layout);
    fileName = getIntent().getStringExtra("FILE_NAME");
    editNameText = (EditText)findViewById(R.id.edit_name_text_id);
    editNameText.setText(fileName);
    List<String> lines;
    try {
      lines = readFileContent(new File(VOCABULARY_PATH + fileName));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    LinearLayout linearLayout =
        (LinearLayout)findViewById(R.id.content_view_id);
    for (String line : lines) {
      EditText editText = createEditView(line);
      linearLayout.addView(editText);
      editTextViews.add(editText);
    }
    for (int i = 0; i < MAX_LINES - lines.size(); i++) {
      EditText editText = createEditView("");
      linearLayout.addView(editText);
      editTextViews.add(editText);
    }
  }

  @Override
  protected void onStop() {
    ArrayList<String> lines = new ArrayList();
    for (EditText editText : editTextViews) {
      String line = editText.getText().toString();
      if (!line.isEmpty()) {
        lines.add(editText.getText().toString());
      }
    }
    String newName = editNameText.getText().toString();
    try {
      Files.write(Paths.get(VOCABULARY_PATH + fileName), lines,
                  StandardCharsets.UTF_8);
      File oldFile = new File(VOCABULARY_PATH + fileName);
      File newFile = new File(VOCABULARY_PATH + newName);
      Files.move(oldFile.toPath(), newFile.toPath());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    } finally {
      super.onStop();
      if (!newName.equals(fileName)) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    }
  }

  private EditText createEditView(String line) {
    EditText editText = new EditText(this);
    editText.setText(line);
    editText.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
            (keyCode == KeyEvent.KEYCODE_ENTER)) {
          // Returning true so that pressing enter wouldn't create a new line.
          return true;
        }
        return false;
      }
    });
    return editText;
  }
}
