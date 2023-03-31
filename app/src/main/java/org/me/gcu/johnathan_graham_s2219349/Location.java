//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class Location {
    private Double latitude, longitude;
    private String name;

    private final LatLng glasgow = new LatLng(55.860916, -4.251433);

    public Location() {
        this.latitude = 0.00;
        this.longitude = 0.00;
    }

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String description) {
        if(description.isEmpty() || description.equals("Not Found")){
            this.name = "Not Found";
            return;
        }

        String[] descArray =  description.split(";");

        this.name = descArray[1].substring(10).replace(" ", "").replace(",", " ");
    }

    public double getDistanceFromGlasgowInKM(){
        if(getName().equals("Not Found")){
            return -1.00;
        }

        double theta = getLongitude() - glasgow.longitude;
        double distance = 60 * 1.1515 * (180/Math.PI) * Math.acos(
                Math.sin(getLatitude() * (Math.PI/180)) * Math.sin(glasgow.latitude * (Math.PI/180)) +
                        Math.cos(getLatitude() * (Math.PI/180)) * Math.cos(glasgow.latitude * (Math.PI/180)) * Math.cos(theta * (Math.PI/180))
        );

        DecimalFormat df = new DecimalFormat("0.00");
        return Double.parseDouble(df.format(distance * 1.609344));
    }

    public double getBearingFromGlasgow(){
        if(getName().equals("Not Found")){
            return -1.00;
        }

        double latitude1 = Math.toRadians(glasgow.latitude);
        double latitude2 = Math.toRadians(getLatitude());

        double longDiff= Math.toRadians(getLongitude() - glasgow.longitude);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    public String getDirectionFromGlasgow(){
        if(getName().equals("Not Found")){
            return "Not Found";
        }

        if (getBearingFromGlasgow() >= 315 || getBearingFromGlasgow() <= 45)
        {
            return "North of Glasgow";
        }

        if (getBearingFromGlasgow() >= 135 && getBearingFromGlasgow() <= 225)
        {
            return "South of Glasgow";
        }

        if (getBearingFromGlasgow() >= 45 && getBearingFromGlasgow() <= 135)
        {
            return "East of Glasgow";
        }

        if (getBearingFromGlasgow() >= 225 && getBearingFromGlasgow() <= 315)
        {
            return "West of Glasgow";
        }

        return "Not Found";
    }

    @Override
    public String toString(){
        return String.format("lat: %s long: %s", latitude, longitude);
    }


}
