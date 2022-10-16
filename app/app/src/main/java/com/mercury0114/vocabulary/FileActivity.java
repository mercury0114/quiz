package com.mercury0114.vocabulary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.io.File;
import java.io.IOException;

public class FileActivity extends AppCompatActivity {
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file);
    String filePath = getIntent().getStringExtra("FILE_PATH");
    Column column = Column.valueOf(getIntent().getStringExtra("COLUMN"));
    vocabularyChecker = new VocabularyChecker(2, column);
    try {
      vocabularyChecker.prepareQuestions(new File(filePath));
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    questionsRemainingView =
        (TextView)findViewById(R.id.questions_remaining_id);
    questionView = (TextView)findViewById(R.id.question_view_id);
    statusView = (TextView)findViewById(R.id.status_view_id);
    statusView.setTextColor(Color.RED);
    updateQuestionsViews("");

    final Button revealAnswerButton =
        findViewById(R.id.reveal_answer_button_id);
    revealAnswerButton.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        updateQuestionsViews("Answer was: " + vocabularyChecker.revealAnswer());
      }
    });
    final EditText editText = (EditText)findViewById(R.id.text_input_id);
    editText.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
            (keyCode == KeyEvent.KEYCODE_ENTER)) {
          int questionsRemaining = vocabularyChecker.questionsRemaining();
          vocabularyChecker.checkAnswer(editText.getText().toString());
          String statusViewText =
              questionsRemaining < vocabularyChecker.questionsRemaining()
                  ? "Wrong answer"
                  : "";
          editText.getText().clear();
          updateQuestionsViews(statusViewText);
          return true;
        }
        return false;
      }
    });
  }

  private void updateQuestionsViews(String statusViewText) {
    if (vocabularyChecker.questionsRemaining() == 0) {
      finish();
      return;
    }
    questionsRemainingView.setText(vocabularyChecker.questionsRemaining() +
                                   " questions remain:");
    questionView.setText(vocabularyChecker.nextQuestion());
    statusView.setText(statusViewText);
  }
}
