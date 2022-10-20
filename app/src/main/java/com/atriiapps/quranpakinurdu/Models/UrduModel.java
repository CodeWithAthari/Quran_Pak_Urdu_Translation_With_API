package com.atriiapps.quranpakinurdu.Models;

public class UrduModel {


    int index,sura,aya;
    String text;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSura() {
        return sura;
    }

    public void setSura(int sura) {
        this.sura = sura;
    }

    public int getAya() {
        return aya;
    }

    public void setAya(int aya) {
        this.aya = aya;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UrduModel(int index, int sura, int aya, String text) {
        this.index = index;
        this.sura = sura;
        this.aya = aya;
        this.text = text;
    }
}
