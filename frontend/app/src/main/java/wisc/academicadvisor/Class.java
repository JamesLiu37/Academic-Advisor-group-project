package wisc.academicadvisor;

/**
 * Created by Dominic DiGiovanni on 12/7/2017.
 */

public class Class {
    //private variables
    int _classid;
    String _courseid;
    int _dayOfWeek;
    String _time;
    // String _userid;
    String _location;

    // Empty constructor
    public Class(){

    }

    // constructor
    /* public Class(String course, int dayOfWeek, String time, String userid, String location){

        this._courseid = course;
        this._dayOfWeek = dayOfWeek;
        this._time = time;
        this._userid = userid;
        this._location = location;
    } */

    public Class(String course, int dayOfWeek, String time, String location){

        this._courseid = course;
        this._dayOfWeek = dayOfWeek;
        this._time = time;
        this._location = location;
    }

    public int getClassID(){
        return this._classid;
    }

    public void setClassID(int classid){
        this._classid = classid;
    }

    // getting course ID
    public String getCourseID(){
        return this._courseid;
    }

    // setting course id
    public void setCourseID(String id){
        this._courseid = id;
    }

    // getting day of week
    public int getDayOfWeek(){
        return this._dayOfWeek;
    }

    // setting day of week
    public void setDayOfWeek(int dow){
        this._dayOfWeek = dow;
    }

    // getting time of class
    public String getTime(){
        return this._time;
    }

    // setting time of class
    public void setTime(String time){
        this._time = time;
    }

    // getting user id
    // public String getUserId(){
    //     return this._userid;
    // }

    // setting user id
    // public void setUserId(String userid){
     //   this._userid = userid;
    // }

    // getting Location
    public String getLocation(){
        return this._location;
    }

    // setting Location
    public void setLocation(String location){
        this._location = location;
    }


}

