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
import androidx.lifecycle.ViewModelProvider;
import com.google.common.collect.ImmutableList;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;
import com.mercury0114.vocabulary.QuestionAnswer.Column;
import java.util.ArrayList;

public class VocabularyActivity extends AppCompatActivity {
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;
  private VocabularyChecker vocabularyChecker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.file_layout);
    ArrayList<String> texts = (ArrayList<String>) getIntent().getSerializableExtra("TEXTS");
    Column column = Column.valueOf(getIntent().getStringExtra("COLUMN"));
    VocabularyCheckerModel viewModel =
        new ViewModelProvider(this).get(VocabularyCheckerModel.class);
    vocabularyChecker =
        viewModel.createOrGetChecker(/* penaltyFactor= */ 2, column, ImmutableList.copyOf(texts));

    questionsRemainingView = (TextView) findViewById(R.id.questions_remaining_id);
    questionView = (TextView) findViewById(R.id.question_view_id);
    statusView = (TextView) findViewById(R.id.status_view_id);
    updateTextViews("Type answer in the space above");
    final Button revealAnswerButton = findViewById(R.id.reveal_answer_button_id);
    revealAnswerButton.setOnClickListener(
        new OnClickListener() {
          public void onClick(View view) {
            updateTextViews(
                String.format("%s: %s", questionView.getText(), vocabularyChecker.revealAnswer()));
          }
        });
    final EditText editText = (EditText) findViewById(R.id.text_input_id);
    editText.setOnKeyListener(
        new OnKeyListener() {
          @Override
          public boolean onKey(View view, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)) {
              AnswerStatus answerStatus =
                  vocabularyChecker.checkAnswer(editText.getText().toString());
              editText.getText().clear();
              updateTextViews(getStatusViewText(answerStatus));
              return true;
            }
            return false;
          }
        });
  }

  private String getStatusViewText(AnswerStatus answerStatus) {
    switch (answerStatus) {
      case CORRECT:
        return "Correct, next question";
      case CLOSE:
        return "Close, try again";
      case WRONG:
        return "Wrong, try again";
    }
    throw new RuntimeException("Wrong answerStatus enum value");
  }

  private void updateTextViews(String statusViewText) {
    if (vocabularyChecker.questionsRemaining() == 0) {
      finish();
      return;
    }
    questionsRemainingView.setText(vocabularyChecker.questionsRemaining() + " questions remain:");
    questionView.setText(vocabularyChecker.nextQuestion());
    statusView.setText(statusViewText);
  }
}
