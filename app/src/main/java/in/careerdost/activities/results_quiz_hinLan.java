package in.careerdost.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.Log;
import com.facebook.ads.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.careerdost.R;
import in.careerdost.database.CustomPercentFormatter;
import in.careerdost.database.Question_TET_hinLan;

public class results_quiz_hinLan extends AppCompatActivity {

    ArrayList<Question_TET_hinLan> questionTEThinLanList = new ArrayList<>();

    TextView mGrade, mFinalScore, mCorrectAns, pAssPercentage;
    TextView total_ques, catQuizHinLang, skipped_ques, correct_ans, incorrect_ans;
    Button mRetryButton, btnRetake, btnShareResult;
    ImageView resultsImage;
    PieChart pieChart;

    Question_TET_hinLan currentQ;
    private int totalQuestion;

    private DecimalFormat mFormat;

    private final String TAG = InterstitialAdActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_quiz_layout);

        catQuizHinLang = findViewById(R.id.cat_quiz_title);
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
        catQuizHinLang.setText(ctgTitle);

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

        interstitialAd = new InterstitialAd(this, getString(R.string.fb_interstitial));
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });
        interstitialAd.loadAd();
        showAdWithDelay();

        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(results_quiz_hinLan.this, tet_quiz_hindi.class));
                results_quiz_hinLan.this.finish();
            }
        });
        btnShareResult.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "टेस्ट रिजल्ट्स: मैंने " + totalQuesScore + " में से " + score + " स्कोर किया " + ctgTitle + " टेस्ट-सीरीज में।" +
                    "\n\n - CTET परीक्षा प्रेप - क्विज़ #app @careerdost के सौजन्य से! " +
                    "\nफ्री डाउनलोड कीजिए! - #गूगलप्ले पर उपलब्ध! " +
                    "\nhttp://bit.ly/2FCvHgJ";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "आपका CTET स्कोर क्या है?");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }

    private void showAdWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            if (interstitialAd.isAdInvalidated()) {
                return;
            }
            interstitialAd.show();
        }, 1000 * 60 * 5);
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}