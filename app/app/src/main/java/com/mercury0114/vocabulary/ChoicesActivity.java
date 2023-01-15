package com.mercury0114.vocabulary;

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

import android.app.AlertDialog;

import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;

public class ChoicesActivity extends AppCompatActivity {
  String filePath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.choices_layout);
    filePath = getIntent().getStringExtra("PATH");
    TextView textView = (TextView)findViewById(R.id.file_name_text_view_id);
    textView.setText(new File(filePath).getName());
    
    final Button leftColumnButton = findViewById(R.id.left_column_button_id);
    leftColumnButton.setOnClickListener(
        createColumnButtonListener(Column.LEFT));
    
    final Button rightColumnButton = findViewById(R.id.right_column_button_id);
    rightColumnButton.setOnClickListener(
        createColumnButtonListener(Column.RIGHT));
 
    final Button bothColumnsButton = findViewById(R.id.both_columns_button_id);
    bothColumnsButton.setOnClickListener(
        createColumnButtonListener(Column.BOTH));

    final Button viewContentButton = findViewById(R.id.view_content_button_id);
    viewContentButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(ChoicesActivity.this, ContentActivity.class);
        intent.putExtra("PATH", filePath);
        startActivity(intent);
      }
    });

    final Button deleteFileButton = findViewById(R.id.delete_file_button_id);
    deleteFileButton.setOnClickListener(createDeleteFileListener());
  }

  private OnClickListener createDeleteFileListener() {
    return new OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChoicesActivity.this);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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

  private OnClickListener createColumnButtonListener(Column column) {
    return new OnClickListener() {
      public void onClick(View view) {
        Intent intent = new Intent(ChoicesActivity.this, FileActivity.class);
	intent.putExtra("PATH", filePath);
        intent.putExtra("COLUMN", column.name());
        startActivity(intent);
      }
    };
  }
}
