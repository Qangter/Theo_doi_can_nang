package com.example.theo_doi_can_nang.data;

public class WeightRecord {
    private int id;
    private String date;
    private float weight;
    private String note;
    private String imagePath;

    public WeightRecord(int id, String date, float weight, String note) {
        this(id, date, weight, note, null);
    }

    public WeightRecord(int id, String date, float weight, String note, String imagePath) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.note = note;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public float getWeight() {
        return weight;
    }

    public String getNote() {
        return note;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
