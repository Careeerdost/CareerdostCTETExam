package in.careerdost.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.ads.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import in.careerdost.R;
import in.careerdost.database.Ctet_cal_db;
import in.careerdost.database.Question_TET_hinLan;
import in.careerdost.database.QuizDb_TET_hinLan;

public class quiz_hinLan extends AppCompatActivity {
    private static final long COUNTDOWN_IN_MILLIS = 480000;
    ArrayList<Question_TET_hinLan> questionTEThinLanList = new ArrayList<>();
    private int score = 0;
    private int qid = 0;
    private int questionNum = 0;
    private int totalQuestion;
    int attempt_counter = 6;
    int correctAnswer = 0;

    private final String TAG = InterstitialAdActivity.class.getSimpleName();
    private InterstitialAd interstitialAd;

    private Question_TET_hinLan currentQ;
    private TextView catLevel, question, timer, overallScore, txtTotalQuestion,
            txtShowAnswer, overallCorrectAnswer;
    private Button btnConfirm;
    private RadioGroup rbGroup;
    private RadioButton rb1, rb2, rb3, rb4;

    private int skipped = 0;
    private final int incorrectAnswer = totalQuestion - (score / 5);

    int eachCorrectAnswerScore = 5;
    int eachIncorrectAnswerScore = 0;
    int correctAnswers = totalQuestion - incorrectAnswer;

    private ColorStateList txtColorDefaultRb;
    private ColorStateList txtColorDefaultCd;
    private long timeLeftInMillis;
    private boolean answered;

