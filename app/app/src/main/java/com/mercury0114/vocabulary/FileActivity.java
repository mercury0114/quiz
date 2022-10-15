package com.mercury0114.vocabulary;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;

public class FileActivity extends AppCompatActivity {
  private TextView questionsRemainingView;
  private TextView questionView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file);
    String filePath = getIntent().getStringExtra("FILE_PATH");
    vocabularyChecker = new VocabularyChecker(2);
    try {
      vocabularyChecker.prepareQuestions(new File(filePath));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    questionsRemainingView =
        (TextView)findViewById(R.id.questions_remaining_id);
    questionView = (TextView)findViewById(R.id.question_view_id);
    updateQuestionsViews();

    final Button revealAnswerButton = findViewById(R.id.reveal_answer_button_id);
    revealAnswerButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        revealAnswerButton.setText("Answer was: " + vocabularyChecker.revealAnswer());
        updateQuestionsViews();
      }
    });
    final EditText editText = (EditText)findViewById(R.id.text_input_id);
    editText.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
            (keyCode == KeyEvent.KEYCODE_ENTER)) {
          vocabularyChecker.checkAnswer(editText.getText().toString());
          revealAnswerButton.setText("Reveal Answer");
          editText.getText().clear();
          updateQuestionsViews();
          return true;
        }
        return false;
      }
    });
  }

  private void updateQuestionsViews() {
    questionsRemainingView.setText(vocabularyChecker.questionsRemaining() +
                                   " questions remain:");
    questionView.setText(vocabularyChecker.nextQuestion());
  }
}