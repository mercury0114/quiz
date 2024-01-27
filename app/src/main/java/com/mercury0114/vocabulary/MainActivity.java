package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesManager.VOCABULARY_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_layout);
    new File(VOCABULARY_PATH).mkdirs();

    Button button = findViewById(R.id.continue_button_id);
    button.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, FolderActivity.class);
            intent.putExtra("PATH", VOCABULARY_PATH);
            startActivity(intent);
          }
        });
  }
}
