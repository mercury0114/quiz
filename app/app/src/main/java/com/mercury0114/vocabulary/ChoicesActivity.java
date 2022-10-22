package com.mercury0114.vocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury0114.vocabulary.QuestionAnswer.Column;

public class ChoicesActivity extends AppCompatActivity {
  String filePath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choices_layout);
    filePath = getIntent().getStringExtra("FILE_PATH");
    String fileName = getIntent().getStringExtra("FILE_NAME");
    TextView textView = (TextView)findViewById(R.id.file_name_text_view_id);
    textView.setText(fileName);
    final Button leftColumnButton = findViewById(R.id.left_column_button_id);
    leftColumnButton.setOnClickListener(
        createColumnButtonListener(Column.LEFT));
    final Button rightColumnButton = findViewById(R.id.right_column_button_id);
    rightColumnButton.setOnClickListener(
        createColumnButtonListener(Column.RIGHT));
    final Button viewContentButton = findViewById(R.id.view_content_button_id);
    viewContentButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) { viewContentButton.setText(filePath); }
    });
  }

  private OnClickListener createColumnButtonListener(Column column) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(ChoicesActivity.this, FileActivity.class);
        intent.putExtra("FILE_PATH", filePath);
        intent.putExtra("COLUMN", column.name());
        startActivity(intent);
      }
    };
  }
}
