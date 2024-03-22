package com.mercury0114.vocabulary;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import static com.mercury0114.vocabulary.FilesManager.computeStatisticsFilePath;
import static com.mercury0114.vocabulary.FilesManager.writeToFile;
import static com.mercury0114.vocabulary.QuestionAnswer.extractQuestionAnswer;
import static com.mercury0114.vocabulary.StatisticsEntry.createEmptyStatisticsEntry;
import static com.mercury0114.vocabulary.StatisticsEntry.createStatisticsEntry;

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
import java.io.File;
import java.util.ArrayList;

public class VocabularyActivity extends AppCompatActivity {
  private Column column;
  private VocabularyChecker vocabularyChecker;
  private Statistics statistics;
  private TextView questionsRemainingView;
  private TextView questionView;
  private TextView statusView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.vocabulary_layout);
    ImmutableList<String> texts = getTextsPassedFromParentActivity();
    VocabularyCheckerModel viewModel =
        new ViewModelProvider(this).get(VocabularyCheckerModel.class);
    this.column = Column.valueOf(getIntent().getStringExtra("COLUMN"));
    this.vocabularyChecker = viewModel.createOrGetChecker(column, texts);

    String statisticsFilePath = computeStatisticsPath();
    FilesManager.createFileIfDoesNotExist(statisticsFilePath);
    this.statistics = gatherStatistics(texts);

    this.questionsRemainingView = (TextView) findViewById(R.id.questions_remaining_id);
    this.questionView = (TextView) findViewById(R.id.question_view_id);
    this.statusView = (TextView) findViewById(R.id.status_view_id);
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
              String questionBeforeCheckingAnswer = vocabularyChecker.currentQuestion();
              AnswerStatus answerStatus =
                  vocabularyChecker.checkAnswer(editText.getText().toString());
              statistics.updateOneStatisticsEntry(questionBeforeCheckingAnswer, answerStatus);
              editText.getText().clear();
              updateTextViews(getStatusViewText(answerStatus));
              return true;
            }
            return false;
          }
        });
  }

  @Override
  public void onStop() {
    saveStatisticsToFile();
    super.onStop();
  }

  private ImmutableList<String> getTextsPassedFromParentActivity() {
    ArrayList<String> texts = (ArrayList<String>) getIntent().getSerializableExtra("TEXTS");
    return ImmutableList.copyOf(texts);
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
      saveStatisticsToFile();
      finish();
      return;
    }
    questionsRemainingView.setText(vocabularyChecker.questionsRemaining() + " questions remain:");
    questionView.setText(vocabularyChecker.currentQuestion());
    statusView.setText(statusViewText);
  }

  private Statistics gatherStatistics(ImmutableList<String> texts) {
    ImmutableList<String> questions =
        texts.stream()
            .map(text -> extractQuestionAnswer(text, column).question)
            .collect(toImmutableList());
    ImmutableList<String> currentStatisticsFileLines =
        FilesManager.readLinesAndSort(new File(computeStatisticsPath()));
    ImmutableList<StatisticsEntry> existingEntries =
        currentStatisticsFileLines.stream()
            .map(line -> createStatisticsEntry(line))
            .collect(toImmutableList());
    ImmutableList<StatisticsEntry> entryForEachQuestion =
        questions.stream()
            .map(question -> findEntryOrEmpty(question, existingEntries))
            .collect(toImmutableList());
    return new Statistics(entryForEachQuestion);
  }

  private static StatisticsEntry findEntryOrEmpty(
      String question, ImmutableList<StatisticsEntry> entries) {
    return entries.stream()
        .filter(entry -> entry.question().equals(question))
        .collect(toOptional())
        .orElse(createEmptyStatisticsEntry(question));
  }

  private String computeStatisticsPath() {
    String vocabularyFilePath = getIntent().getStringExtra("PATH");
    return computeStatisticsFilePath(vocabularyFilePath, this.column);
  }

  private void saveStatisticsToFile() {
    String vocabularyFilePath = getIntent().getStringExtra("PATH");
    String statisticsPath = computeStatisticsPath();
    ImmutableList<String> currentVocabularyFileLines =
        FilesManager.readLinesAndSort(new File(vocabularyFilePath));
    ImmutableList<String> currentStatisticsFileLines =
        FilesManager.readLinesAndSort(new File(statisticsPath));
    ImmutableList<String> updatedStatisticsFileLines =
        statistics.prepareUpdatedStatisticsFileLines(
            this.column, currentVocabularyFileLines, currentStatisticsFileLines);
    writeToFile(statisticsPath, updatedStatisticsFileLines, Column.LEFT);
  }
}
