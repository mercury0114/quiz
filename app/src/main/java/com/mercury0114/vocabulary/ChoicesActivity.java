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
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;

public class ChoicesActivity extends AppCompatActivity {
  String filePath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choices_layout);
    filePath = getIntent().getStringExtra("PATH");
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
        new OnClickListener() {
          public void onClick(View view) {
            Intent intent = buildIntentForSelectCustomWordsActivity(filePath);
            startActivity(intent);
          }
        });

    final Button editContentButton = findViewById(R.id.edit_content_button_id);
    editContentButton.setOnClickListener(
        new OnClickListener() {
          public void onClick(View view) {
            Intent intent = buildIntentForEditContentActivity(filePath);
            startActivity(intent);
          }
        });

    final Button deleteFileButton = findViewById(R.id.delete_file_button_id);
    deleteFileButton.setOnClickListener(createDeleteFileListener());
  }

  private OnClickListener createColumnButtonListener(Column column, String filePath) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = buildIntentForFileActivity(column, filePath);
        startActivity(intent);
      }
    };
  }

  private OnClickListener createDeleteFileListener() {
    return new OnClickListener() {
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChoicesActivity.this);
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

  private Intent buildIntentForFileActivity(Column column, String filePath) {
    Intent intent = new Intent(ChoicesActivity.this, FileActivity.class);
    intent.putExtra("PATH", filePath);
    intent.putExtra("COLUMN", column.name());
    return intent;
  }

  private Intent buildIntentForEditContentActivity(String filePath) {
    Intent intent = new Intent(ChoicesActivity.this, EditContentActivity.class);
    intent.putExtra("PATH", filePath);
    return intent;
  }

  private Intent buildIntentForSelectCustomWordsActivity(String filePath) {
    Intent intent = new Intent(ChoicesActivity.this, SelectCustomWordsActivity.class);
    intent.putExtra("PATH", filePath);
    return intent;
  }
}
