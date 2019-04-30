package in.careerdost.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.careerdost.R;
import in.careerdost.database.CustomPercentFormatter;
import in.careerdost.database.Question_TET_engLan;

public class results_quiz_engLan extends AppCompatActivity {

    ArrayList<Question_TET_engLan> questionTETengLanList = new ArrayList<>();

    TextView mGrade, mFinalScore, mCorrectAns, pAssPercentage;
    TextView total_ques, catQuizEngLan, skipped_ques, correct_ans, incorrect_ans;
    Button mRetryButton, btnRetake, btnShareResult;
    ImageView resultsImage;
    PieChart pieChart;

    Question_TET_engLan currentQ;
    private int totalQuestion;

    private DecimalFormat mFormat;

    InterstitialAd interstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_quiz_layout);

        catQuizEngLan = findViewById(R.id.cat_quiz_title);
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
        catQuizEngLan.setText(ctgTitle);

        if (score >= 90 && score <= totalQuesScore) {
            resultsImage.setImageResource(R.drawable.next_level);
            mGrade.setText(getResources().getString(R.string.outstanding));
        } else if (score >= 75 && score < 89) {
            resultsImage.setImageResource(R.drawable.bingo);
            mGrade.setText(getResources().getString(R.string.good_work));
        } else if (score >= 55 && score < 74) {
            resultsImage.setImageResource(R.drawable.goodeffort);
            mGrade.setText(getResources().getString(R.string.good_effort));
        } else if (score >= 40 && score < 54) {
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

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_careerdost));
        AdRequest interAdRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(interAdRequest);

        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            finish();
                        }
                    });
                } else {
                    startActivity(new Intent(results_quiz_engLan.this, tet_quiz_english.class));
                    results_quiz_engLan.this.finish();
                }
            }
        });
        btnShareResult.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Test Results: I Scored " + score + " out of " + totalQuesScore + " in " + ctgTitle + " Test-Series" +
                    "\n\n - via CTET Exam Prep - Quiz #app by @careerdost " +
                    "\nFREE Download now! - Available on #GooglePlay " +
                    "\nhttp://bit.ly/2FCvHgJ";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "What's your CTET Score?");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}