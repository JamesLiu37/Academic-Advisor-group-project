package wisc.academicadvisor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class CourseSchedule extends AppCompatActivity {
    private View toSchedule;
    private String day;

    // ListView variables
    private ListView coursesAddedSoFar;
    private ArrayList<String> coursesList;
    private ArrayAdapter<String> adapterList;

    // 5 days in a week (key), start and end time values int[numClassesinthatday][start (0) or end time (1)]
    private HashMap<Integer, int[][]> classTimeHash = new HashMap<Integer, int[][]>();

    // track the number of classed added to have unique ids for the views
    private int numClassesAdded = 0;

    // track the number of courses added (used for view coloring)
    private int numCoursesAdded = 0;

    // store the first and last id of the classes(views) in a course
    private HashMap<String, int[]> courseIdsHash = new HashMap<String, int[]>();

    // index is the id, points to the day of the week
    private ArrayList<Integer> idToDay = new ArrayList<>();

    // instance of the local database
    private DatabaseHandler db;

    // user logged in
    private String user;

    private List<Class> loadedClasses;

    private LinearLayout topOfSchedule;

    private RelativeLayout.LayoutParams params;

    private boolean toRemove;

    private String courseViewTitle;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedule);

        // instantiate the database
        db = new DatabaseHandler(this);

        // instanitate the linear layout for the top of schedule to dynamically set margins
        topOfSchedule = (LinearLayout)findViewById(R.id.dayLabelsLinearLayout);
        // linear layour parameters
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        // Listview that holds courses aded
        coursesAddedSoFar = (ListView)findViewById(R.id.coursesAdded);
        coursesList = new ArrayList<String>();

        // set array adapter for the Listview, gives styling to the output
        adapterList = new ArrayAdapter<String>(this,
                R.layout.schedule_course_list, coursesList);
        coursesAddedSoFar.setAdapter(adapterList);


        // TODO load old classes that have been added
        // load classes that were previously added
        loadClasses();

        // if there are classes loaded in dynamically set the schedule margin top
        /* if(coursesList.size() > 0 && coursesList.size() < 3){
            params.setMargins(0,dpToPx(150),0,0);
            topOfSchedule.setLayoutParams(params);
        }
        else if(coursesList.size() >= 3){
            params.setMargins(0,dpToPx(200),0,0);
            topOfSchedule.setLayoutParams(params);
        } */

        // grab data from previous activity, use try catch for when no activity added (just viewing previous schedule)
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            // get the course name and timing of classes
            String course = extras.getString("course");
            // TODO
            String[] schedule = extras.getStringArray("schedule");

            // Toast.makeText(CourseSchedule.this, schedule, Toast.LENGTH_LONG).show();

            // add course unless already added
            if(!coursesList.contains(course)) {
                coursesList.add(course);
                adapterList.notifyDataSetChanged();

                // set margin top
                /* if(coursesList.size() >= 3){
                    params.setMargins(0,dpToPx(200),0,0);
                    topOfSchedule.setLayoutParams(params);
                } */

                // add the course to the schedule
                // TODO
                addToSched(schedule, course);
            }

        } catch (Exception e){

            // input a message saying no courses have been added if no courses loaded or added
            if(coursesList.isEmpty()){
                // set top margin to be smaller since no courses added
                params.setMargins(0,dpToPx(85),0,0);
                topOfSchedule.setLayoutParams(params);
                // setMargins(topOfSchedule, 0, 200, 0, 0);
                coursesList.add("No Courses Added");
                adapterList.notifyDataSetChanged();
            }
        }


        // when courses are clicked on (REMOVE)
        coursesAddedSoFar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                final int positionAccess = position;

                if(coursesList.get(position).equals("No Courses Added")){
                    // TODO go to course Search page maybe
                    Toast.makeText(CourseSchedule.this, "No Courses Added Go Search", Toast.LENGTH_LONG).show();
                }
                // clicked on a course
                else {
                    // Alert dialog to decide if course should be deleted
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(CourseSchedule.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(CourseSchedule.this);
                    }
                    builder.setTitle("Remove Course")
                            .setMessage("Are you sure you want to remove " + coursesList.get(position) + " ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    // remove the classes corresponding to the course
                                    String course = coursesList.get(positionAccess);
                                    int[] ids = courseIdsHash.get(course);

                                    // delete rows from class table
                                    db.deleteCourse(course);

                                    // delete views (loop through the ids corresponding to the course)
                                    for (int i = ids[0]; i < ids[1] + 1; i++) {
                                        // find the view by unique id
                                        View toDelete = (View) findViewById(i);
                                        // find which day of the week the view belongs to and delete it
                                        switch (idToDay.get(i)) {
                                            case 0:
                                                RelativeLayout rm = (RelativeLayout) findViewById(R.id.mondayRelativeLayout);
                                                rm.removeView(toDelete);
                                                break;
                                            case 1:
                                                RelativeLayout rt = (RelativeLayout) findViewById(R.id.tuesdayRelativeLayout);
                                                rt.removeView(toDelete);
                                                break;
                                            case 2:
                                                RelativeLayout rw = (RelativeLayout) findViewById(R.id.wednesdayRelativeLayout);
                                                rw.removeView(toDelete);
                                                break;
                                            case 3:
                                                RelativeLayout rth = (RelativeLayout) findViewById(R.id.thursdayRelativeLayout);
                                                rth.removeView(toDelete);
                                                break;
                                            case 4:
                                                RelativeLayout rf = (RelativeLayout) findViewById(R.id.fridayRelativeLayout);
                                                rf.removeView(toDelete);
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    // Remove the course if pressed
                                    coursesList.remove(positionAccess);

                                    // TODO set margins if all courses removed
                                    if (coursesList.size() == 0) {
                                        // params.setMargins(0,dpToPx(100),0,0);
                                        // topOfSchedule.setLayoutParams(params);
                                    }

                                    adapterList.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing since cancel
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    }
                }
        });
    }

    // convert dp to pixels
    public int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    // add a course to the schedule
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void addToSched(String[] days, String course)
    {

        int dayOfTheWeek = 0;
        // Relative layouts for different days of the week
        RelativeLayout rm;
        RelativeLayout rt;
        RelativeLayout rw;
        RelativeLayout rth;
        RelativeLayout rf;

        String [] tokens;

        int prevNumClasses = numClassesAdded;

        Class classToAdd;

        // loop through week days
        while(dayOfTheWeek < 5){
            // cases for all days of the week to add classes as views
            switch (dayOfTheWeek) {
                case 0:  rm = (RelativeLayout)findViewById(R.id.mondayRelativeLayout);

                    // check if the day has no class scheduled
                    if(days[dayOfTheWeek].equals("")){
                        break;
                    }

                    // parse by & (split multiple classes in a day)
                    tokens = parseDay(days[dayOfTheWeek]);

                    // add this day of classes to the schedule
                    for(int i = 0; i < tokens.length; i++) {
                        // parse the indiviaby : and -
                        addClass(parseClass(tokens[i]), rm, 0, tokens[i], course);
                        // add class to database
                        classToAdd = new Class(course, dayOfTheWeek, tokens[i], "none");
                        db.addClass(classToAdd);
                    }

                    break;
                case 1:  rt = (RelativeLayout)findViewById(R.id.tuesdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(days[dayOfTheWeek].equals("")){
                        break;
                    }

                    // parse by &
                    tokens = parseDay(days[dayOfTheWeek]);

                    // add this day of classes to the schedule
                    for(int i = 0; i < tokens.length; i++) {
                        // parse by : and -
                        addClass(parseClass(tokens[i]), rt, 1, tokens[i], course);
                        // add class to database
                        classToAdd = new Class(course, dayOfTheWeek, tokens[i], "none");
                        db.addClass(classToAdd);
                    }


                    break;
                case 2:  rw = (RelativeLayout)findViewById(R.id.wednesdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(days[dayOfTheWeek].equals("")){
                        break;
                    }

                    // parse by &
                    tokens = parseDay(days[dayOfTheWeek]);

                    // add this day of classes to the schedule
                    for(int i = 0; i < tokens.length; i++) {
                        // parse by : and -
                        addClass(parseClass(tokens[i]), rw, 2, tokens[i], course);
                        // add class to database
                        classToAdd = new Class(course, dayOfTheWeek, tokens[i], "none");
                        db.addClass(classToAdd);
                    }


                    break;
                case 3:  rth = (RelativeLayout)findViewById(R.id.thursdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(days[dayOfTheWeek].equals("")){
                        break;
                    }

                    // parse by &
                    tokens = parseDay(days[dayOfTheWeek]);

                    // add this day of classes to the schedule
                    for(int i = 0; i < tokens.length; i++) {
                        // parse by : and -
                        addClass(parseClass(tokens[i]), rth, 3, tokens[i], course);
                        // add class to database
                        classToAdd = new Class(course, dayOfTheWeek, tokens[i], "none");
                        db.addClass(classToAdd);
                    }


                    break;
                case 4:  rf = (RelativeLayout)findViewById(R.id.fridayRelativeLayout);

                    // check if the day has no class scheduled
                    if(days[dayOfTheWeek].equals("")){
                        break;
                    }

                    // parse by &
                    tokens = parseDay(days[dayOfTheWeek]);

                    // add this day of classes to the schedule
                    for(int i = 0; i < tokens.length; i++) {
                        // parse by : and -
                        addClass(parseClass(tokens[i]), rf, 4, tokens[i], course);
                        // add class to database
                        classToAdd = new Class(course, dayOfTheWeek, tokens[i], "none");
                        db.addClass(classToAdd);
                    }


                    break;
                default:
                    break;
            }

            dayOfTheWeek++;

        }

        // set the id of the last class
        int lastClassAdded = numClassesAdded;
        lastClassAdded--;

        // put the ids in the hash map
        int[] ids = {prevNumClasses, lastClassAdded};

        courseIdsHash.put(course, ids);
        numCoursesAdded++;
    }

    // parse the schedule for the class and return the tokens
    public String[] parseClass(String day){

        String delim = "[ &:-]+";
        String[] tokens;

        tokens = day.split(delim);

        return tokens;
    }

    // parse the entire day intp separate classes
    public String[] parseDay(String day){
        String delim = "[ &]+";
        String[] tokens;

        tokens = day.split(delim);

        return tokens;
    }


    // add one day of classes to the schedule
    public void addClass(String [] tokens, RelativeLayout rl, int day, String time, String course){
            // Calculate where to place the class
            int timeHour = Integer.parseInt(tokens[0]);
            // subtract 7 since schedule starts at 7 am, multiply by 60 since each block is 60dp
            int offset = ((timeHour - 7) * 60);

            // time in minutes after the hour
            int timeMin = Integer.parseInt(tokens[1]);
            // add the minutes to offset since 1 min = 1dp
            offset = offset + timeMin;

            // calculate the class time
            int timeHourEnd = Integer.parseInt(tokens[2]);
            int timeMinEnd = Integer.parseInt(tokens[3]);

            int offsetEnd = ((timeHourEnd - 7) * 60);
            offsetEnd = offsetEnd + timeMinEnd;

            int classTime = offsetEnd - offset;
            int heightPx = dpToPx(classTime);


            // new view to add
            // View v = new View(this);
            TextView v = new TextView(this);
            // TODO put correct labels on views
            v.setText(time);
            v.setPadding(dpToPx(3), 0, 0, 0);
            v.setTextColor(getResources().getColor(R.color.black));

            RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    heightPx);
            rp.setMargins(dpToPx(0), dpToPx(offset), 0, 0);
            v.setLayoutParams(rp);

        // Toast.makeText(CourseSchedule.this,"NumCourses in case: " + numCoursesAdded,Toast.LENGTH_SHORT).show();
        // TODO set view colors
            switch(numCoursesAdded) {
                case 0:
                    v.setBackgroundColor(getResources().getColor(R.color.lightestBlue));
                    // Toast.makeText(CourseSchedule.this,"Case 0: ID: " + Integer.toString(v.getId()),Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    // Toast.makeText(CourseSchedule.this,"Case 1: ID: " + Integer.toString(v.getId()),Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    v.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                    // Toast.makeText(CourseSchedule.this,"Case 2: ID: " + Integer.toString(v.getId()),Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    v.setBackgroundColor(getResources().getColor(R.color.red));
                    break;
                case 4:
                    v.setBackgroundColor(getResources().getColor(R.color.green));
                    break;
                case 5:
                    v.setBackgroundColor(getResources().getColor(R.color.purple));
                    break;
                case 6:
                    v.setBackgroundColor(getResources().getColor(R.color.orange));
                    break;
                case 7:
                    v.setBackgroundColor(getResources().getColor(R.color.lightestBlue));
                    break;
                default:
                    v.setBackgroundColor(getResources().getColor(R.color.lightestBlue));
                    break;
            }
            // v.setBackgroundResource(R.drawable.line);

            // set the id of view to a unique integer
            v.setId(numClassesAdded);

            // set the day of the week that corresponds with this class (id, day of the week)
            idToDay.add(numClassesAdded, day);

        // Toast.makeText(CourseSchedule.this, "Class Added " + time +
        //        "  class number: " + Integer.toString(numClassesAdded), Toast.LENGTH_SHORT).show();

            // increment number of classes added to be used for next id
            numClassesAdded++;

        final String courseClick = course;
        // TODO handle view clicks
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Do some job here
                // Toast.makeText(CourseSchedule.this, courseClick, Toast.LENGTH_SHORT).show();


                // Alert dialog to decide if course should be deleted
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(CourseSchedule.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(CourseSchedule.this);
                }
                builder.setTitle("Course Information")
                        .setMessage("Course: " + courseClick)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        /* .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing since cancel
                            }
                        }) */
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();



            }
        });

        // add the view to the relative layout
            rl.addView(v);

    }

    // load classes that had been added to the schedule previously
    public void loadClasses(){
        loadedClasses = new ArrayList<Class>();
        loadedClasses = db.getAllClasses();

        String currCourse = "";
        String prevCourse = "";

        int counter = 0;

        int firstClassAdded = 0;
        int lastClassAdded = 0;
        boolean lastClassSet = false;
        boolean lastCourse = false;
        for(int i = 0; i < loadedClasses.size(); i++){
            lastClassSet = false;
            lastCourse = false;

            String course = loadedClasses.get(i).getCourseID();
            currCourse = course;

            // this shouldnt happen
            if(!coursesList.contains(course)) {
                coursesList.add(course);
                adapterList.notifyDataSetChanged();
            }

            // handle view ids for deletion
            if((!currCourse.equals(prevCourse) || i==loadedClasses.size()-1) && numClassesAdded != 0 ){
                // if not the last course
                if((!currCourse.equals(prevCourse))) {
                    lastClassAdded = numClassesAdded-1;
                    numCoursesAdded++;
                    // Toast.makeText(CourseSchedule.this, course + " " + Integer.toString(numCoursesAdded),Toast.LENGTH_SHORT).show();
                }
                // last course
                else {
                    lastClassAdded = numClassesAdded;
                    lastCourse = true;
                }

                int[] ids = {firstClassAdded, lastClassAdded};

                courseIdsHash.put(prevCourse, ids);

                lastClassSet = true;
            }

            RelativeLayout rm;
            RelativeLayout rt;
            RelativeLayout rw;
            RelativeLayout rth;
            RelativeLayout rf;

            String [] tokens;

            int dayOfTheWeek = loadedClasses.get(i).getDayOfWeek();
            String time = loadedClasses.get(i).getTime();


            // cases for all days of the week to add classes as views
            switch (dayOfTheWeek) {
                case 0:  rm = (RelativeLayout)findViewById(R.id.mondayRelativeLayout);

                    // check if the day has no class scheduled
                    if(time.equals("")){
                        break;
                    }

                    tokens = parseClass(time);

                    // add this day of classes to the schedule
                    addClass(tokens, rm, 0, time, currCourse);

                    break;
                case 1:  rt = (RelativeLayout)findViewById(R.id.tuesdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(time.equals("")){
                        break;
                    }

                    tokens = parseClass(time);

                    // add this day of classes to the schedule
                    addClass(tokens, rt, 1, time, currCourse);

                    break;
                case 2:  rw = (RelativeLayout)findViewById(R.id.wednesdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(time.equals("")){
                        break;
                    }

                    tokens = parseClass(time);

                    // add this day of classes to the schedule
                    addClass(tokens, rw, 2, time, currCourse);

                    break;
                case 3:  rth = (RelativeLayout)findViewById(R.id.thursdayRelativeLayout);

                    // check if the day has no class scheduled
                    if(time.equals("")){
                        break;
                    }

                    tokens = parseClass(time);

                    // add this day of classes to the schedule
                    addClass(tokens, rth, 3, time, currCourse);

                    break;
                case 4:  rf = (RelativeLayout)findViewById(R.id.fridayRelativeLayout);

                    // check if the day has no class scheduled
                    if(time.equals("")){
                        break;
                    }

                    tokens = parseClass(time);

                    // add this day of classes to the schedule
                    addClass(tokens, rf, 4, time, currCourse);

                    break;
                default:
                    break;
            }

            if(lastClassSet){
                firstClassAdded = numClassesAdded - 1;
            }
            // increment number of courses added to schedule for the last course in load
            if(lastCourse){
                numCoursesAdded++;
            }
            prevCourse = currCourse;

        }

    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
