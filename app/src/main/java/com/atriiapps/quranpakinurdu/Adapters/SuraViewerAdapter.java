package com.atriiapps.quranpakinurdu.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atriiapps.quranpakinurdu.Activity.SuraActivity;
import com.atriiapps.quranpakinurdu.Models.SuraModel;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.ExternalConstants;
import com.atriiapps.quranpakinurdu.Utilities.VariableUtils;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.RvSuraViewerBinding;

import java.io.IOException;
import java.util.ArrayList;

public class SuraViewerAdapter extends RecyclerView.Adapter<SuraViewerAdapter.ViewHolder> {

    ArrayList<SuraModel> list;
    Context context;
    String textStyle = pref_utils.get_Pref_String(context, "text_style", "Right");

    String bismillah = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ";
    MediaPlayer mediaPlayer = new MediaPlayer();
    Handler mHandler = new Handler();

int runs = 0;
    public SuraViewerAdapter(ArrayList<SuraModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SuraViewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_sura_viewer, parent, false));
    }

    RvSuraViewerBinding binding;

    @Override
    public void onBindViewHolder(@NonNull SuraViewerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SuraModel model = list.get(position);

        binding = holder.binding;


        utils.log("position", "Current Aya " + position);
        if (position < list.size()) {
            pref_utils.put_Pref_Int(context, "last_aya", position);
            pref_utils.put_Pref_Int(context, "last_sura", VariableUtils.CurrentSura);


        }

        if (model.getArabicText().trim().startsWith(bismillah)) {

            String str = model.getArabicText().trim();

            str = str.replace(bismillah, " ");

            if (str.length() < 3) {
                binding.mArabicText.setText(bismillah + str + "۔");

            } else {
                binding.mArabicText.setText(bismillah + "۔" + "\n" + str + "۔");

            }


        } else {
            binding.mArabicText.setText(model.getArabicText() + "۔");

        }


        binding.mMeta.setText(model.getArabicSura() + ":" + model.getArabicAya());
        if (position > 4) {
            utils.setAnimWait(R.anim.slide_from_bottom_fast, holder.itemView, 0, context);

        }


        if (model.getUrduText().matches(".*[a-z].*")) {
            binding.mUrduText.setVisibility(View.GONE);
            binding.mEngText.setVisibility(View.VISIBLE);
            binding.mEngText.setText(model.getUrduText());

        } else {
            binding.mUrduText.setTextAppearance(context, R.style.urduTranslation);
            binding.mUrduText.setText(model.getUrduText());
            binding.mEngText.setVisibility(View.GONE);

        }


        switch (textStyle) {
            case "Left":
                binding.mEngText.setGravity(Gravity.START);
                binding.mArabicText.setGravity(Gravity.START);
                binding.mUrduText.setGravity(Gravity.START);
                break;
            case "Center":
                binding.mEngText.setGravity(Gravity.CENTER);
                binding.mArabicText.setGravity(Gravity.CENTER);
                binding.mUrduText.setGravity(Gravity.CENTER);
                break;

            default:
                if (!model.getUrduText().matches(".*[a-z].*") && !textStyle.equalsIgnoreCase("Right")) {
                    binding.mEngText.setGravity(Gravity.END);
                }
                binding.mArabicText.setGravity(Gravity.START);
                binding.mUrduText.setGravity(Gravity.START);

        }


        binding.mArabicText.setOnClickListener(view -> {

//            PLAY AUDIO CODE
            int index = model.getArabicIndex();

            try {
                utils.setToast(context,"Playing Recitation");
                mediaPlayer.reset();
                String url = ExternalConstants.PLAY_AYA_128_ARABIC + index + ".mp3";
                Uri uri = Uri.parse(url);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                            Toast.makeText(context, "Media Error", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            Toast.makeText(context, "Radio Server Died", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            Toast.makeText(context, "Stream is possibly offline", Toast.LENGTH_LONG).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_IO:
                            Toast.makeText(context, "IO Error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });





            } catch (Exception e) {
                utils.setToast(context, e.toString());
            }


        });
        binding.mUrduText.setOnClickListener(view -> {

//            PLAY AUDIO CODE
            int index = model.getArabicIndex();

            try {
                utils.setToast(context,"Playing Recitation");
                mediaPlayer.reset();
                String url = ExternalConstants.PLAY_AYA_64_URDU + index + ".mp3";
                Uri uri = Uri.parse(url);
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                            Toast.makeText(context, "Media Error", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            Toast.makeText(context, "Radio Server Died", Toast.LENGTH_SHORT).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            Toast.makeText(context, "Stream is possibly offline", Toast.LENGTH_LONG).show();
                            break;
                        case MediaPlayer.MEDIA_ERROR_IO:
                            Toast.makeText(context, "IO Error", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });



            } catch (Exception e) {
                utils.setToast(context, e.toString());
            }


        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvSuraViewerBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvSuraViewerBinding.bind(itemView);
        }
    }

}
