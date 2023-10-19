package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;
import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setContentView(R.layout.main_layout);
    configureFolderNameTextView();
    configureNewFileButton();
    configureNewFolderButton();
    configureDeleteFolderButton();
    String folderPath = getPath();
    LinearLayout dynamicHolder = (LinearLayout) findViewById(R.id.main_view_id);
    for (String fileName : GetFilesNames(folderPath)) {
      dynamicHolder.addView(createExistingFileButton(fileName));
    }
  }

  private void configureFolderNameTextView() {
    TextView textView = (TextView) findViewById(R.id.folder_name_text_view_id);
    textView.setText(new File(getPath()).getName());
    if (getPath().equals(VOCABULARY_PATH)) {
      textView.setVisibility(View.GONE);
    }
  }

  private void configureNewFileButton() {
    Button button = findViewById(R.id.new_file_button_id);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String newFilePath = getPath() + "_enter_file_name/";
            if (Files.exists(Paths.get(newFilePath))) {
              button.setText("_enter_file_name exists");
              return;
            }
            try {
              Files.createFile(Paths.get(newFilePath));
              ArrayList<String> exampleList = new ArrayList();
              exampleList.add("enter_question_1, enter_answer_1");
              exampleList.add("enter_question_2, enter_answer_2");
              Files.write(Paths.get(newFilePath), exampleList, StandardCharsets.UTF_8);
            } catch (IOException exception) {
              throw new RuntimeException(exception);
            }
            Intent intent = new Intent(FolderActivity.this, ContentActivity.class);
            intent.putExtra("PATH", newFilePath);
            startActivity(intent);
          }
        });
  }

  private void configureNewFolderButton() {
    Button button = findViewById(R.id.new_folder_button_id);
    button.setTextColor(0xFFFF0000);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String newFolderPath = getPath() + "_new_folder_name/";
            if (Files.exists(Paths.get(newFolderPath))) {
              button.setText("new_folder_name exists");
              return;
            }
            new File(newFolderPath).mkdirs();
            Intent intent = new Intent(FolderActivity.this, FolderActivity.class);
            intent.putExtra("PATH", newFolderPath);
            startActivity(intent);
          }
        });
  }

  private void configureDeleteFolderButton() {
    Button button = findViewById(R.id.delete_folder_button_id);
    if (FilesReader.GetFilesNames(getPath()).size() > 0 || getPath().equals(VOCABULARY_PATH)) {
      button.setVisibility(View.GONE);
    }
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FolderActivity.this);
            builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    new File(getPath()).delete();
                    finish();
                  }
                });
            builder.setMessage("DELETE FOLDER?");
            builder.create().show();
          }
        });
  }

  private Button createExistingFileButton(String fileName) {
    Button button = new Button(this);
    String path = getPath() + fileName + "/";
    if (!new File(path).isFile()) {
      button.setTextColor(0xFFFF0000);
    }
    button.setText(fileName);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String path = getPath() + fileName + "/";
            Intent intent =
                new File(path).isFile()
                    ? new Intent(FolderActivity.this, ChoicesActivity.class)
                    : new Intent(FolderActivity.this, FolderActivity.class);
            intent.putExtra("PATH", path);
            startActivity(intent);
          }
        });
    return button;
  }

  private String getPath() {
    return getIntent().getStringExtra("PATH");
  }
}
