package com.atriiapps.quranpakinurdu.Utilities;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textview.MaterialTextView;

public class utils {


    public static void replaceFragment (Context context , Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = ((AppCompatActivity)context). getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
//            ft.replace(R.id.fragmentContainerView, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }



    public static void setToast(Context context,String msg){
        if(msg!=null){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Message is Null", Toast.LENGTH_SHORT).show();
        }



    }
    public static void log(String tag, String msg){

        Log.d(tag,msg);


    }


    public static void setAnim(int id, MaterialTextView view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context, id));
    }
    public static void setAnimWait(int id, View view, int Delay, Context context) {
        try {
            view.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(() -> {
                view.setVisibility(View.VISIBLE);
                view.startAnimation(AnimationUtils.loadAnimation(context, id));
            }, Delay);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "We Have Error please contact developer asap...", Toast.LENGTH_SHORT).show();

        }

    }







}
