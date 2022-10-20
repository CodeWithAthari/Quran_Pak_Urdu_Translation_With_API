package com.atriiapps.quranpakinurdu.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

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
import com.atriiapps.quranpakinurdu.Models.SuraMeta;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.Constants;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.ActivityMainBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar.mToolbar);
lastAyaRead();
        initRv();


    }

    private void lastAyaRead() {
        pref_utils.PREF_INIT(activity);
        int lastAya = pref_utils.get_Pref_Int(activity,"last_aya",1)-1;
        int lastSura = pref_utils.get_Pref_Int(activity,"last_sura",1);

        Toast.makeText(activity, "Last AYA "+lastAya+"\nLast Sura "+lastSura, Toast.LENGTH_SHORT).show();




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

            new Handler().postDelayed(() -> {
                if(list.size() == 0){
                    binding.mLoading.setText("Failed Trying Again in 5 Seconds");


                    new Handler().postDelayed(() -> gettingData(), 5000);
                    utils.setToast(activity, "Error Again");
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
                    return Response.success(new String(jsonString), cacheEntry);
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
}