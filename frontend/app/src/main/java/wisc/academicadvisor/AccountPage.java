package wisc.academicadvisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccountPage extends AppCompatActivity {
    private Button searchBtn;
    private Button scheduleBtn;
    private Button logoutBtn;
    private DatabaseHandler db;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        // instantiate the buttons
        searchBtn = (Button)findViewById(R.id.searchBtn);
        scheduleBtn = (Button)findViewById(R.id.scheduleBtn);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);

        db = new DatabaseHandler(this);
        tv = (TextView)findViewById(R.id.welcomeBack);
        if(db.getAllUsers().size() > 0) {
            String text = "Hello, " + db.getAllUsers().get(0).getName();
            tv.setText(text);
        }


        // listener for the course search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(AccountPage.this, CourseSearch.class));
            }
        });

        // listener for the schedule button
        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(AccountPage.this, CourseSchedule.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO LOGOUT USER AND RETURN TO LOGIN SCREEN
                db.logoutUser();
                startActivity(new Intent(AccountPage.this, Welcome.class));
            }
        });
    }
}
