package com.mercury0114.vocabulary;

import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import android.view.View;
import android.view.View.OnClickListener;


public class FileActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file);
    String filePath = getIntent().getStringExtra("FILE_PATH");
    VocabularyChecker vocabularyChecker = new VocabularyChecker(2);
    try {
        vocabularyChecker.prepareQuestions(new File(filePath));
    } catch (IOException exception) {
        throw new RuntimeException(exception);
    }
    TextView textView = (TextView)findViewById(R.id.text_view_id);
    textView.setText(filePath + " " + vocabularyChecker.questionsRemaining());

    final Button button = findViewById(R.id.submit_button_id);
    button.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {}
    });
  }
}
