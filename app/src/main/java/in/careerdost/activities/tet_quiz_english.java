package in.careerdost.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import in.careerdost.NetworkCheck.CheckNetwork;
import in.careerdost.R;
import in.careerdost.database.Question_TET_childDevPed;
import in.careerdost.database.Question_TET_engLan;
import in.careerdost.database.Question_TET_envStu;
import in.careerdost.database.Question_TET_maths;

public class tet_quiz_english extends AppCompatActivity {

    public static final String Child_Dev_Ped = "childDevPed";
    public static final String Eng_Lan = "engLan";
    public static final String Env_Stu = "envStu";
    public static final String Maths = "maths";
    private Spinner spinner_childDevPed, spinner_envStu, spinner_engLan, spinner_maths;
    Button childDevPedBtn, envStuBtn, engLanBtn, mathsBtn;

    InterstitialAd interstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tet_quiz_english);

        childDevPedBtn = findViewById(R.id.childDevPedBtn);
        spinner_childDevPed = findViewById(R.id.spinner_childDevPed);
        engLanBtn = findViewById(R.id.engLanBtn);
        spinner_engLan = findViewById(R.id.spinner_engLan);
        envStuBtn = findViewById(R.id.envStuBtn);
        spinner_envStu = findViewById(R.id.spinner_envStu);
        mathsBtn = findViewById(R.id.mathsBtn);
        spinner_maths = findViewById(R.id.spinner_maths);
        Button btn_more_quiz_eng = findViewById(R.id.btn_more_quiz_eng);

        btn_more_quiz_eng.setOnClickListener((View v) -> {
            if (CheckNetwork.isOnline(tet_quiz_english.this)) //returns true if internet available
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://bit.ly/2Y2EeyV"));
                startActivity(intent);
            } else {
                Toast.makeText(tet_quiz_english.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        });

        String[] ctgChildDevPed = Question_TET_childDevPed.getAllChildDevPedLevels();
        ArrayAdapter<String> adptCtgChildDevPed = new ArrayAdapter<>(tet_quiz_english.this,
                android.R.layout.simple_spinner_item, ctgChildDevPed);
        adptCtgChildDevPed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_childDevPed.setAdapter(adptCtgChildDevPed);

        childDevPedBtn.setOnClickListener(v -> startQuizChildDevPed());

        String[] ctgEngLan = Question_TET_engLan.getAllEngLanLevels();
        ArrayAdapter<String> adptCtgEngLan = new ArrayAdapter<>(tet_quiz_english.this,
                android.R.layout.simple_spinner_item, ctgEngLan);
        adptCtgEngLan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_engLan.setAdapter(adptCtgEngLan);

        engLanBtn.setOnClickListener((View v) -> startQuizEngLan());

        String[] ctgEnvStu = Question_TET_envStu.getAllEnvStuLevels();
        ArrayAdapter<String> adptCtgEnvStu = new ArrayAdapter<>(tet_quiz_english.this,
                android.R.layout.simple_spinner_item, ctgEnvStu);
        adptCtgEnvStu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_envStu.setAdapter(adptCtgEnvStu);

        envStuBtn.setOnClickListener((View v) -> {
            startQuizEnvStu();
        });

        String[] ctgMaths = Question_TET_maths.getAllMathsLevels();
        ArrayAdapter<String> adptCtgMaths = new ArrayAdapter<>(tet_quiz_english.this,
                android.R.layout.simple_spinner_item, ctgMaths);
        adptCtgMaths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_maths.setAdapter(adptCtgMaths);

        mathsBtn.setOnClickListener((View v) -> {
            startQuizMaths();
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_careerdost));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }

    private void startQuizChildDevPed() {
        String childDevPed = spinner_childDevPed.getSelectedItem().toString();
        Intent levelOne = new Intent(tet_quiz_english.this, quiz_childDevPed.class);
        levelOne.putExtra(Child_Dev_Ped, childDevPed);
        startActivity(levelOne);
    }

    private void startQuizEngLan() {
        String engLan = spinner_engLan.getSelectedItem().toString();
        Intent levelTwo = new Intent(tet_quiz_english.this, quiz_engLan.class);
        levelTwo.putExtra(Eng_Lan, engLan);
        startActivity(levelTwo);
    }

    private void startQuizEnvStu() {
        String envStu = spinner_envStu.getSelectedItem().toString();
        Intent levelThree = new Intent(tet_quiz_english.this, quiz_envStu.class);
        levelThree.putExtra(Env_Stu, envStu);
        startActivity(levelThree);
    }

    private void startQuizMaths() {
        String maths = spinner_maths.getSelectedItem().toString();
        Intent levelFour = new Intent(tet_quiz_english.this, quiz_maths.class);
        levelFour.putExtra(Maths, maths);
        startActivity(levelFour);
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }
            });
        } else {
            Intent intent = new Intent(this, main_act.class);
            startActivity(intent);
            finish();
        }
    }
}