package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readFileContent;

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
  
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;
  private String filePath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_layout);
    filePath = getIntent().getStringExtra("FILE_PATH");
    List<String> lines;
    try {
      lines = readFileContent(new File(filePath));
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
    try {
      Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    } finally {
      super.onStop();
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
