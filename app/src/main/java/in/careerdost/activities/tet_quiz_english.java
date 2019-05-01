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

import in.careerdost.R;
import in.careerdost.database.Question_TET_childDevPed;
import in.careerdost.database.Question_TET_engLan;

public class tet_quiz_english extends AppCompatActivity {

    public static final String Child_Dev_Ped = "childDevPed";
    public static final String Eng_Lan = "engLan";
    private Spinner spinner_childDevPed, spinner_engLan;
    Button childDevPedBtn, engLanBtn;

    InterstitialAd interstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tet_quiz_english);

        childDevPedBtn = findViewById(R.id.childDevPedBtn);
        spinner_childDevPed = findViewById(R.id.spinner_childDevPed);
        engLanBtn = findViewById(R.id.engLanBtn);
        spinner_engLan = findViewById(R.id.spinner_engLan);
        Button btn_more_quiz_eng = findViewById(R.id.btn_more_quiz_eng);

        btn_more_quiz_eng.setOnClickListener((View v) -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://bit.ly/2Y2EeyV"));
                startActivity(intent);
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
