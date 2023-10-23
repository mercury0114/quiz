package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.readLinesAndSort;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class SelectCustomWordsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.select_custom_content_layout);
    String filePath = getIntent().getStringExtra("PATH");
    addButtonsToLinearLayout(filePath);
  }

  private void addButtonsToLinearLayout(String filePath) {
    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_view_id);
    for (String line : readLinesAndSort(new File(filePath))) {
      Button button = new Button(this);
      button.setText(line);
      linearLayout.addView(button);
    }
  }
}
