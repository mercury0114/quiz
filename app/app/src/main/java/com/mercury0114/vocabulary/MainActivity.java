package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;
import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury0114.vocabulary.FileActivity;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new File(VOCABULARY_PATH).mkdirs();
    setContentView(R.layout.activity_main);
    LinearLayout dynamicHolder = (LinearLayout)findViewById(R.id.main_view_id);
    for (String fileName : GetFilesNames(VOCABULARY_PATH)) {
      dynamicHolder.addView(createFileButton(fileName, Column.LEFT));
      dynamicHolder.addView(createFileButton(fileName, Column.RIGHT));
    }
  }

  private Button createFileButton(String fileName, Column column) {
    Button button = new Button(this);
    button.setText(fileName + "\n ask from " + column.name() + " column");
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, FileActivity.class);
        intent.putExtra("FILE_PATH", VOCABULARY_PATH + fileName);
        intent.putExtra("COLUMN", column.name());
        startActivity(intent);
      }
    });
    return button;
  }
}
