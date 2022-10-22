package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readFileContent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_layout);
    String filePath = getIntent().getStringExtra("FILE_PATH");
    List<String> lines;
    try {
      lines = readFileContent(new File(filePath));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    LinearLayout linearLayout =
        (LinearLayout)findViewById(R.id.content_view_id);
    for (String line : lines) {
      linearLayout.addView(createQuestionAnswerButton(line));
    }
  }

  private Button createQuestionAnswerButton(String line) {
    Button button = new Button(this);
    button.setText(line);
    return button;
  }
}
