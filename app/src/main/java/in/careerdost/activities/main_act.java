package in.careerdost.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.iid.FirebaseInstanceId;

import in.careerdost.R;
import in.careerdost.menu_activities.Aboutus;
import in.careerdost.menu_activities.Feedback;
import in.careerdost.menu_activities.Help;
import in.careerdost.menu_activities.Moreapps;
import in.careerdost.NetworkCheck.CheckNetwork;
import in.careerdost.recycler_activities.recycler_ctet_eng_txt;
import in.careerdost.recycler_activities.recycler_ctet_hindi_txt;
import in.careerdost.recycler_activities.recycler_ctet_videos;

public class main_act extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    Dialog backpressedDialog;
    ImageView closeDialog;
    Button engquizbtn, hindiquizbtn, rateus, quit;
    InterstitialAd interstitialAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        backpressedDialog = new Dialog(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button engLang = findViewById(R.id.englishBtn);
        Button hindiLang = findViewById(R.id.hindiBtn);

        ImageView videoLibIcon = findViewById(R.id.videoLibIcon);
        ImageView solvedEng = findViewById(R.id.solvedEng);
        ImageView solvedHindi = findViewById(R.id.solvedHindi);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    /*Toast.makeText(main_act.this, msg, Toast.LENGTH_SHORT).show();*/
                });
        // [END retrieve_current_token]


        engLang.setOnClickListener((View v) -> {
            onQuizActivityClick();
                /*Intent intent = new Intent(main_act.this, tet_quiz_english.class);
                startActivity(intent);*/
        });
        hindiLang.setOnClickListener((View v) -> {
            onQuizActivityClick();
                /*Intent intent = new Intent(main_act.this, tet_quiz_hindi.class);
                startActivity(intent);*/
        });

        videoLibIcon.setOnClickListener((View v) -> {
            Intent intent = new Intent(main_act.this, recycler_ctet_videos.class);
            startActivity(intent);
        });

        solvedEng.setOnClickListener((View v) -> {
            Intent intent = new Intent(main_act.this, recycler_ctet_eng_txt.class);
            startActivity(intent);
        });

        solvedHindi.setOnClickListener((View v) -> {
            Intent intent = new Intent(main_act.this, recycler_ctet_hindi_txt.class);
            startActivity(intent);
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_careerdost));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_aboutUs) {
            Intent aboutus = new Intent(this, Aboutus.class);
            startActivity(aboutus);
        } else if (id == R.id.nav_calendar) {
            Intent calendar = new Intent(this, calendar_ctet.class);
            startActivity(calendar);
        } else if (id == R.id.nav_setting) {
            Intent setting = new Intent(this, settings_ctet.class);
            startActivity(setting);
        } else if (id == R.id.nav_privacypolicy) {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://careerdost.in/privacy-policy"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_help) {
            Intent help = new Intent(this, Help.class);
            startActivity(help);
        } else if (id == R.id.nav_youtubePage) {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.youtube.com/c/careerdost?sub_confirmation=1"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_fbPage) {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/careerdost.in"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_twPage) {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://twitter.com/careerdost"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_shareApp) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "CTET Exam Prep - Quiz by #Careerdost is a FREE #ExamPreparation #app for CTET & TET  #exams " +
                    "\n - via @careerdost " +
                    "\n\nFREE Download now! - Available on #GooglePlay " +
                    "\nhttp://bit.ly/2FCvHgJ";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Improve your CTET Score!");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_moreApps) {
            Intent moreapps = new Intent(this, Moreapps.class);
            startActivity(moreapps);
        } else if (id == R.id.nav_feedback) {
            Intent feedback = new Intent(this, Feedback.class);
            startActivity(feedback);
        } else if (id == R.id.nav_update) {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://bit.ly/2FCvHgJ"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("InflateParams")
    public void onQuizActivityClick() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View infoView = layoutInflater.inflate(R.layout.info_dialog, null);
        AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(infoView);
        engquizbtn = infoView.findViewById(R.id.eng_quiz_button);
        engquizbtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(main_act.this, tet_quiz_english.class);
            startActivity(intent);
            builder.dismiss();
        });
        hindiquizbtn = infoView.findViewById(R.id.hindi_quiz_button);
        hindiquizbtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(main_act.this, tet_quiz_hindi.class);
            startActivity(intent);
            builder.dismiss();
        });
        /*bestofluck = infoView.findViewById(R.id.bestofluck);
        bestofluck.setOnClickListener((View v) -> {
            builder.dismiss();
        });*/
        builder.show();
    }

    @Override
    public void onBackPressed() {
        backpressedDialog.setContentView(R.layout.backpressed_dialog);
        closeDialog = backpressedDialog.findViewById(R.id.close_dialog);
        rateus = backpressedDialog.findViewById(R.id.rateus);
        quit = backpressedDialog.findViewById(R.id.quit);

        closeDialog.setOnClickListener((View v) -> {
            backpressedDialog.dismiss();
        });
        rateus.setOnClickListener((View v) -> {
            if (CheckNetwork.isOnline(main_act.this)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://bit.ly/2FCvHgJ"));
                startActivity(intent);
            } else {
                Toast.makeText(main_act.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        });
        quit.setOnClickListener((View v) -> {
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
                finish();
            }
        });
        backpressedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        backpressedDialog.show();
    }
}