package app.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Event {

    private Integer id;
    private String day;
    private String location;
    private String summary;
    private String title;
    public Event() {

    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getSummary(){
        return this.summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public String  getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title= title;
    }


}
