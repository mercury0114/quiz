package com.mercury0114.vocabulary;

import org.junit.Test;
import com.mercury0114.vocabulary.QuestionAnswer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestionAnswerTest {
    @Test
    public void correctAnswer_returnsTrue() {
        QuestionAnswer questionAnswer = new QuestionAnswer("question", "answer");
        assertTrue(questionAnswer.correctAnswer("answer"));
    }

    @Test
    public void correctAnswer_returnsFalse() {
        QuestionAnswer questionAnswer = new QuestionAnswer("question", "answer");
        assertFalse(questionAnswer.correctAnswer("wrong_answer"));
    }
}
