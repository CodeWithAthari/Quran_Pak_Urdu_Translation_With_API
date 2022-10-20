package com.atriiapps.quranpakinurdu.Models;

public class SuraMeta {
    String id,sindex,ayas,start,name,tname,ename,type,sorder,rukus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSindex() {
        return sindex;
    }

    public void setSindex(String sindex) {
        this.sindex = sindex;
    }

    public String getAyas() {
        return ayas;
    }

    public void setAyas(String ayas) {
        this.ayas = ayas;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSorder() {
        return sorder;
    }

    public void setSorder(String sorder) {
        this.sorder = sorder;
    }

    public String getRukus() {
        return rukus;
    }

    public void setRukus(String rukus) {
        this.rukus = rukus;
    }

    public SuraMeta(String id, String sindex, String ayas, String start, String name, String tname, String ename, String type, String sorder, String rukus) {
        this.id = id;
        this.sindex = sindex;
        this.ayas = ayas;
        this.start = start;
        this.name = name;
        this.tname = tname;
        this.ename = ename;
        this.type = type;
        this.sorder = sorder;
        this.rukus = rukus;
    }
}
