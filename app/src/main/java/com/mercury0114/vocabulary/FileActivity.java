package com.mercury0114.vocabulary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;

public class FileActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choices_layout);
    String filePath = getIntent().getStringExtra("PATH");
    TextView textView = (TextView) findViewById(R.id.file_name_text_view_id);
    textView.setText(new File(filePath).getName());

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

    final Button editContentButton = findViewById(R.id.edit_content_button_id);
    editContentButton.setOnClickListener(createEditContentButtonListener(filePath));

    final Button deleteFileButton = findViewById(R.id.delete_file_button_id);
    deleteFileButton.setOnClickListener(createDeleteFileListener(filePath));
  }

  private OnClickListener createColumnButtonListener(Column column, String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = buildIntentForVocabularyActivity(column, filePath);
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

  private Intent buildIntentForVocabularyActivity(Column column, String filePath) {
    Intent intent = new Intent(FileActivity.this, VocabularyActivity.class);
    ImmutableList<String> texts = FilesReader.readLinesAndSort(new File(filePath));
    intent.putExtra("TEXTS", texts);
    intent.putExtra("COLUMN", column.name());
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
}
