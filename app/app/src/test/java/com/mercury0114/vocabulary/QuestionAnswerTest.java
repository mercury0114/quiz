package com.mercury0114.vocabulary;

import org.junit.Test;
import com.mercury0114.vocabulary.QuestionAnswer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import com.mercury0114.vocabulary.QuestionAnswer.AnswerStatus;

public class QuestionAnswerTest {
    @Test
    public void answerStatus_correctAnswer_returnsCorrectStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("answer"), AnswerStatus.CORRECT);
    }

    @Test
    public void answerStatus_wrongAnswer_returnsWrongStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("wrong_answer"), AnswerStatus.WRONG);
    }

    @Test
    public void answerStatus_wrongFirstLetter_returnsCloseStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("answer"), AnswerStatus.CORRECT);
        assertEquals(qa.getAnswerStatus("Answer"), AnswerStatus.CLOSE);
    }

    @Test
    public void answerStatus_tooManyLetters_returnsCloseStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("answe"), AnswerStatus.CLOSE);
    }

    @Test
    public void answerStatus_tooFewLetters_returnsCloseStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("answerX"), AnswerStatus.CLOSE);
    }

    @Test
    public void answerStatus_wrongLetterInserted_returnsCloseStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("ansXwer"), AnswerStatus.CLOSE);
    }

    @Test
    public void answerStatus_twoWrongLetters_returnsCloseStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("AnSwer"), AnswerStatus.CLOSE);
    }

    @Test
    public void answerStatus_threeWrongLetters_returnsWrongStatus() {
        QuestionAnswer qa = new QuestionAnswer("question", "answer");
        assertEquals(qa.getAnswerStatus("ANSwer"), AnswerStatus.WRONG);
    }
}
