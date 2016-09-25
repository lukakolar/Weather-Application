package com.lukakolar.weatherapplication.Entity;


// Class that represents a city in RecyclerView
public class CityWeatherObject {

    public String name;
    public Integer id;
    public String temperature;
    public String humidity;
    public String description;

    public CityWeatherObject(Integer id, String name, String temperature, String humidity, String description){
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.humidity = humidity;
        this.description = description;
    }

    // For debugging
    @Override
    public String toString() {
        return id.toString() + " " + name + " " + temperature + " " + humidity + " " + description;
    }

}
