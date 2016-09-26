package com.lukakolar.weatherapplication.Entity;


import android.os.Parcel;
import android.os.Parcelable;

// Class that represents a city in RecyclerView
public class CityWeatherObject implements Parcelable{

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

    public CityWeatherObject(CityWeatherObject other) {
        this.id = other.id;
        this.name = other.name;
        this.temperature = other.temperature;
        this.humidity = other.humidity;
        this.description = other.description;
    }

    // For debugging
    @Override
    public String toString() {
        return id.toString() + " " + name + " " + temperature + " " + humidity + " " + description;
    }

    // Parcelling part
    public CityWeatherObject(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.id = Integer.valueOf(data[0]);
        this.name = data[1];
        this.temperature = data[2];
        this.humidity = data[3];
        this.description = data[4];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(this.id),
                this.name,
                this.temperature,
                this.humidity,
                this.description});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CityWeatherObject createFromParcel(Parcel in) {
            return new CityWeatherObject(in);
        }

        public CityWeatherObject[] newArray(int size) {
            return new CityWeatherObject[size];
        }
    };

}
