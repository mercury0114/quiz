package com.mercury0114.vocabulary;

import static com.mercury0114.vocabulary.FilesReader.GetFilesNames;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;
import java.io.File;
import android.content.Intent;

import com.mercury0114.vocabulary.FileActivity;

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
            Button button = createFileButton(fileName);
            button.setText(fileName);
            dynamicHolder.addView(button);
        }
    }

    private Button createFileButton(String fileName) {
        Button button = new Button(this);
        button.setText(fileName);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                intent.putExtra("FILE_PATH", PATH + fileName);
                startActivity(intent);
            }
        });
        return button;
    }
}
