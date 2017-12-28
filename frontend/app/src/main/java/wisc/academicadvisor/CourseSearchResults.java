package wisc.academicadvisor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Text;

import android.text.Html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CourseSearchResults extends AppCompatActivity {

    private ArrayList<Spanned> a_ClassList = new ArrayList<Spanned>();
    private String jsResponse = "";
    private JSONArray ja_Courses_g;
    private ArrayAdapter<Spanned> adapterList;
    private TextView resultsTitle;
    private String course = "", courseNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Spanned htmlText = Html.fromHtml("test");
        // a_ClassList.add(htmlText);
        // adapterList.notifyDataSetChanged();
        setContentView(R.layout.activity_course_search_results);
        resultsTitle = (TextView) findViewById(R.id.resultsTitle);

        readServerPage rSp = new readServerPage();
        rSp.execute();
    }

    protected class readServerPage extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            URL url = null;
            try {
                JSONParser parser = new JSONParser();
                final String search_URL = getIntent().getStringExtra("search_URL");
                url = new URL(search_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                jsResponse = "";
                String line;
                while ((line = rd.readLine()) != null)
                    jsResponse += line + "\n";
                final JSONArray ja_Courses = (JSONArray) parser.parse(jsResponse);
                ja_Courses_g = ja_Courses;
                final TextView launcher = (TextView) findViewById(R.id.resultsTitle);
                launcher.post(new Runnable() {
                    @Override
                    public void run() {
                        final String numFilters = getIntent().getStringExtra("filters");
                        resultsTitle.setText(ja_Courses_g.size() + " Course Search Results\n" +
                                "with " + numFilters + " of 7 filters applied");
                        for (int c = 0; c < ja_Courses.size(); c++) {
                            final JSONObject joCourse = (JSONObject) ja_Courses.get(c);
                            //String sectionNum = ("" + joCourse.get("fullCourse")).split("-")[1];
                            String cInfo = "<b>" + joCourse.get("title") + "</b>";
                            course = joCourse.get("course") + "";
                            courseNum = joCourse.get("courseNum") + "";
                            cInfo += "<br /><i>" + course + " "
                                    + courseNum
                                    + "</i> (" + joCourse.get("numCredits") + " credits)";
                            //if (!a_ClassList.contains(course + " " + courseNum))
                            a_ClassList.add(Html.fromHtml(cInfo));
                        }

                        ListView courseList = (ListView) findViewById(R.id.courseList);
                        adapterList = new ArrayAdapter<Spanned>(CourseSearchResults.this,
                                R.layout.course_search_results_list, a_ClassList);
                        courseList.setAdapter(adapterList);

                        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView parent, View v, int position, long id) {
                                // Remove interest from list if clicked
                                Intent intent = new Intent(CourseSearchResults.this, CourseDescription.class);
                                intent.putExtra("course",
                                        ((JSONObject) ja_Courses_g.get(position)).get("course") + "");
                                intent.putExtra("courseNum",
                                        ((JSONObject) ja_Courses_g.get(position)).get("courseNum") + "");
                                startActivity(intent);
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dontcare) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);
            findViewById(R.id.subLevel).setVisibility(View.VISIBLE);
        }
    }


}
