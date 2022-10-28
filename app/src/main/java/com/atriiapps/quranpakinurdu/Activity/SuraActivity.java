package com.atriiapps.quranpakinurdu.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.atriiapps.quranpakinurdu.Utilities.ExternalConstants;
import com.atriiapps.quranpakinurdu.Utilities.VariableUtils;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivitySurahBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class SuraActivity extends AppCompatActivity {
   public static ActivitySurahBinding binding;
    SuraActivity activity = this;

    SuraViewerAdapter adapter;
    ArrayList<SuraModel> list = new ArrayList<>();

    String sura_no, sura_name, sura_arabic_name;
    ArrayList<ArabicModel> arabicList = new ArrayList<>();
    ArrayList<UrduModel> urduList = new ArrayList<>();

    int lastAya, lastSura, aya_no;
    Constants Constants = new Constants();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurahBinding.inflate(getLayoutInflater());
        pref_utils.PREF_INIT(activity);
        getStatusBarSettings();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.mToolbar);
        Constants.updateConstants();
        sura_no = getIntent().getStringExtra("sura_no");
        aya_no = getIntent().getIntExtra("aya_no", 1);
        sura_name = getIntent().getStringExtra("sura_name");
        sura_arabic_name = getIntent().getStringExtra("sura_arabic_name");
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_ios_24);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(upArrow);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.mToolbarText.setText(sura_name);
        if (sura_name == null) {
            getSuraName();
            utils.setToast(activity, "Searching Verse " + aya_no);
        }
        playingSura();

        pref_utils.put_Pref_String(activity, "last_sura_arabic_name", sura_arabic_name);
        pref_utils.put_Pref_String(activity, "last_sura_eng_name", sura_name);

        VariableUtils.CurrentSura = Integer.parseInt(sura_no);

        initRv();


    }

    private void playingSura() {
//        binding.mPlayerholder.setVisibility(View.GONE);
        binding.mPlayer.mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(activity);
                dialog.setCancelable(false);
                dialog.setMessage("Loading Sura...");
                dialog.show();
                utils.setToast(activity, "Playing Wait");
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.reset();
//                String url = ExternalConstants.PLAY_SURA_128_ARABIC + sura_no + ".mp3";
                String url = "android.resources://"+getPackageName()+"/res/raw/audio.mp3";
                utils.log("audio",url);
                Uri uri = Uri.parse(url);
                try {
                    mediaPlayer.setDataSource(activity, uri);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                  utils.log("error",e.toString());

                }
                mediaPlayer.setOnPreparedListener(mp -> {
                    Toast.makeText(activity, "Prepared", Toast.LENGTH_SHORT).show();

                    mediaPlayer.start();
                    dialog.dismiss();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    dialog.dismiss();
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                            Toast.makeText(activity, "Media Error", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            Toast.makeText(activity, "Radio Server Died", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            Toast.makeText(activity, "Stream is possibly offline", Toast.LENGTH_LONG).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_IO:
                            Toast.makeText(activity, "IO Error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });


                mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        Toast.makeText(SuraActivity.this, " "+i, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void getStatusBarSettings() {

        boolean isHideStatusBar = pref_utils.get_Pref_Boolean(activity, "hide_status_bar", true);
        if (isHideStatusBar)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    private void getSuraName() {
        binding.toolbar.mToolbarText.setText(getString(R.string.app_name));

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
                        binding.toolbar.mToolbarText.setText(sura_name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                },
                error -> {
                })

        {
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
            scrollToFindingAya();
            return;
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

    private void scrollToFindingAya() {
        if (aya_no > list.size()) {
            utils.setToast(activity, "Unknown Verse");
        }
        binding.mSuraViewerRV.scrollToPosition(aya_no - 1);

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
            onScroll();

        }, error -> new Handler().postDelayed(() -> {
            if (list.size() == 0) {
                binding.mLoading.setText("Failed Trying Again in 5 Seconds");


                new Handler().postDelayed(this::fetchingSura, 5000);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void onScroll() {

        final int[] state = new int[1];

        binding.mSuraViewerRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    binding.toolbarLinearHolder.setVisibility(View.GONE);
                } else if (dy < -10) {
                    binding.toolbarLinearHolder.setVisibility(View.VISIBLE);

                }


            }
        });

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
        if (aya_no > 3) {
            editText.setText(String.valueOf(aya_no));

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

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }
}

