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
import java.io.File;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new File(VOCABULARY_PATH).mkdirs();
    setContentView(R.layout.activity_main);
    LinearLayout dynamicHolder = (LinearLayout)findViewById(R.id.main_view_id);
    for (String fileName : GetFilesNames(VOCABULARY_PATH)) {
      Button button = createFileButton(fileName);
      button.setText(fileName);
      dynamicHolder.addView(button);
    }
  }

  private Button createFileButton(String fileName) {
    Button button = new Button(this);
    button.setText(fileName);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, FileActivity.class);
        intent.putExtra("FILE_PATH", VOCABULARY_PATH + fileName);
        startActivity(intent);
      }
    });
    return button;
  }
}
