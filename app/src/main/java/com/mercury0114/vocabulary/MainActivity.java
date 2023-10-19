package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.VOCABULARY_PATH;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new File(VOCABULARY_PATH).mkdirs();
    Intent intent = new Intent(MainActivity.this, FolderActivity.class);
    intent.putExtra("PATH", VOCABULARY_PATH);
    startActivity(intent);
  }
}
