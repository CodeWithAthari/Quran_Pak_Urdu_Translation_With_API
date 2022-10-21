package com.atriiapps.quranpakinurdu.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.atriiapps.quranpakinurdu.Adapters.SuraViewerAdapter;
import com.atriiapps.quranpakinurdu.Models.ArabicModel;
import com.atriiapps.quranpakinurdu.Models.SuraModel;
import com.atriiapps.quranpakinurdu.Models.UrduModel;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.VariableUtils;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivitySurahBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class SuraActivity extends AppCompatActivity {
    ActivitySurahBinding binding;
    SuraActivity activity = this;

    SuraViewerAdapter adapter;
    ArrayList<SuraModel> list = new ArrayList<>();

    String sura_no, sura_name, sura_arabic_name;
    ArrayList<ArabicModel> arabicList = new ArrayList<>();
    ArrayList<UrduModel> urduList = new ArrayList<>();

    int lastAya, lastSura, aya_no;
    Constants Constants = new Constants();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurahBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.mToolbar);
        pref_utils.PREF_INIT(activity);
        Constants.updateConstants();
        sura_no = getIntent().getStringExtra("sura_no");
        aya_no = getIntent().getIntExtra("aya_no", 1);
        sura_name = getIntent().getStringExtra("sura_name");
        sura_arabic_name = getIntent().getStringExtra("sura_arabic_name");


        if (sura_name == null) {
            getSuraName();
            utils.setToast(activity, "Searching Verse " + aya_no);
        }

        pref_utils.put_Pref_String(activity, "last_sura_arabic_name", sura_arabic_name);
        pref_utils.put_Pref_String(activity, "last_sura_eng_name", sura_name);

        VariableUtils.CurrentSura = Integer.parseInt(sura_no);

        initRv();


    }

    private void getSuraName() {
        StringRequest request = new StringRequest(Request.Method.GET, Constants.GET_SURA_META_ONE + sura_no,

                response -> {


                    try {
                        JSONObject mainObj = new JSONObject(response);
                        String status = mainObj.getString("status");
                        if (!status.equals("Success")) {
                            utils.setToast(activity, "Message: " + mainObj.getString("message"));
                            return;
                        }

                        JSONArray array = mainObj.getJSONArray("Sura");

                        JSONObject suraObj = array.getJSONObject(0);

                        sura_name = suraObj.getString("tname");
                        sura_arabic_name = suraObj.getString("name");


                        VariableUtils.CurrentSura = Integer.parseInt(sura_no);
                        pref_utils.put_Pref_String(activity, "last_sura_arabic_name", sura_arabic_name);
                        pref_utils.put_Pref_String(activity, "last_sura_eng_name", sura_name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                },
                error -> {
                });

        RequestQueue queue = Volley.newRequestQueue(activity);

        queue.add(request);


    }


    private void initRv() {
        adapter = new SuraViewerAdapter(list, activity);
        new FastScrollerBuilder(binding.mSuraViewerRV).build();
        binding.mSuraViewerRV.setAdapter(adapter);


        fetchingSura();


    }

    private void lastRead() {


        lastAya = pref_utils.get_Pref_Int(activity, "last_aya", 1);
        lastSura = pref_utils.get_Pref_Int(activity, "last_sura", 1);

        if (aya_no > 3) {
            lastAya = aya_no;
        }


        if (lastAya > 3 && lastSura == VariableUtils.CurrentSura) {
            new Handler().postDelayed(() -> {
                if (lastAya > list.size()) {
                    utils.setToast(activity, "Unknown Verse");
                }
                binding.mSuraViewerRV.scrollToPosition(lastAya - 1);


            }, 500);
        }
//
    }


    private void getCurrentQuranVersion() {
        Constants.QURAN_TRANSLATION_VERSION = pref_utils.get_Pref_String(activity, "quran_version", Constants.DEFAULT_QURAN_TRANSLATION_VERSION_SHARED_PREF);
//        utils.setToast(activity, Constants.QURAN_TRANSLATION_VERSION);
    }

    @SuppressLint("SetTextI18n")
    private void fetchingSura() {
        getCurrentQuranVersion();
        binding.mLoading.setText("Loading...");

        utils.setAnim(R.anim.fade, binding.mLoading, activity);

        @SuppressLint("NotifyDataSetChanged")
        StringRequest request = new StringRequest(Request.Method.GET, Constants.WEBSITE_BASE_URL + "?v=" + Constants.QURAN_TRANSLATION_VERSION + "&q=get_sura&sura_no=" + sura_no, response -> {

            try {
//                utils.setToast(activity, Constants.WEBSITE_BASE_URL +"?v="+Constants.QURAN_TRANSLATION_VERSION+"&q=get_sura&sura_no=" + sura_no);
                JSONObject mainObj = new JSONObject(response);
                list.clear();
                String status = mainObj.getString("status");
                if (!status.equals("Success")) {
                    utils.setToast(activity, "Message: " + mainObj.getString("message"));
                    return;
                }

                JSONObject suraObj = mainObj.getJSONObject("sura");

                JSONArray arabicArray = suraObj.getJSONArray("arabic");

                JSONArray urduArray = suraObj.getJSONArray("urdu");
                utils.log("resp", urduArray.toString());

//            Adding Arabic Array
                for (int i = 0; i < arabicArray.length(); i++) {
                    JSONObject object = arabicArray.getJSONObject(i);
                    ArabicModel model = new Gson().fromJson(object.toString(), ArabicModel.class);

                    arabicList.add(model);


                }

                for (int i = 0; i < urduArray.length(); i++) {
                    JSONObject object = urduArray.getJSONObject(i);
                    UrduModel model = new Gson().fromJson(object.toString(), UrduModel.class);
                    urduList.add(model);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < arabicList.size(); i++) {
                SuraModel model = new SuraModel(arabicList.get(i).getIndex(), arabicList.get(i).getSura(), arabicList.get(i).getAya(), arabicList.get(i).getText(), urduList.get(i).getIndex(), urduList.get(i).getSura(), urduList.get(i).getAya(), urduList.get(i).getText());
                list.add(model);
            }

            utils.setAnim(android.R.anim.fade_out, binding.mLoading, activity);
            binding.mLoading.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
            lastRead();

        }, error -> new Handler().postDelayed(() -> {
            if (list.size() == 0) {
                binding.mLoading.setText("Failed Trying Again in 5 Seconds");


                new Handler().postDelayed(this::fetchingSura, 5000);
                utils.setToast(activity, "Error Again");
            }
        }, 2000)) {
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
                    headerValue = Objects.requireNonNull(response.headers).get("Date");
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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_navigate) {
            findAya();
        }


        return super.onOptionsItemSelected(item);
    }

    private void findAya() {
        if (list.size() == 0) {
            utils.setToast(activity, "Please Wait...");
            return;
        }


        MaterialAlertDialogBuilder dialogBuilder;

        dialogBuilder = new MaterialAlertDialogBuilder(activity);
        dialogBuilder.setTitle("Find Aya in " + sura_name);

        EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Enter Aya No, " + list.size());
        if (lastAya > 3) {
            editText.setText(String.valueOf(lastAya));

        }

        dialogBuilder.setView(editText);
        dialogBuilder.setPositiveButton("Find Aya", (dialogInterface, j) -> {
            int sura = Integer.parseInt(editText.getText().toString());
            if (sura > 0 & sura <= list.size()) {
                binding.mSuraViewerRV.scrollToPosition(sura - 1);
            } else {
                utils.setToast(activity, "Unknown Aya No");
            }


        });

        dialogBuilder.show();


    }
}