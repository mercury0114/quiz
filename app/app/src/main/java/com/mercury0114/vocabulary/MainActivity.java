package com.mercury0114.vocabulary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;

public class MainActivity extends AppCompatActivity {

    private final static String PATH = Environment.getExternalStorageDirectory() +
                "/Documents/com.mercury0114.vocabulary/vocabulary/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new File(PATH).mkdirs();
        setContentView(R.layout.activity_main);
        LinearLayout dynamicHolder = (LinearLayout) findViewById(R.id.mainView);
        for (String fileName : GetFilesNames(PATH)) {
            TextView t = new TextView(this);
            t.setText(fileName);
            dynamicHolder.addView(t);
        }
    }
}
