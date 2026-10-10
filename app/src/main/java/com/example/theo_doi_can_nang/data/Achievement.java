package com.example.theo_doi_can_nang.data;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private String modelFileName;
    private boolean isUnlocked;
    private String unlockedDate;

    public Achievement(String id, String title, String description, String modelFileName, boolean isUnlocked, String unlockedDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.modelFileName = modelFileName;
        this.isUnlocked = isUnlocked;
        this.unlockedDate = unlockedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getModelFileName() {
        return modelFileName;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getUnlockedDate() {
        return unlockedDate;
    }

    public void setUnlockedDate(String unlockedDate) {
        this.unlockedDate = unlockedDate;
    }
}
