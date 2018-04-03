package com.example.vinay.appointmentmanager;

/**
 * Created by vinay on 26/03/2018.
 */

public class Synonym {

    private String category;
    private String synonyms;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public String toString() {
        return "Synonym [Category=" + category + ", Synonyms = " + synonyms + "]";
    }
}