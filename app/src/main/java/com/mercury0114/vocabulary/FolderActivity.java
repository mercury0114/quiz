package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;
import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;
import static com.mercury0114.vocabulary.FilesReader.isStatisticsFile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FolderActivity extends AppCompatActivity {
  private static final String HIDE_STATISTICS_FILES = "Hide Statistics Files";
  private static final String SHOW_STATISTICS_FILES = "Show Hidden Statistics Files";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setContentView(R.layout.folder_layout);
    configureFolderNameTextView();
    configureNewFileButton();
    configureNewFolderButton();
    configureShowStatisticsButton();
    configureDeleteFolderButton();
    updateFilesDisplay(HIDE_STATISTICS_FILES);
  }

  private ImmutableList<Button> getButtonsToDisplay(String displayOption) {
    ImmutableList<String> fileNamesToDisplay =
        GetFilesNames(getFolderPath()).stream()
            .filter(name -> !isStatisticsFile(name) || displayOption.equals(SHOW_STATISTICS_FILES))
            .collect(toImmutableList());
    return fileNamesToDisplay.stream()
        .map(
            name ->
                isFileName(name)
                    ? createExistingFileButton(name)
                    : createExistingFolderButton(name))
        .collect(toImmutableList());
  }

  private void updateFilesDisplay(String displayOption) {
    ImmutableList<Button> buttons = getButtonsToDisplay(displayOption);
    LinearLayout dynamicHolder = (LinearLayout) findViewById(R.id.display_files_id);
    dynamicHolder.removeAllViews();
    for (Button button : buttons) {
      dynamicHolder.addView(button);
    }
  }

  private void configureFolderNameTextView() {
    TextView textView = (TextView) findViewById(R.id.folder_name_text_view_id);
    textView.setText(new File(getFolderPath()).getName());
    if (getFolderPath().equals(VOCABULARY_PATH)) {
      textView.setVisibility(View.GONE);
    }
  }

  private void configureNewFileButton() {
    Button button = findViewById(R.id.new_file_button_id);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String newFilePath = getFolderPath() + "_enter_file_name";
            if (Files.exists(Paths.get(newFilePath))) {
              button.setText("_enter_file_name exists");
              return;
            }
            try {
              Files.createFile(Paths.get(newFilePath));
              ImmutableList<String> exampleList =
                  ImmutableList.of(
                      "enter_question_1 | enter_answer_1", "enter_question_2 | enter_answer_2");
              Files.write(Paths.get(newFilePath), exampleList, StandardCharsets.UTF_8);
            } catch (IOException exception) {
              throw new RuntimeException(exception);
            }
            Intent intent = new Intent(FolderActivity.this, EditContentActivity.class);
            intent.putExtra("PATH", newFilePath);
            startActivity(intent);
          }
        });
  }

  private void configureNewFolderButton() {
    Button button = findViewById(R.id.new_folder_button_id);
    button.setTextColor(Color.RED);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String newFolderPath = getFolderPath() + "_new_folder_name/";
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

  private void configureShowStatisticsButton() {
    Button button = findViewById(R.id.show_statistics_button_id);
    button.setText(HIDE_STATISTICS_FILES);
    button.setTextColor(Color.YELLOW);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String text = button.getText().toString();
            button.setText(
                text.equals(HIDE_STATISTICS_FILES) ? SHOW_STATISTICS_FILES : HIDE_STATISTICS_FILES);
            updateFilesDisplay(button.getText().toString());
          }
        });
  }

  private void configureDeleteFolderButton() {
    Button button = findViewById(R.id.delete_folder_button_id);
    String folderPath = getFolderPath();
    if (FilesReader.GetFilesNames(folderPath).size() > 0 || folderPath.equals(VOCABULARY_PATH)) {
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
                    new File(getFolderPath()).delete();
                    finish();
                  }
                });
            builder.setMessage("DELETE FOLDER?");
            builder.create().show();
          }
        });
  }

  private Button createExistingFileButton(String fileName) {
    Button button = createBasicButton(Color.BLACK, fileName);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String path = getFolderPath() + fileName;
            Intent intent = new Intent(FolderActivity.this, FileActivity.class);
            intent.putExtra("PATH", path);
            startActivity(intent);
          }
        });
    return button;
  }

  private Button createExistingFolderButton(String folderName) {
    Button button = createBasicButton(Color.RED, folderName);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            String path = getFolderPath() + folderName + "/";
            Intent intent = new Intent(FolderActivity.this, FolderActivity.class);
            intent.putExtra("PATH", path);
            startActivity(intent);
          }
        });
    return button;
  }

  private Button createBasicButton(int colorId, String name) {
    Button button = new Button(this);
    button.setAllCaps(false);
    button.setTextColor(colorId);
    button.setText(name);
    return button;
  }

  private String getFolderPath() {
    return getIntent().getStringExtra("PATH");
  }

  private boolean isFileName(String name) {
    String path = getFolderPath() + name;
    return (new File(path)).isFile();
  }
}