    private long backPressedTime;
    Ctet_cal_db ctet_cal_db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout_hindi);

        ctet_cal_db = new Ctet_cal_db(this);

        question = findViewById(R.id.question);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);

        Button btnShareQues = findViewById(R.id.btn_share_ques);
        Button btnHint = findViewById(R.id.btn_hint);
        btnConfirm = findViewById(R.id.btn_confirm);
        Button btnSkip = findViewById(R.id.btn_skip);

        txtShowAnswer = findViewById(R.id.txt_show_Answer);
        catLevel = findViewById(R.id.ques_category);
        overallCorrectAnswer = findViewById(R.id.overallCorrectAnswer);

        overallScore = findViewById(R.id.overallScore);
        timer = findViewById(R.id.timer);
        txtTotalQuestion = findViewById(R.id.txtTotalQuestion);

        txtColorDefaultRb = rb1.getTextColors();
        txtColorDefaultCd = timer.getTextColors();

        Intent intent = getIntent();
        String ctgHinLan = intent.getStringExtra(tet_quiz_hindi.Hin_Lan);
        catLevel.setText(ctgHinLan);

        overallScore.setText(getResources().getString(R.string.score_zero));
        overallCorrectAnswer.setText(getResources().getString(R.string.correct_zero));
        String quesNumText = (Integer.toString(questionNum) + getResources().getString(R.string.slash) + totalQuestion);
        txtTotalQuestion.setText(quesNumText);

        QuizDb_TET_hinLan dbHelper = new QuizDb_TET_hinLan(this);
        questionTEThinLanList = dbHelper.getQuestions(ctgHinLan);
        totalQuestion = questionTEThinLanList.size();
        Collections.shuffle(questionTEThinLanList);
        currentQ = questionTEThinLanList.get(qid);
        setNextQues();

        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDown();

        btnShareQues.setOnClickListener(v -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Can you answer this?\n" + currentQ.getQUESTION() +
                    "\n - via @careerdost " +
                    "\n\nFREE Download now! - Available on #GooglePlay " +
                    "\nhttp://bit.ly/2FCvHgJ";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Question of the Day!");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });

        btnConfirm.setOnClickListener((View v) -> {
            if (!answered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(getBaseContext(), "Please select a Option", Toast.LENGTH_SHORT).show();
                }
            } else {
                setNextQues();
            }
        });
        btnSkip.setOnClickListener((View v) -> {
            skipped = skipped + 1;
            setNextQues();
        });
        btnHint.setOnClickListener((View v) -> {
            attempt_counter--;
            if (attempt_counter > 0) {
                Toast.makeText(quiz_hinLan.this, "Max Hints = 5\nHints Left = " + attempt_counter, Toast.LENGTH_SHORT).show();
                YoYo.with(Techniques.StandUp)
                        .duration(700)
                        .playOn(findViewById(R.id.linear_show_answer));
                txtShowAnswer.setText(currentQ.getSOLVED());
            } else {
                //attempt_counter--;
                if (attempt_counter == 0) {
                    Toast.makeText(quiz_hinLan.this, "Sorry, max Hints used", Toast.LENGTH_SHORT).show();
                    btnHint.setEnabled(false);
                    btnHint.setAlpha(.5f);
                }
            }
        });

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

        AdView adView = new AdView(this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.hindi_quiz_fb_banner);
        adContainer.addView(adView);
        adView.loadAd();
    }

    private void setNextQues() {
        YoYo.with(Techniques.SlideInDown)
                .duration(500)
                .playOn(findViewById(R.id.question));
        rb1.setTextColor(txtColorDefaultRb);
        YoYo.with(Techniques.SlideInLeft)
                .duration(700)
                .playOn(findViewById(R.id.radio_button1));
        rb2.setTextColor(txtColorDefaultRb);
        YoYo.with(Techniques.SlideInRight)
                .duration(800)
                .playOn(findViewById(R.id.radio_button2));
        rb3.setTextColor(txtColorDefaultRb);
        YoYo.with(Techniques.SlideInLeft)
                .duration(900)
                .playOn(findViewById(R.id.radio_button3));
        rb4.setTextColor(txtColorDefaultRb);
        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .playOn(findViewById(R.id.radio_button4));
        rbGroup.clearCheck();
        txtShowAnswer.setText("");
        if (qid < totalQuestion) {
            currentQ = questionTEThinLanList.get(qid);
            question.setText(currentQ.getQUESTION());
            rb1.setText(currentQ.getOPTA());
            rb2.setText(currentQ.getOPTB());
            rb3.setText(currentQ.getOPTC());
            rb4.setText(currentQ.getOPTD());
            qid++;
            questionNum++;
            String quesNumText = (Integer.toString(questionNum) + getResources().getString(R.string.slash) + totalQuestion);
            txtTotalQuestion.setText(quesNumText);
            answered = false;
            btnConfirm.setText(getResources().getString(R.string.confirm));

        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        CountDownTimer countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeFormatted);
        if (timeLeftInMillis < 30000) {
            timer.setTextColor(Color.RED);
        } else {
            timer.setTextColor(txtColorDefaultCd);
        }
    }

    private void checkAnswer() {
        answered = true;

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQ.getANSWER()) {
            score += 5;
            String overallScoreText = (getResources().getString(R.string.score_count) + " " + score);
            overallScore.setText(overallScoreText);
        }
        if (answerNr == currentQ.getANSWER()) {
            correctAnswer += 1;
            String overallCorrectAnswerText = (getResources().getString(R.string.correct_count) + " " + correctAnswer);
            overallCorrectAnswer.setText(overallCorrectAnswerText);
        }

        showSolution();
    }

    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);

        switch (currentQ.getANSWER()) {
            case 1:
                YoYo.with(Techniques.RollIn)
                        .duration(300)
                        .playOn(findViewById(R.id.linear_show_answer));
                rb1.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_a_is_correct));
                break;
            case 2:
                YoYo.with(Techniques.RollIn)
                        .duration(300)
                        .playOn(findViewById(R.id.linear_show_answer));
                rb2.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_b_is_correct));
                break;
            case 3:
                YoYo.with(Techniques.RollIn)
                        .duration(300)
                        .playOn(findViewById(R.id.linear_show_answer));
                rb3.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_c_is_correct));
                break;
            case 4:
                YoYo.with(Techniques.RollIn)
                        .duration(300)
                        .playOn(findViewById(R.id.linear_show_answer));
                rb4.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_d_is_correct));
                break;
        }
        if (qid < totalQuestion) {
            btnConfirm.setText(getResources().getString(R.string.next));
        } else {
            btnConfirm.setText(getResources().getString(R.string.finish));
        }
    }

    private void finishQuiz() {
        String ctgTitle = catLevel.getText().toString();
        Intent intent = new Intent(quiz_hinLan.this, results_quiz_hinLan.class);
        Bundle b = new Bundle();
        b.putString("CTG_TITLE", ctgTitle);
        b.putInt("score", score);
        b.putInt("totalQuestion", totalQuestion);
        b.putInt("skipped", skipped);
        b.putInt("correctAnswer", correctAnswer);
        int incorrectAnswer = totalQuestion - correctAnswer;
        b.putInt("incorrectAnswer", incorrectAnswer);
        intent.putExtras(b);
        startActivity(intent);
        finish();
        //Save completed quiz to DB
        ctet_cal_db.saveDay(""+ Calendar.getInstance().getTimeInMillis());
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

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            Intent intent = new Intent(this, tet_quiz_hindi.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please press back again to finish!", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}