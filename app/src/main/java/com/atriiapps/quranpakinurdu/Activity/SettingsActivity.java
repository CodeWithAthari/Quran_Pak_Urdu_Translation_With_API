package com.atriiapps.quranpakinurdu.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivitySettingsBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SettingsActivity activity = this;

    String[] typesMeta = {"ur_maududi", "ur_qadri", "ur_kanzuliman", "ur_ahmedali", "ur_jalandhry",
            "ur_jawadi", "ur_junagarhi", "ur_najafi", "en_maududi"};
    String[] types = {"ابوالاعلی مودودی", "طاہر القادری", "احمد رضا خان", "احمد علی", "جالندہری", "علامہ جوادی",
            "محمد جوناگڑھی", "محمد حسین نجفی", "Abul  Ala  Maududi"};
    String[] options = {"Left", "Center", "Right"};

    Constants Constants = new Constants();
    boolean doubleBackToExitPressedOnce = false;
    boolean isHideStatusBar = true;
    boolean hideStatusBar = true;
    Boolean isShowNotifications = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref_utils.PREF_INIT(activity);
        getDefaultStatusBarSettings();
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_ios_24);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.mToolbarText.setText("Settings");

        pref_utils.PREF_INIT(activity);

        functions();





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    private void getDefaultStatusBarSettings() {

        boolean isHideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);
        if (isHideStatusBar)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    private void getUpdatedStatusBarSettings() {

        if (hideStatusBar)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    private void functions() {
        translationType();

        binding.mSaveSettingsBtn.setOnClickListener(view -> {

            pref_utils.put_Pref_String(activity, "quran_version", typesMeta[binding.mQuranVersion.getSelectedItemPosition()]);
            pref_utils.put_Pref_Int(activity, "quran_version_id", binding.mQuranVersion.getSelectedItemPosition());

            pref_utils.put_Pref_String(activity, "text_style", options[binding.mTextPos.getSelectedItemPosition()]);
            pref_utils.put_Pref_Int(activity, "text_style_id", binding.mTextPos.getSelectedItemPosition());
            pref_utils.put_Pref_Boolean(activity, "hide_status_bar", isHideStatusBar);

            pref_utils.put_Pref_Boolean(activity, "show_notifications", isShowNotifications);

            utils.setToast(activity, "Saved");
            finishAffinity();
            startActivity(new Intent(activity, SplashActivity.class));



        });

        hideStatus();

        textPos();

        notifications();


    }

    private void notifications() {
        isShowNotifications  =   pref_utils.get_Pref_Boolean(activity, "show_notifications", true);

      binding.mShowNotificationsCheck.setChecked(isShowNotifications);

        binding.mShowNotifications.setOnClickListener(view -> {
            isShowNotifications = !isShowNotifications;
            binding.mShowNotificationsCheck.setChecked(isHideStatusBar);

            binding.mShowNotificationsCheck.setChecked(isShowNotifications);

        });

    }


    private void textPos() {
        int selectedOption = pref_utils.get_Pref_Int(activity, "text_style_id", 2);

        ArrayAdapter ad = new ArrayAdapter(
                this,
                R.layout.spinner_item, R.id.textView,
                options);

        ad.setDropDownViewResource(
                R.layout
                        .spinner_item);

        binding.mTextPos.setAdapter(ad);

        binding.mTextPos.setSelection(selectedOption);


    }

    private void hideStatus() {
        isHideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);
        hideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);

        binding.hideStatusCheck.setChecked(isHideStatusBar);

        binding.hideStatus.setOnClickListener(view -> {
            isHideStatusBar = !isHideStatusBar;
            binding.hideStatusCheck.setChecked(isHideStatusBar);
            hideStatusBar = !hideStatusBar;
            getUpdatedStatusBarSettings();

        });

    }

    private void translationType() {
        int selectedItem = pref_utils.get_Pref_Int(activity, "quran_version_id", 0);

        ArrayAdapter ad = new ArrayAdapter(
                this,
                R.layout.spinner_item, R.id.textView,
                types);

        ad.setDropDownViewResource(
                R.layout
                        .spinner_item);


        binding.mQuranVersion.setAdapter(ad);

        binding.mQuranVersion.setSelection(selectedItem);

    }

    @Override
    public void onBackPressed() {
        utils.setToast(activity, "Your Changes are not Saved");

        super.onBackPressed();
    }
}