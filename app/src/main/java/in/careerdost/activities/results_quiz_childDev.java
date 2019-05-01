package in.careerdost.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import in.careerdost.R;
import in.careerdost.database.CustomPercentFormatter;
import in.careerdost.database.Question_TET_childDev;

public class results_quiz_maths extends AppCompatActivity {

    ArrayList<Question_TET_maths> questionTETmathsList = new ArrayList<>();

    TextView mGrade, mFinalScore, mCorrectAns, pAssPercentage;
    TextView total_ques, catQuizMaths, skipped_ques, correct_ans, incorrect_ans;
    Button mRetryButton, btnRetake, btnShareResult;
    ImageView resultsImage;
    Question_TET_childDev currentQ;
    private int totalQuestion;

    private DecimalFormat mFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_quiz_layout);

        catQuizChildDev = findViewById(R.id.cat_quiz_title);
        mGrade = findViewById(R.id.congrats);
        mFinalScore = findViewById(R.id.youScored);

        btnRetake = findViewById(R.id.btnRetake);
        btnShareResult = findViewById(R.id.btnShareResult);
        resultsImage = findViewById(R.id.resultsImage);

        Bundle b = getIntent().getExtras();
        final String ctgTitle = b.getString("CTG_TITLE");
        final int score = b.getInt("score");
        final int totalQues = b.getInt("totalQuestion");
        final int skipped = b.getInt("skipped");
        final int correctAnswer = b.getInt("correctAnswer");
        final int incorrectAnswer = b.getInt("incorrectAnswer");
        int eachCorrectAnswerScore = 5;
        int totalQuesScore = totalQues * eachCorrectAnswerScore;
        String mFinalScoreText = (getResources().getString(R.string.you_scored) + " " + score + " " + getResources().getString(R.string.out_of) + " " + totalQuesScore);
        mFinalScore.setText(mFinalScoreText);
        catQuizChildDev.setText(ctgTitle);

        btnRetake.setOnClickListener(v -> {
                startActivity(new Intent(results_quiz_childDev.this, tet_quiz_english.class));
                results_quiz_maths.this.finish();
        });
        btnShareResult.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Test Results: I Scored " + score + " out of " + totalQuesScore + " in " + ctgTitle + " Test-Series" +
                    "\n\n - via CTET Exam Prep - Quiz #app by @careerdost " +
                    "\nFREE Download now! - Available on #GooglePlay " +
                    "\nhttps://bit.ly/2Y2EeyV";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "What's your CTET Score?");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}
