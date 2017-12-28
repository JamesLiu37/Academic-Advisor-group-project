package wisc.academicadvisor;

/**
 * Created by Dominic DiGiovanni on 12/7/2017.
 */

public class User {
    //private variables
    String _id;
    String _name;

    // Empty constructor
    public User(){

    }

    // constructor just username
    public User(String id){
        this._id = id;
    }

    // constructor
    public User(String id, String name){
        this._id = id;
        this._name = name;
    }

    // getting ID
    public String getID(){
        return this._id;
    }

    // setting id
    public void setID(String id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

}
