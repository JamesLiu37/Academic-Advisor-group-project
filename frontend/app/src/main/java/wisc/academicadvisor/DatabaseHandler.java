package wisc.academicadvisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominic DiGiovanni on 12/7/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "academicadvisor";

    // user table
    private static final String TABLE_USER = "users";

    // user Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    private static final String TABLE_CLASS = "class";

    // columns for class table
    private static final String KEY_CLASS_ID = "classid";
    private static final String KEY_COURSE_ID = "courseid";
    private static final String KEY_DAY_OF_WEEK= "dayofweek";
    private static final String KEY_TIME = "time";
    // private static final String KEY_USER_ID = "userid";
    private static final String KEY_LOCATION = "location";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT"
                + ")";

        /* String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS + "("
                + KEY_CLASS_ID + " INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,"
                + KEY_COURSE_ID + " TEXT," + KEY_DAY_OF_WEEK + " INTEGER,"
                + KEY_TIME + " TEXT," + KEY_LOCATION + " TEXT" + ")"; */

        Log.d("DATABASE HANDLER", "User Table Created");

        String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS + "("
                + KEY_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_COURSE_ID + " TEXT, " + KEY_DAY_OF_WEEK + " INTEGER," + KEY_TIME + " TEXT," + KEY_LOCATION + " TEXT" + ")";

        Log.d("DATABASE HANDLER", "Tables created");

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);

        // Create tables again
        onCreate(db);

    }

    // Adding new USER
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getID());
        values.put(KEY_NAME, user.getName()); // Name

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addClass(Class classToAdd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_ID, classToAdd.getCourseID());
        values.put(KEY_DAY_OF_WEEK, classToAdd.getDayOfWeek());
        values.put(KEY_TIME, classToAdd.getTime());
        values.put(KEY_LOCATION, classToAdd.getLocation());
        // values.put(KEY_USER_ID, classToAdd.getUserId());


        // Inserting Row
        db.insert(TABLE_CLASS, null, values);
        db.close(); // Closing database connection
    }


    // Getting single user
    // TODO fix this should be String
   /*  public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, new String[] { KEY_ID,
                        KEY_NAME}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(cursor.getString(0),
                cursor.getString(1));

        return user;
    } */


    // Updating single user
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());

        // updating row
        return db.update(TABLE_USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
    }

    // Deleting single user
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
        db.close();
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setID(cursor.getString(0));
                user.setName(cursor.getString(1));
                // Adding user to list
                userList.add(user);
            } while (cursor.moveToNext());
        }

        return userList;
    }

    public List<Class> getAllClasses(){
        List<Class> classList = new ArrayList<Class>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLASS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Class classVar = new Class();

                // *** TODO check this ******
                classVar.setCourseID(cursor.getString(cursor.getColumnIndex(KEY_COURSE_ID)));
                classVar.setDayOfWeek(cursor.getInt(cursor.getColumnIndex(KEY_DAY_OF_WEEK)));
                classVar.setTime(cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                classVar.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                // Adding class to list
                classList.add(classVar);
            } while (cursor.moveToNext());
        }

        return classList;
    }

    /*
    public void deleteCourse(String course){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "DELETE FROM " + TABLE_CLASS + " WHERE " + KEY_COURSE_ID + " = " + course;
    }*/

    public void deleteCourse(String course)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, KEY_COURSE_ID + "=?", new String[]{course});
        db.close();
    }

    // delete the user table if logging out
    public void logoutUser(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.execSQL("DELETE FROM " + TABLE_CLASS);
        db.close();
    }

}
