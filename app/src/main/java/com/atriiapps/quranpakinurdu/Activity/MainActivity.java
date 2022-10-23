package com.atriiapps.quranpakinurdu.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atriiapps.quranpakinurdu.Adapters.SuraMetaAdapter;
import com.atriiapps.quranpakinurdu.BuildConfig;
import com.atriiapps.quranpakinurdu.Models.SuraMeta;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Services.NotificationReciever;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.ExternalConstants;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    MainActivity activity = this;

    JSONArray array;

    ArrayList<SuraMeta> list = new ArrayList<>();
    SuraMetaAdapter adapter;

    Constants Constants = new Constants();

    String currentDate = utils.getCurrentDate();
    String currentDay = utils.getCurrentDay();
    String currentMonth = utils.getCurrentMonth();

    Boolean isEditTextFocus = false;
    Boolean isFromBroadcast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        pref_utils.PREF_INIT(activity);
        getStatusBarSettings();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.mToolbar);
        pref_utils.PREF_INIT(activity);
        getCurrentQuranVersion();
        lastAyaRead();
        initRv();
        binding.textField.setClickable(false);
        getHijriDate();
        checkForUpdate();


        if (!isFromBroadcast) {
            startServices();
        }

        utils.setAnimWait(R.anim.fade_in, binding.textField, 0, activity);

        binding.textFF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!binding.textFF.getText().toString().matches("")) {

                    filter(binding.textFF.getText().toString());


                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.mNestedScroll.setSmoothScrollingEnabled(true);

        binding.textFF.setOnFocusChangeListener((view, isFocus) -> {
            isEditTextFocus = isFocus;
            int scrollY = binding.mNestedScroll.getScrollY();
            if (isFocus) {
                if (scrollY < 500) {

                    binding.mNestedScroll.smoothScrollTo(binding.mNestedScroll.getScrollX(), binding.mNestedScroll.getScrollY() + 1000);
                }
            }

        });


    }



    private void startServices() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(activity, NotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 10, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ExternalConstants.NotificationDelay, pendingIntent);


    }

    private void checkForUpdate() {
        binding.updateContainer.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.CHECK_4_UPDATE,
                response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");

                        if (!status.equalsIgnoreCase("Success")) {
                            utils.setToast(activity, object.getString("message"));
                            return;
                        }

                        String myVersion = BuildConfig.VERSION_NAME;
                        String apiVersion = object.getString("currentversion");
                        utils.log("version", myVersion + "\n" + apiVersion);

                        if (!apiVersion.matches(myVersion)) {
                            binding.updateContainer.setVisibility(View.VISIBLE);
                            utils.setAnimWait(R.anim.fade_in, binding.updateContainer, 0, activity);
                            binding.mUpdateApp.materialButton.setOnClickListener(view -> {
                                Intent ii = null;
                                try {
                                    ii = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("link")));
                                    startActivity(ii);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {

            utils.log("error", error.toString());

            new Handler().postDelayed(this::checkForUpdate, 10000);

        });

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);


    }

    private void getHijriDate() {

        updateDateInUI(false);


        String url = ExternalConstants.GREGORIAN_TO_HIJRI + currentDate;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONObject object = new JSONObject(response);

                        int statusCode = object.getInt("code");

                        if (statusCode != 200) {
                            return;
                        }

                        JSONObject dataObj = object.getJSONObject("data");
                        JSONObject hijriObj = dataObj.getJSONObject("hijri");
                        currentDate = hijriObj.getString("date");
                        currentDay = hijriObj.getJSONObject("weekday").getString("en");
                        currentMonth = hijriObj.getJSONObject("month").getString("en");

                        updateDateInUI(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {

            utils.log("error", error.toString());

            new Handler().postDelayed(this::getHijriDate, 10000);

        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }


            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        ;

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);


    }

    void updateDateInUI(Boolean isAnim) {

        if (isAnim) {
            utils.setAnimWait(R.anim.slide_from_bottom_date, binding.topHeader.mTodayDay, 0, activity);
            utils.setAnimWait(R.anim.slide_from_bottom_date, binding.topHeader.mMonth, 200, activity);
            utils.setAnimWait(R.anim.slide_from_bottom_date, binding.topHeader.mTodayDate, 300, activity);

        }


        binding.topHeader.mTodayDate.setText(currentDate);
        binding.topHeader.mTodayDay.setText(currentDay);
        binding.topHeader.mMonth.setText(currentMonth);
    }

    private void getCurrentQuranVersion() {
        Constants.QURAN_TRANSLATION_VERSION = pref_utils.get_Pref_String(activity, "quran_version", Constants.DEFAULT_QURAN_TRANSLATION_VERSION_SHARED_PREF);
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<SuraMeta> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (SuraMeta item : list) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTname().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentQuranVersion();
        utils.setAnimWait(R.anim.fade_in, binding.mSuraRv, 0, activity);
        utils.setAnimWait(R.anim.fade_in, binding.mTopHeaderHolder, 0, activity);
        utils.setAnimWait(R.anim.fade_in, binding.mLastReadHolder, 0, activity);
        utils.setAnimWait(R.anim.fade_in, binding.textField, 0, activity);
        binding.textFF.setText("");
        binding.textFF.clearFocus();
        adapter.filterList(list);

        lastAyaRead();
    }

    private void lastAyaRead() {

        int lastAya = pref_utils.get_Pref_Int(activity, "last_aya", -2);
        String last_sura_arabic_name = pref_utils.get_Pref_String(activity, "last_sura_arabic_name", "failed");
        String last_sura_eng_name = pref_utils.get_Pref_String(activity, "last_sura_eng_name", "failed");
        int lastSura = pref_utils.get_Pref_Int(activity, "last_sura", 1);


        if (lastAya > 0) {
            binding.mLastReadHolder.setVisibility(View.VISIBLE);
            utils.setAnimWait(R.anim.fade_in, binding.mLastReadHolder, 0, activity);

            binding.mLastRead.mArabicNameLastRead.setText(last_sura_arabic_name);

            binding.mLastRead.mLastReadPointer.setText(lastSura + ":" + lastAya);

            binding.mLastRead.mCardConstraintLastRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("sura_no", String.valueOf(lastSura));
                    intent.putExtra("sura_name", last_sura_eng_name);
                    intent.putExtra("sura_arabic_name", last_sura_arabic_name);
                    startActivity(intent.setClass(activity, SuraActivity.class));

                }
            });

            return;
        }

        binding.mLastReadHolder.setVisibility(View.GONE);


    }

    private void initRv() {
        adapter = new SuraMetaAdapter(list, activity);
        new FastScrollerBuilder(binding.mSuraRv).build();
        binding.mSuraRv.setAdapter(adapter);

        gettingData();


    }

    private void gettingData() {
        binding.mLoading.setText("Loading...");

        utils.setAnim(R.anim.fade, binding.mLoading, activity);

        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_SURA_META, response -> {
            list.clear();
            try {
                JSONObject mainObj = new JSONObject(response);

                String status = mainObj.getString("status");
                if (!status.equals("Success")) {
                    utils.setToast(activity, "Message: " + mainObj.getString("message"));
                    return;
                }

                array = mainObj.getJSONArray("Sura");

                for (int j = 0; j < array.length(); j++) {
                    JSONObject object = array.getJSONObject(j);
                    SuraMeta model = new Gson().fromJson(object.toString(), SuraMeta.class);

                    list.add(model);

                }
                adapter.notifyDataSetChanged();
                utils.setAnim(android.R.anim.fade_out, binding.mLoading, activity);
                binding.mLoading.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {
            utils.log("err", error.toString());
            new Handler().postDelayed(() -> {
                if (list.size() == 0) {
                    binding.mLoading.setText("Failed Trying Again in 5 Seconds");


                    new Handler().postDelayed(() -> gettingData(), 5000);
                }
            }, 2000);


        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }


            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };


        RequestQueue queue = Volley.newRequestQueue(activity);

        queue.add(request);

        if (list.size() > 0) {
            binding.textField.setClickable(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_navigate:

                findAyainQuran();

                break;

            case R.id.main_menu_settings:

                startActivity(new Intent(activity, SettingsActivity.class));

                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void findAyainQuran() {


        MaterialAlertDialogBuilder dialogBuilder;

        dialogBuilder = new MaterialAlertDialogBuilder(activity);
        dialogBuilder.setTitle("Goto Specific Aya in Quran Pak");


        EditText editText = new EditText(activity);
        editText.setHint("Chapter:Verse e.g(2:205)");


        dialogBuilder.setView(editText);
        dialogBuilder.setPositiveButton("Find Aya", (dialogInterface, j) -> {

            String text = editText.getText().toString().trim();

            if (text.matches("") || !text.contains(":") || !isStartWithNumber(text) || !isEndWithNumber(text)) {
                utils.setToast(activity, "Enter Valid...");
                utils.log("edittext", text);
                return;
            }

            String sura = text.split(":")[0].replace(":", "");
            String aya = text.split(":")[1].replace(":", "");
            utils.log("find", sura + "\n" + aya);

            if (aya.matches("")) {
                aya = "1";
            }
            Intent intent = new Intent();
            intent.putExtra("sura_no", sura);
            intent.putExtra("aya_no", Integer.parseInt(aya));
            startActivity(intent.setClass(activity, SuraActivity.class));


        });

        dialogBuilder.show();


    }

    private boolean isEndWithNumber(String text) {

        if (text.endsWith("1")) {
            return true;
        } else if (text.endsWith("2")) {
            return true;
        } else if (text.endsWith("3")) {
            return true;
        } else if (text.endsWith("4")) {
            return true;
        } else if (text.endsWith("5")) {
            return true;
        } else if (text.endsWith("6")) {
            return true;
        } else if (text.endsWith("7")) {
            return true;
        } else if (text.endsWith("8")) {
            return true;
        } else if (text.endsWith("9")) {
            return true;
        } else if (text.endsWith("0")) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isStartWithNumber(String text) {

        if (text.startsWith("1")) {
            return true;
        } else if (text.startsWith("2")) {
            return true;
        } else if (text.startsWith("3")) {
            return true;
        } else if (text.startsWith("4")) {
            return true;
        } else if (text.startsWith("5")) {
            return true;
        } else if (text.startsWith("6")) {
            return true;
        } else if (text.startsWith("7")) {
            return true;
        } else if (text.startsWith("8")) {
            return true;
        } else if (text.startsWith("9")) {
            return true;
        } else {
            return false;
        }


    }

    private void getStatusBarSettings() {

        boolean isHideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);
        if (isHideStatusBar)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    @Override
    public void onBackPressed() {
        if (isEditTextFocus) {
            binding.textFF.clearFocus();
        } else {
            finish();
        }
    }
}