//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class Earthquake
{
    private String title, description, link, category;
    private DateTime publishDate;
    private Location location;

    public Earthquake(){
        this.title = "Not Found";
        this.description = "Not Found";
        this.link = "Not Found";
        this.publishDate = new DateTime();
        this.category = "Not Found";
        this.location = new Location();
    }

    public Earthquake(String title, String description, String link, String publishDate, String category, Location location) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.publishDate = new DateTime(publishDate);
        this.category = category;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public DateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = new DateTime(publishDate);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getMagnitude() {
        if(description.equals("Not Found")){
            return 0.00;
        }
        String[] descArray =  description.split(" ");
        return Double.valueOf(descArray[descArray.length -1]);
    }

    public Double getDepth() {
        if(description.equals("Not Found")){
            return 0.00;
        }
        String[] descArray =  description.split(";");
        return Double.valueOf(descArray[3].substring(7).replace(" ", "").replace("km", ""));
    }

    @Override
    public String toString(){
        return String.format("%s | %s | M %.2f | D %.2f KM | %s", location.getName(), publishDate.toString(), getMagnitude(), getDepth(),getLocation().getDirectionFromGlasgow());
    }


}
