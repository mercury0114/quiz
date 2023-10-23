package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readLinesAndSort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;

public class SelectCustomWordsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_custom_content_layout);
    String filePath = getIntent().getStringExtra("PATH");
    prepareStartButton(filePath);
    addSelectWordButtons(filePath);
  }

  private void prepareStartButton(String filePath) {
    final Button startButton = (Button) findViewById(R.id.start_practice_button_id);
    startButton.setOnClickListener(
        new OnClickListener() {
          public void onClick(View view) {
            Intent intent = new Intent(SelectCustomWordsActivity.this, FileActivity.class);
            // TODO(mariusl): implement proper logic
            intent.putExtra("PATH", filePath);
            intent.putExtra("COLUMN", Column.BOTH.name());
            startActivity(intent);
          }
        });
  }

  private void addSelectWordButtons(String filePath) {
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.select_custom_content_id);
    for (String line : readLinesAndSort(new File(filePath))) {
      linearLayout.addView(createSelectButton(line));
    }
  }

  private Button createSelectButton(String line) {
    Button button = new Button(this);
    button.setText(line);
    button.setOnClickListener(
        new OnClickListener() {
          public void onClick(View view) {
            // TODO(mariusl): implement full logic
          }
        });
    return button;
  }
}
