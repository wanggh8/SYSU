package com.example.wang.myapplication;
import java.io.Serializable;

public class Collection implements Serializable {

    private String name;
    private String Cycle;
    private String category;
    private String nutrition;
    private String background_colour;

    public Collection(String name, String Cycle, String category, String nutrition, String background_colour) {
        this.name = name;
        this.Cycle = Cycle;
        this.category = category;
        this.nutrition = nutrition;
        this.background_colour = background_colour;
    }


    public String getName() {
        return name;
    }

    public String getCycle() {
        return Cycle;
    }

    public void setCycle(String Cycle) {
        this.Cycle = Cycle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNutrition() {
        return nutrition;
    }

    public void setNutrition(String nutrition) {
        this.nutrition = nutrition;
    }

    public String getBackground() {
        return background_colour;
    }

    public void setBackground(String background_colour) {
        this.background_colour =background_colour;
    }

}
