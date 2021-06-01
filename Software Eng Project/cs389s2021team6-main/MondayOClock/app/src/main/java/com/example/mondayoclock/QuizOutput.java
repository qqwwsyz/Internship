package com.example.mondayoclock;

import android.provider.BaseColumns;

public final class QuizOutput {

    private QuizOutput() {
    }

    public static class QuizTable implements BaseColumns {

        public static final String TABLE_NAME = "mathquiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
    }
}
