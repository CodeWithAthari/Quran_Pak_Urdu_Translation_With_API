package com.atriiapps.quranpakinurdu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atriiapps.quranpakinurdu.Activity.SuraActivity;
import com.atriiapps.quranpakinurdu.Models.SuraMeta;
import com.atriiapps.quranpakinurdu.R;
import com.atriiapps.quranpakinurdu.Utilities.utils;
import com.atriiapps.quranpakinurdu.databinding.RvSuraBinding;

import java.util.ArrayList;

public class SuraMetaAdapter extends RecyclerView.Adapter<SuraMetaAdapter.ViewHolder> {

    ArrayList<SuraMeta> list;
    Context context;

    public void filterList(ArrayList<SuraMeta> filterlist) {
        this.list = filterlist;
        notifyDataSetChanged();
    }

    public SuraMetaAdapter(ArrayList<SuraMeta> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SuraMetaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_sura, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuraMetaAdapter.ViewHolder holder, int position) {
        SuraMeta model = list.get(position);

        RvSuraBinding binding = holder.binding;

        binding.mMetaArabic.setText(model.getName());
        binding.mMetaMeaning.setText(model.getEname());
        binding.mMetaEng.setText(model.getTname());
        binding.mMetaType.setText(model.getType());
        binding.mSuraNumber.setText(model.getId());


        binding.rvSuraMetaClicker.setOnClickListener(view -> {

            Intent intent = new Intent();
            intent.putExtra("sura_no",model.getId());
            intent.putExtra("sura_name",model.getTname());
            intent.putExtra("sura_arabic_name",model.getName());
            context.startActivity(intent.setClass(context, SuraActivity.class));

        });

        if(position >4){
            utils.setAnimWait(R.anim.slide_from_bottom_fast,holder.itemView,0,context);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RvSuraBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvSuraBinding.bind(itemView);
        }
    }


}
