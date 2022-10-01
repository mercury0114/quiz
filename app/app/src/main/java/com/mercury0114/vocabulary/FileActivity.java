package com.mercury0114.vocabulary;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class FileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        String filePath = getIntent().getStringExtra("FILE_PATH");
    }
}
