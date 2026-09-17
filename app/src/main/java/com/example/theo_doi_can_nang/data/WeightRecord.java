package com.example.theo_doi_can_nang.data;

public class WeightRecord {
    private int id;
    private String date;
    private float weight;
    private String note;

    public WeightRecord(int id, String date, float weight, String note)
    {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getDate(){
        return date;
    }

    public float getWeight() {
        return weight;
    }

    public String getNote() {
        return note;
    }
}
