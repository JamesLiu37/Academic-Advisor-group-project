package wisc.academicadvisor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.StreamHandler;

public class CourseDescription extends AppCompatActivity {

    private TextView course_UID, course_title, credits, breadth;

    private String course = "", num = "";

    private String jsResponse = "";
    private ArrayList<String> sec_array = new ArrayList<String>();

    private ProgressBar pb;

    private String scrollText = "";

    private LinearLayout linearLayout;

    private JSONArray jaCourse;

    // to pass to next activity
    private String courseParam;
    // to pass to next activity

    private String sectionParamTest;

    private String scheduleParam;

    protected class readServerPage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            pb.setVisibility(View.INVISIBLE);
            findViewById(R.id.subLevel).setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... in) {
            try {
                HttpURLConnection urlConnection = null;
                URL url = null;
                try {
                    course = getIntent().getStringExtra("course");
                    num = getIntent().getStringExtra("courseNum");
                    String search_URL = "http://tyleroconnell.com:8081/course?course=" +
                            course + "&num=" + num;
                    url = new URL(search_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null)
                        jsResponse += line + "\n";

                    try {
                        JSONParser parser = new JSONParser();
                        jaCourse = (JSONArray) parser.parse(jsResponse);

                        final JSONObject firstCourse = (JSONObject) jaCourse.get(0);
                        final String titleString = firstCourse.get("course") + " "
                                + firstCourse.get("courseNum");

                        scrollText += firstCourse.get("description") + "<br />";
                        //Html.fromHtml(HTML-formatted string)
                        course_UID.post(new Runnable() {
                            @Override
                            public void run() {
                                linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                TextView tv = new TextView(CourseDescription.this);
                                // layoutParams.topMargin = 10-20;
                                // tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                // tv.setText("test");
                                tv.setLayoutParams(layoutParams);
                                // Toast.makeText(CourseDescription.this, scrollText, Toast.LENGTH_LONG).show();
                                tv.setText(Html.fromHtml(scrollText));
                                tv.setTextSize(16);
                                tv.setPadding(0,0,0,0);
                                tv.setTextColor(getResources().getColor(R.color.white));
                                linearLayout.addView(tv);
                                scrollText = "";
                            }
                        });


                            // one section done
                            course_UID.post(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {

                                    for (int sec = 0; sec < jaCourse.size(); sec++) {
                                        // scrollText += "<br />"; // OR USE <br />
                                        JSONObject secItr = (JSONObject) jaCourse.get(sec);
                                        scrollText += "<b>" + secItr.get("section") + "</b>";
                                        scrollText += "<br />Professor: " + secItr.get("professors");
                                        scrollText += "<br />Rating: " + secItr.get("ratings");
                                        scrollText += "<br />Average GPA: " + secItr.get("gpa");
                                        scrollText += "<br />";
                                        sec_array.add(parseSchedule(secItr.get("schedules") + ""));
                                        scrollText +=
                                                parseSchedule(secItr.get("schedules") + "");
                                        // scrollText += "<br />";

                                        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);

                                        // if(sec != 0) {
                                            // TODO add line drawable
                                            View view = new View(CourseDescription.this);
                                            LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, 3);
                                            view.setBackgroundColor(getResources().getColor(R.color.white));
                                            view.setLayoutParams(viewParams);
                                            linearLayout.addView(view);
                                        // }

                                        // add TextView
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        TextView tv = new TextView(CourseDescription.this);
                                        layoutParams.topMargin = 33;
                                        // tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        // tv.setText("test");
                                        tv.setLayoutParams(layoutParams);
                                        // Toast.makeText(CourseDescription.this, scrollText, Toast.LENGTH_LONG).show();
                                        tv.setText(Html.fromHtml(scrollText));
                                        tv.setTextSize(16);
                                        tv.setTextColor(getResources().getColor(R.color.white));
                                        linearLayout.addView(tv);

                                        // add Button
                                        Button addCourseBtn = new Button(CourseDescription.this);
                                        addCourseBtn.setTextColor(getResources().getColor(R.color.black));
                                        addCourseBtn.setBackgroundTintList(getResources().getColorStateList(R.color.color_state_list));
                                        addCourseBtn.setText("Add to Schedule");

                                        sectionParamTest = (String)secItr.get("section");
                                        scheduleParam = parseSchedule(secItr.get("schedules") + "");
                                        // Toast.makeText(CourseDescription.this,(String)secItr.get("schedules"), Toast.LENGTH_LONG).show();
                                        addCourseBtn.setOnClickListener(new View.OnClickListener() {
                                            private String sectionParam = sectionParamTest;
                                            private String schedule = scheduleParam;
                                            @Override
                                            public void onClick(View v) {
                                                courseParam = titleString;
                                                // Bundle parameters TODO
                                                Intent intent = new Intent(CourseDescription.this, CourseSchedule.class);
                                                Bundle extras = new Bundle();
                                                extras.putString("course", courseParam + " " + sectionParam);
                                                extras.putString("courseTitle", courseParam);
                                                String [] scheduleArr = new String [5];
                                                // scheduleArr = parseSched(schedule);
                                                String scheduleParsed = "";
                                                try {
                                                    // Toast.makeText(CourseDescription.this, schedule, Toast.LENGTH_LONG).show();
                                                    // parse the schedule
                                                    scheduleArr = convertDate(schedule);
                                                    extras.putStringArray("schedule",scheduleArr);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                // Toast.makeText(CourseDescription.this, scheduleParsed, Toast.LENGTH_LONG).show();
                                                // extras.putString("scheduleStr", scheduleParsed);
                                                // extras.putStringArray("schedule",scheduleArr);
                                                intent.putExtras(extras);
                                                startActivity(intent);
                                            }
                                        });

                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        // addCourseBtn.setGravity("center");
                                        lp.topMargin = 5;
                                        lp.bottomMargin = 25;
                                        lp.leftMargin = 325;
                                        linearLayout.addView(addCourseBtn, lp);

                                        // Bottom line
                                        if(sec == jaCourse.size()-1) {
                                            View viewBot = new View(CourseDescription.this);
                                            LinearLayout.LayoutParams viewParamsBot = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, 3);
                                            viewParamsBot.bottomMargin = 20;
                                            viewBot.setBackgroundColor(getResources().getColor(R.color.white));
                                            viewBot.setLayoutParams(viewParamsBot);
                                            linearLayout.addView(viewBot);
                                        }

                                        scrollText = "";
                                    }
                                }
                            });

                        final String FscrollText = scrollText;
                        course_UID.post(new Runnable() {
                            @Override
                            public void run() {
                                course_UID.setText(titleString);
                                // course_UID.setTextColor(getResources().getColor(R.color.white));

                                course_title.setText("" + firstCourse.get("title"));
                                course_title.setTextColor(getResources().getColor(R.color.white));

                                credits.setText("" + firstCourse.get("numCredits"));
                                credits.setTextColor(getResources().getColor(R.color.white));
                                String bs = firstCourse.get("breadth") + "";
                                if (bs.length() == 0)
                                    bs = "N/A";
                                breadth.setText(bs);
                                breadth.setTextColor(getResources().getColor(R.color.white));
                                // course_desc.setText(Html.fromHtml(FscrollText));

                            }
                        });

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_description);
        course_UID = (TextView) findViewById(R.id.course_UID);
        course_title = (TextView) findViewById(R.id.course_title);
        credits = (TextView) findViewById(R.id.credits);
        breadth = (TextView) findViewById(R.id.breadth);
        // course_desc = (TextView) findViewById(R.id.course_desc);

        pb = (ProgressBar) findViewById(R.id.progressBar);

        readServerPage rSp = new readServerPage();
        rSp.execute();


    }

    public String parseSchedule(String sch) {
        /** Parses strings of the form
         *  MW 4:00-5:15PM\nTR 4:00-5:15PM         *
         */
        String[] daysOfWeek = {"", "", "", "", ""};
        String days = "MTWRF";
        String schOut = "";
        String[] schArr_NL = sch.split("\n");
        // for distinct class times
        for (int L = 0; L < schArr_NL.length; L++) {
            if (schArr_NL[L].length() > 0) {
                String[] perLineArr = schArr_NL[L].split("\\s+");
                // each day
                for (int D = 0; D < 5; D++) {
                    if (perLineArr[0].contains("" + days.charAt(D))) {
                        if (!daysOfWeek[D].equals(""))
                            daysOfWeek[D] += ", "; // multiple classes in 1 day
                        else
                            daysOfWeek[D] += days.charAt(D) + ": "; // 1st class of day
                        daysOfWeek[D] += perLineArr[1]; // time range
                    }
                }
            }
        }
        boolean firstDayReached = false;
        for (int D = 0; D < 5; D++) {
            if (daysOfWeek[D].length() > 0) {
                if (!firstDayReached)
                    firstDayReached = true;
                else // only add new line if we had class on an earlier day of the week
                    schOut += "<br />";
                schOut += daysOfWeek[D];
            }
        }
        return schOut;
    }


    public String[] convertDate(String in) throws ParseException {
        String ret = "";
        // in = in.substring(3);
        String[] times = in.split("<br />");
        // trim off days of the week
        // for(int i = 1; i < times.length; i++){
        //     times[i] = times[i].substring(3);
       //     Toast.makeText(CourseDescription.this, times[i], Toast.LENGTH_LONG).show();
        // }
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");
        // Toast.makeText(CourseDescription.this, "String in: " + in, Toast.LENGTH_SHORT).show();
        Date startDate, endDate;

        String [] test = new String [5];
        test[0] = "";
        test[1] = "";
        test[2] = "";
        test[3] = "";
        test[4] = "";
        for (int i = 0; i < times.length; i++) {
            switch(Character.toString(times[i].charAt(0))){
                case "M":
                    // if
                    times[i] = times[i].substring(3);
                    if(test[0].equals("")){
                        test[0] = parseHelper(times[i]);
                    }
                    else {
                        test[0] = test[0] + "&" + parseHelper(times[i]);
                    }
                    break;
                case "T":
                    times[i] = times[i].substring(3);
                    if(test[1].equals("")){
                        test[1] = parseHelper(times[i]);
                    }
                    else {
                        test[1] = test[1] + "&" + parseHelper(times[i]);
                    }
                    break;
                case "W":
                    times[i] = times[i].substring(3);
                    if(test[2].equals("")){
                        test[2] = parseHelper(times[i]);
                    }
                    else {
                        test[2] = test[2] + "&" + parseHelper(times[i]);
                    }
                    break;
                case "R":
                    times[i] = times[i].substring(3);
                    if(test[3].equals("")){
                        test[3] = parseHelper(times[i]);
                    }
                    else {
                        test[3] = test[3] + "&" + parseHelper(times[i]);
                    }
                    break;
                case "F":
                    times[i] = times[i].substring(3);
                    if(test[4].equals("")){
                        test[4] = parseHelper(times[i]);
                    }
                    else {
                        test[4] = test[4] + "&" + parseHelper(times[i]);
                    }
                    break;
                default:
                    break;
            }

            // Toast.makeText(CourseDescription.this, "ret", Toast.LENGTH_LONG).show();
        }

        /* for(int j = 0; j < test.length; j++){
            Toast.makeText(CourseDescription.this, Integer.toString(j) + " :"+ test[j], Toast.LENGTH_SHORT).show();
        }*/

        // Toast.makeText(CourseDescription.this, "convert " + ret, Toast.LENGTH_LONG).show();

        return test;
    }

    public String parseHelper(String needsHelp) throws ParseException {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");
        Date startDate, endDate;
        String start = needsHelp.split("-")[0];
        String end = needsHelp.split("-")[1];

        if (!start.contains("AM") && !start.contains("PM")) start += end.substring(end.length() - 2, end.length());
        startDate = parseFormat.parse(start);
        endDate = parseFormat.parse(end);

        String ret = displayFormat.format(startDate) + "-" + displayFormat.format(endDate);
        return ret;
    }


    public String parseScheduleMilitary(String[] sch) {
        String s = "";
        boolean first_DOW_reached = false;
        // have we encountered a day of the week with courses yet?
        String MTWRF = "MTWRF";
        for (int day = 0; day < 5; day++) {
            if (sch[day].length() > 0) { // any courses on that day?
                if (!first_DOW_reached)
                    first_DOW_reached = true;
                else // 2nd day of the week with courses, use newline
                    s += "\n";
                s += (MTWRF.charAt(day) + ":");
                String[] classRepeats = sch[day].split("&");
                for (int j = 0; j < classRepeats.length; j++) {
                    String[] times = classRepeats[j].split("-");
                    String start = times[0], end = times[1];
                    String startHour = start.split(":")[0];
                    String m = " AM - ";
                    int sHr = Integer.parseInt(startHour);
                    if (sHr >= 12) {
                        m = " PM - ";
                        if (sHr > 12) {
                            sHr -= 12;
                            start = sHr + ":" + start.split(":")[1];
                        }
                    }
                    s += " " + start + m;
                    String endHour = end.split(":")[0];
                    m = " AM";
                    int eHr = Integer.parseInt(endHour);
                    if (Integer.parseInt(endHour) >= 12) {
                        m = " PM";
                        if (eHr > 12) {
                            eHr -= 12;
                            end = eHr + ":" + end.split(":")[1];
                        }
                    }
                    s += end + m;
                    if (j + 1 < classRepeats.length)
                        s += ","; // last class of day
                }

            }
        }
        return s;
    }

    // parse the schedule M: 8:50-9:40AM

    public String [] parseSched(String schedule){
        // initialize the array to return
        String [] toReturn = new String[5];
        toReturn[0] = "";
        toReturn[1] = "";
        toReturn[2] = "";
        toReturn[3] = "";
        toReturn[4] = "";

        // split by newline
        String [] days = schedule.split("\\r?\\n");
        for(int i = 0; i < days.length; i++){
            String dayOfWeek = days[i];
            String amOrPm = dayOfWeek.substring(dayOfWeek.length() - 2);
            switch(Character.toString(dayOfWeek.charAt(0))){
                case "M":
                    String time = dayOfWeek.substring(3);
                    break;
                case "T":
                    break;
                case "W":
                    break;
                case "R":
                    break;
                case "F":
                    break;
            }
        }


        return null;
    }


}
