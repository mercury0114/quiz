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
    Intent intent = new Intent(MainActivity.this, FolderActivity.class);
    intent.putExtra("PATH", VOCABULARY_PATH);
    startActivity(intent);
  }
}
