package com.atriiapps.quranpakinurdu.Utilities;

import android.annotation.SuppressLint;
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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


    public static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static String getCurrentDay(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }
    public static String getCurrentMonth(){
        @SuppressLint("SimpleDateFormat")
        Calendar cal=Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        return month_name;
    }






}
