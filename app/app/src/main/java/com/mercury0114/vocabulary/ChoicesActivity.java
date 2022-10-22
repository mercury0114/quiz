package com.mercury0114.vocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;
import java.io.IOException;

public class ChoicesActivity extends AppCompatActivity {
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choices_layout);

    final Button leftColumnButton = findViewById(R.id.left_column_button_id);
    leftColumnButton.setOnClickListener(
        createColumnButtonListener(Column.LEFT));
    final Button rightColumnButton = findViewById(R.id.right_column_button_id);
    rightColumnButton.setOnClickListener(
        createColumnButtonListener(Column.RIGHT));
  }

  private OnClickListener createColumnButtonListener(Column column) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(ChoicesActivity.this, FileActivity.class);
        intent.putExtra("FILE_PATH", getIntent().getStringExtra("FILE_PATH"));
        intent.putExtra("COLUMN", column.name());
        startActivity(intent);
      }
    };
  }
}
