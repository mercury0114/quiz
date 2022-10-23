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
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new File(VOCABULARY_PATH).mkdirs();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setContentView(R.layout.main_layout);
    LinearLayout dynamicHolder = (LinearLayout)findViewById(R.id.main_view_id);
    configureNewFileButton();
    for (String fileName : GetFilesNames(VOCABULARY_PATH)) {
      dynamicHolder.addView(createExistingFileButton(fileName));
    }
  }

  private void configureNewFileButton() {
    Button button = findViewById(R.id.new_file_button_id);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String newFileName = "_enter_file_name";
        Path newFilePath = Paths.get(VOCABULARY_PATH + newFileName);
        if (Files.exists(newFilePath)) {
          button.setText("_enter_file_name exists");
          return;
        }
        try {
          Files.createFile(newFilePath);
          ArrayList<String> exampleList = new ArrayList();
          exampleList.add("enter_question_1, enter_answer_1");
          exampleList.add("enter_question_2, enter_answer_2");
          Files.write(newFilePath, exampleList, StandardCharsets.UTF_8);
        } catch (IOException exception) {
          throw new RuntimeException(exception);
        }
        Intent intent = new Intent(MainActivity.this, ContentActivity.class);
        intent.putExtra("FILE_NAME", newFileName);
        startActivity(intent);
      }
    });
  }

  private Button createExistingFileButton(String fileName) {
    Button button = new Button(this);
    button.setText(fileName);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ChoicesActivity.class);
        intent.putExtra("FILE_NAME", fileName);
        startActivity(intent);
      }
    });
    return button;
  }
}
