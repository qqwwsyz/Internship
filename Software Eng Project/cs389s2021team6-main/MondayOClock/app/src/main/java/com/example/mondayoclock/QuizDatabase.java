package com.example.mondayoclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mondayoclock.QuizOutput.QuizTable;

import java.util.ArrayList;
import java.util.List;


public class QuizDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MathQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public QuizDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizTable.TABLE_NAME + " ( " +
                QuizTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizTable.COLUMN_QUESTION + " TEXT, " +
                QuizTable.COLUMN_OPTION1 + " TEXT, " +
                QuizTable.COLUMN_OPTION2 + " TEXT, " +
                QuizTable.COLUMN_OPTION3 + " TEXT, " +
                QuizTable.COLUMN_ANSWER_NR + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuizTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuizTable() {
        Question q1 = new Question("( - 5 ) * 9 - 8 + 5 = ?", "-48", "48", "0", 1);
        popQuestion(q1);
        Question q2 = new Question("6 * 3 - 5 + 10 = ?", "25", "23", "22", 2);
        popQuestion(q2);
        Question q3 = new Question("10 * 10 - 10 + 10 / 10 = ?", "1", "90", "91", 3);
        popQuestion(q3);
        Question q4 = new Question("15 / 15 + 19 + 25 - 5 = ?", "40", "42", "39", 1);
        popQuestion(q4);
        Question q5 = new Question("1-1-1-1-1-1-1-1+1=?", "-8", "-5", "-7", 2);
        popQuestion(q5);
    }

    private void popQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuizTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuizTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuizTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuizTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuizTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        db.insert(QuizTable.TABLE_NAME, null, cv);
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuizTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuizTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizTable.COLUMN_ANSWER_NR)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}