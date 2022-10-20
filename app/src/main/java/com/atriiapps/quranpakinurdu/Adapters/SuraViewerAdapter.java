package com.atriiapps.quranpakinurdu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atriiapps.quranpakinurdu.Models.SuraMeta;
import com.atriiapps.quranpakinurdu.Models.SuraViewer;
import com.atriiapps.quranpakinurdu.Models.TestModel;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.VariableUtils;
import com.atriiapps.quranpakinurdu.Utilities.pref_utils;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.RvSuraBinding;
import com.atriiapps.quranpakinurdu.databinding.RvSuraViewerBinding;

import java.util.ArrayList;

public class SuraViewerAdapter extends RecyclerView.Adapter<SuraViewerAdapter.ViewHolder> {

    ArrayList<TestModel> list;
    Context context;


    public SuraViewerAdapter(ArrayList<TestModel> list, Context context) {
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
        TestModel model = list.get(position);

        RvSuraViewerBinding binding = holder.binding;


        utils.log("position", "Current Aya " + position);

        pref_utils.put_Pref_Int(context, "last_aya", position+3);
        pref_utils.put_Pref_Int(context, "last_sura", VariableUtils.CurrentSura);


        binding.mArabicText.setText(model.getArabicText() + "۔");

        binding.mUrduText.setText(model.getUrduText());

        binding.mMeta.setText(model.getArabicSura() + ":" + model.getArabicAya());
        if (position > 4) {
            utils.setAnimWait(R.anim.slide_from_bottom_fast, holder.itemView, 0, context);

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
