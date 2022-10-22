package com.atriiapps.quranpakinurdu.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    SettingsActivity activity = this;

    String[] typesMeta = {"ur_maududi", "ur_qadri", "ur_kanzuliman", "ur_ahmedali", "ur_jalandhry",
            "ur_jawadi", "ur_junagarhi", "ur_najafi"};
    String[] types = {"ابوالاعلی مودودی", "طاہر القادری", "احمد رضا خان", "احمد علی", "جالندہری", "علامہ جوادی",
            "محمد جوناگڑھی", "محمد حسین نجفی"};
    Constants Constants = new Constants();
    boolean doubleBackToExitPressedOnce = false;
    boolean isHideStatusBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pref_utils.PREF_INIT(activity);

        functions();


    }

    private void functions() {
        translationType();

        binding.mSaveSettingsBtn.setOnClickListener(view -> {

            pref_utils.put_Pref_String(activity, "quran_version", typesMeta[binding.mQuranVersion.getSelectedItemPosition()]);
            pref_utils.put_Pref_Int(activity, "quran_version_id", binding.mQuranVersion.getSelectedItemPosition());

            utils.setToast(activity, "Saved ");
            finishAffinity();
            startActivity(new Intent(activity, MainActivity.class));
            pref_utils.put_Pref_Boolean(activity, "hide_status_bar", isHideStatusBar);


        });

        hidestatus();

    }

    private void hidestatus() {
        isHideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);

        binding.hideStatusCheck.setChecked(isHideStatusBar);

        binding.hideStatus.setOnClickListener(view -> {
            isHideStatusBar = !isHideStatusBar;
            binding.hideStatusCheck.setChecked(isHideStatusBar);

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