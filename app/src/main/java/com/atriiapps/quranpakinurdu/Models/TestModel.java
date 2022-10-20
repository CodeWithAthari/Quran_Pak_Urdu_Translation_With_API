package com.atriiapps.quranpakinurdu.Models;

public class TestModel {

    int arabicIndex,arabicSura,arabicAya;
    String arabicText;
    int urduIndex,urduSura,urduAya;
    String urduText;

    public int getArabicIndex() {
        return arabicIndex;
    }

    public void setArabicIndex(int arabicIndex) {
        this.arabicIndex = arabicIndex;
    }

    public int getArabicSura() {
        return arabicSura;
    }

    public void setArabicSura(int arabicSura) {
        this.arabicSura = arabicSura;
    }

    public int getArabicAya() {
        return arabicAya;
    }

    public void setArabicAya(int arabicAya) {
        this.arabicAya = arabicAya;
    }

    public String getArabicText() {
        return arabicText;
    }

    public void setArabicText(String arabicText) {
        this.arabicText = arabicText;
    }

    public int getUrduIndex() {
        return urduIndex;
    }

    public void setUrduIndex(int urduIndex) {
        this.urduIndex = urduIndex;
    }

    public int getUrduSura() {
        return urduSura;
    }

    public void setUrduSura(int urduSura) {
        this.urduSura = urduSura;
    }

    public int getUrduAya() {
        return urduAya;
    }

    public void setUrduAya(int urduAya) {
        this.urduAya = urduAya;
    }

    public String getUrduText() {
        return urduText;
    }

    public void setUrduText(String urduText) {
        this.urduText = urduText;
    }

    public TestModel(int arabicIndex, int arabicSura, int arabicAya, String arabicText, int urduIndex, int urduSura, int urduAya, String urduText) {
        this.arabicIndex = arabicIndex;
        this.arabicSura = arabicSura;
        this.arabicAya = arabicAya;
        this.arabicText = arabicText;
        this.urduIndex = urduIndex;
        this.urduSura = urduSura;
        this.urduAya = urduAya;
        this.urduText = urduText;
    }
}
