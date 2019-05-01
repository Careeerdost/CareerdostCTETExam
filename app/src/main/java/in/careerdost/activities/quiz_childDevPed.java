package in.careerdost.activities;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import in.careerdost.R;
import in.careerdost.database.Question_TET_childDevPed;
import in.careerdost.database.QuizDb_TET_childDevPed;
import static in.careerdost.activities.tet_quiz_english.Child_Dev_Ped;

public class quiz_childDevPed extends AppCompatActivity {
    ArrayList<Question_TET_childDevPed> questionTETchildDevPedList = new ArrayList<>();
    private int score = 0;
    private int qid = 0;
    private int questionNum = 0;
    private int totalQuestion;
    int attempt_counter = 6;
    int correctAnswer = 0;
    private Question_TET_childDevPed currentQ;
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
    private boolean answered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout_eng);

        question = findViewById(R.id.question);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        btnConfirm = findViewById(R.id.btn_confirm);
        Button btnSkip = findViewById(R.id.btn_skip);
        txtShowAnswer = findViewById(R.id.txt_show_Answer);
        catLevel = findViewById(R.id.ques_category);
        overallCorrectAnswer = findViewById(R.id.overallCorrectAnswer);
        overallScore = findViewById(R.id.overallScore);
        timer = findViewById(R.id.timer);
        txtTotalQuestion = findViewById(R.id.txtTotalQuestion);

        Intent intent = getIntent();
        String ctgChildDevPed = intent.getStringExtra(Child_Dev_Ped);
        catLevel.setText(ctgChildDevPed);

        overallScore.setText(getResources().getString(R.string.score_zero));
        overallCorrectAnswer.setText(getResources().getString(R.string.correct_zero));
        String quesNumText = (Integer.toString(questionNum) + getResources().getString(R.string.slash) + totalQuestion);
        txtTotalQuestion.setText(quesNumText);

        QuizDb_TET_childDevPed dbHelper = new QuizDb_TET_childDevPed(this);
        questionTETchildDevPedList = dbHelper.getQuestions(ctgChildDevPed);
        totalQuestion = questionTETchildDevPedList.size();
        Collections.shuffle(questionTETchildDevPedList);
        currentQ = questionTETchildDevPedList.get(qid);
        setNextQues();

        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDown();

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
    }

    private void setNextQues() {
        rbGroup.clearCheck();
        txtShowAnswer.setText("");
        if (qid < totalQuestion) {
            currentQ = questionTETchildDevPedList.get(qid);
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
                rb1.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_a_is_correct));
                break;
            case 2:
                rb2.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_b_is_correct));
                break;
            case 3:
                rb3.setTextColor(Color.WHITE);
                txtShowAnswer.setText(getResources().getString(R.string.option_c_is_correct));
                break;
            case 4:
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
            Intent intent = new Intent(quiz_childDevPed.this, results_quiz_childDevPed.class);
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
        }
}
