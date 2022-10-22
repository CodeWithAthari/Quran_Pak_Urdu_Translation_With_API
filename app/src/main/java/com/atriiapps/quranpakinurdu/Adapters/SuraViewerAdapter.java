package com.atriiapps.quranpakinurdu.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.atriiapps.quranpakinurdu.Models.SuraModel;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.VariableUtils;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.RvSuraViewerBinding;

import java.util.ArrayList;

public class SuraViewerAdapter extends RecyclerView.Adapter<SuraViewerAdapter.ViewHolder> {

    ArrayList<SuraModel> list;
    Context context;

    String bismillah = "بِسْمِ اللَّهِ الرَّحْمَـٰنِ الرَّحِيمِ";

    public SuraViewerAdapter(ArrayList<SuraModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SuraViewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_sura_viewer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuraViewerAdapter.ViewHolder holder, int position) {
        SuraModel model = list.get(position);

        RvSuraViewerBinding binding = holder.binding;


        utils.log("position", "Current Aya " + position);
        if (position < list.size()) {
            pref_utils.put_Pref_Int(context, "last_aya", position);
            pref_utils.put_Pref_Int(context, "last_sura", VariableUtils.CurrentSura);


        }

        if(model.getArabicText().trim().startsWith(bismillah)){

            String str = model.getArabicText().trim();

            str = str.replace(bismillah," ");

          if(str.length() < 3){
                binding.mArabicText.setText(bismillah + str + "۔");

            }
            else{
                binding.mArabicText.setText(bismillah +"۔" +"\n"+ str + "۔");

            }




        }
        else{
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

        }
        else{
            binding.mUrduText.setTextAppearance(context, R.style.urduTranslation);
            binding.mUrduText.setText(model.getUrduText());
            binding.mEngText.setVisibility(View.GONE);

        }


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
