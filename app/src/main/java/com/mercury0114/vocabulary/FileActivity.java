package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.mercury0114.vocabulary.FilesReader.computeStatisticsFilePath;
import static com.mercury0114.vocabulary.QuestionAnswer.extractQuestionAnswer;
import static com.mercury0114.vocabulary.Statistics.createStatisticsFromLines;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileActivity extends AppCompatActivity {
  private EditText fileNameEditText;
  private String initialFileName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.file_layout);
    String filePath = getIntent().getStringExtra("PATH");
    this.initialFileName = new File(filePath).getName();
    this.fileNameEditText = prepareFileNameEditText(initialFileName);

    final Button leftColumnButton = findViewById(R.id.left_column_button_id);
    leftColumnButton.setOnClickListener(createColumnButtonListener(Column.LEFT, filePath));

    final Button rightColumnButton = findViewById(R.id.right_column_button_id);
    rightColumnButton.setOnClickListener(createColumnButtonListener(Column.RIGHT, filePath));

    final Button bothColumnsButton = findViewById(R.id.both_columns_button_id);
    bothColumnsButton.setOnClickListener(createColumnButtonListener(Column.BOTH, filePath));

    final Button customLeftColumnButton = findViewById(R.id.custom_left_column_button_id);
    customLeftColumnButton.setOnClickListener(
        createCustomColumnButtonListener(Column.LEFT, filePath));

    final Button customRightColumnButton = findViewById(R.id.custom_right_column_button_id);
    customRightColumnButton.setOnClickListener(
        createCustomColumnButtonListener(Column.RIGHT, filePath));

    final Button weakestWordsLeftColumnButton =
        findViewById(R.id.weakest_words_left_column_button_id);
    weakestWordsLeftColumnButton.setOnClickListener(
        createWeakestWordsColumnButtonListener(Column.LEFT, filePath));

    final Button weakestWordsRightColumnButton =
        findViewById(R.id.weakest_words_right_column_button_id);
    weakestWordsRightColumnButton.setOnClickListener(
        createWeakestWordsColumnButtonListener(Column.RIGHT, filePath));

    final Button editContentButton = findViewById(R.id.edit_content_button_id);
    editContentButton.setOnClickListener(createEditContentButtonListener(filePath));

    final Button deleteFileButton = findViewById(R.id.delete_file_button_id);
    deleteFileButton.setOnClickListener(createDeleteFileListener(filePath));
  }

  private EditText prepareFileNameEditText(String initialFileName) {
    EditText editText = (EditText) findViewById(R.id.file_name_edit_text_id);
    editText.setText(initialFileName);
    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    editText.setOnKeyListener(
        new OnKeyListener() {
          @Override
          public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)) {
              // Returning true so that pressing enter wouldn't create a new line.
              return true;
            }
            return false;
          }
        });
    return editText;
  }

  private OnClickListener createColumnButtonListener(Column column, String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        ImmutableList<String> texts = FilesReader.readLinesAndSort(new File(filePath));
        Intent intent = buildIntentForVocabularyActivity(column, filePath, texts);
        startActivity(intent);
      }
    };
  }

  private OnClickListener createCustomColumnButtonListener(Column column, String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = buildIntentForSelectCustomWordsActivity(column, filePath);
        startActivity(intent);
      }
    };
  }

  private OnClickListener createWeakestWordsColumnButtonListener(Column column, String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        ImmutableList<String> texts = FilesReader.readLinesAndSort(new File(filePath));
        ImmutableList<QuestionAnswer> questions =
            texts.stream()
                .map(text -> extractQuestionAnswer(text, column))
                .collect(toImmutableList());
        ImmutableList<String> statisticsFileLines =
            FilesReader.readLinesAndSort(new File(computeStatisticsFilePath(filePath, column)));
        Statistics statistics = createStatisticsFromLines(statisticsFileLines);
        ImmutableList<String> hardestQuestions =
            statistics.getHardestQuestions(/* requestedNumber= */ Math.min(texts.size(), 5));
        ImmutableList<String> textsToAsk =
            texts.stream()
                .filter(
                    text -> hardestQuestions.contains(extractQuestionAnswer(text, column).question))
                .collect(toImmutableList());

        Intent intent = buildIntentForVocabularyActivity(column, filePath, textsToAsk);
        startActivity(intent);
      }
    };
  }

  private OnClickListener createEditContentButtonListener(String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = buildIntentForEditContentActivity(filePath);
        startActivity(intent);
      }
    };
  }

  private OnClickListener createDeleteFileListener(String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
        builder.setPositiveButton(
            "Yes",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                new File(filePath).delete();
                finish();
              }
            });
        builder.setMessage("DELETE FILE?");
        builder.create().show();
      }
    };
  }

  private Intent buildIntentForVocabularyActivity(
      Column column, String filePath, ImmutableList<String> texts) {
    Intent intent = new Intent(FileActivity.this, VocabularyActivity.class);
    intent.putExtra("COLUMN", column.name());
    intent.putExtra("PATH", filePath);
    intent.putExtra("TEXTS", texts);
    return intent;
  }

  private Intent buildIntentForEditContentActivity(String filePath) {
    Intent intent = new Intent(FileActivity.this, EditContentActivity.class);
    intent.putExtra("PATH", filePath);
    return intent;
  }

  private Intent buildIntentForSelectCustomWordsActivity(Column column, String filePath) {
    Intent intent = new Intent(FileActivity.this, SelectCustomWordsActivity.class);
    intent.putExtra("COLUMN", column.name());
    intent.putExtra("PATH", filePath);
    return intent;
  }

  @Override
  protected void onPause() {
    String newFileName = fileNameEditText.getText().toString();
    if (!newFileName.equals(initialFileName)) {
      renameFile(newFileName);
    }
    super.onPause();
  }

  private void renameFile(String newFileName) {
    Path initialPath = Paths.get(getIntent().getStringExtra("PATH"));
    Path newPath = Paths.get(initialPath.getParent() + "/" + newFileName);
    try {
      Files.move(initialPath, newPath);
    } catch (IOException exception) {
      throw new RuntimeException("Renaming file failed", exception);
    }
  }
}
