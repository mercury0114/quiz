package com.mercury0114.vocabulary;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FileActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file);
    String filePath = getIntent().getStringExtra("FILE_PATH");
    TextView textView = (TextView)findViewById(R.id.textView);
    textView.setText(filePath);
  }
}
