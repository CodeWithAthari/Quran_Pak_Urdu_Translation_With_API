package com.atriiapps.quranpakinurdu.Models;

import java.util.ArrayList;

public class SuraViewer {

    ArrayList<ArabicModel> arabicList;
    ArrayList<UrduModel> urduList;

    public ArrayList<ArabicModel> getArabicList() {
        return arabicList;
    }

    public void setArabicList(ArrayList<ArabicModel> arabicList) {
        this.arabicList = arabicList;
    }

    public ArrayList<UrduModel> getUrduList() {
        return urduList;
    }

    public void setUrduList(ArrayList<UrduModel> urduList) {
        this.urduList = urduList;
    }

    public SuraViewer(ArrayList<ArabicModel> arabicList, ArrayList<UrduModel> urduList) {
        this.arabicList = arabicList;
        this.urduList = urduList;
    }
}
