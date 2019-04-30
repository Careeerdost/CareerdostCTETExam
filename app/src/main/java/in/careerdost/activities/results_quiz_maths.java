package in.careerdost.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.careerdost.R;
import in.careerdost.database.CustomPercentFormatter;
import in.careerdost.database.Question_TET_maths;

public class results_quiz_maths extends AppCompatActivity {

    ArrayList<Question_TET_maths> questionTETmathsList = new ArrayList<>();

    TextView mGrade, mFinalScore, mCorrectAns, pAssPercentage;
    TextView total_ques, catQuizMaths, skipped_ques, correct_ans, incorrect_ans;
    Button mRetryButton, btnRetake, btnShareResult;
    ImageView resultsImage;
    PieChart pieChart;

    Question_TET_maths currentQ;
    private int totalQuestion;

    private DecimalFormat mFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_quiz_layout);

        catQuizMaths = findViewById(R.id.cat_quiz_title);
        mGrade = findViewById(R.id.congrats);
        mFinalScore = findViewById(R.id.youScored);

        btnRetake = findViewById(R.id.btnRetake);
        btnShareResult = findViewById(R.id.btnShareResult);
        resultsImage = findViewById(R.id.resultsImage);
        pieChart = findViewById(R.id.score_piechart);

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
        catQuizMaths.setText(ctgTitle);

        if (score >= 60 && score <= totalQuesScore) {
            resultsImage.setImageResource(R.drawable.next_level);
            mGrade.setText(getResources().getString(R.string.outstanding));
        } else if (score >= 45 && score < 59) {
            resultsImage.setImageResource(R.drawable.bingo);
            mGrade.setText(getResources().getString(R.string.good_work));
        } else if (score >= 35 && score < 44) {
            resultsImage.setImageResource(R.drawable.goodeffort);
            mGrade.setText(getResources().getString(R.string.good_effort));
        } else if (score >= 25 && score < 34) {
            resultsImage.setImageResource(R.drawable.oops);
            mGrade.setText(getResources().getString(R.string.comeon_you_can));
        } else {
            resultsImage.setImageResource(R.drawable.fail);
            mGrade.setText(getResources().getString(R.string.sorry_you_failed));
        }

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(correctAnswer, "Right"));
        entries.add(new PieEntry(incorrectAnswer, "Wrong"));
        entries.add(new PieEntry(skipped, "Skipped"));
        PieDataSet set = new PieDataSet(entries, "");
        if (skipped == 0 && incorrectAnswer == 0) {
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true);
        }
        if (correctAnswer == 0 && incorrectAnswer == 0) {
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true);
        }
        if (skipped == 0 && correctAnswer == 0) {
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true);
        }
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.animateY(3000, Easing.EasingOption.EaseOutBack);
        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.getLegend().setEnabled(true);
        pieChart.getDescription().setEnabled(false);
        set.setColors(new int[]{R.color.colorGreenOne, R.color.colorRed, R.color.colorPrimaryDark}, getBaseContext());
        pieChart.setHoleRadius(60f);
        pieChart.setHoleColor((int) 60f);
        //data.setValueFormatter(new MyValueFormatter());
        data.setValueFormatter(new CustomPercentFormatter());
        set.setValueTextSize(18f);
        set.setValueTextColor(Color.WHITE);
        pieChart.invalidate(); // refresh

        btnRetake.setOnClickListener(v -> {
                startActivity(new Intent(results_quiz_maths.this, tet_quiz_english.class));
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