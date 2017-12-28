package wisc.academicadvisor;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button loginBtn;
    private TextView signUp;
    private Drawable buttonCurve;
    private ImageView usernameRect;
    private ImageView passwordRect;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check to see if a user is logged in
        db = new DatabaseHandler(this);
        if(db.getAllUsers().size() > 0){
            setContentView(R.layout.activity_account_page);
            startActivity(new Intent(Welcome.this, AccountPage.class));
        }
        setContentView(R.layout.activity_welcome);


        // make it so editText doesn't pop up immediately
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        loginBtn = (Button)findViewById(R.id.login);

        // listener for the login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // make sure there is text for username and password
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(Welcome.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    // TODO query the database for matching username and password
                    startActivity(new Intent(Welcome.this, AccountPage.class));
                }
            }
        });

        // set the default login button color (light so that appears not clickable yet)
        loginBtn.setBackgroundColor(getResources().getColor(R.color.lightestBlue));

        // instantiate the button curve drawable and set the background color
        /* buttonCurve = ContextCompat.getDrawable(this, R.drawable.buttoncurve).mutate();
        buttonCurve.setColorFilter(new PorterDuffColorFilter(getResources().getColor(
                R.color.lightestBlue), PorterDuff.Mode.SRC_IN)); */

        signUp = (TextView)findViewById(R.id.signUp);

        /* Button createAnAccount = (Button)findViewById(R.id.createanaccount);

        // create an account button listener
        createAnAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, CreateAnAccount.class));
            }
        }); */

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this, CreateAnAccount.class));
            }
        });

        username = (EditText)findViewById(R.id.usernameEdit);
        password = (EditText)findViewById(R.id.passwordEdit);


        // make the password hint text default
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        username.addTextChangedListener(usernameWatcher);
        password.addTextChangedListener(passwordWatcher);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // username.setHintTextColor(getResources().getColor(R.color.lightBlue));

                } else {
                    // username.setHintTextColor(getResources().getColor(R.color.hintgray));

                }
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // password.getText().clear();
                // password.setHint("");
                /* if(!(username.getText().toString().equals("") || password.getText().toString().equals(""))) {
                    loginBtn.setBackgroundColor(getResources().getColor(R.color.lighterBlue));
                } */
            }
        });

    }

    // listner for edit text change for the password
    private final TextWatcher passwordWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            // Check to see if text entered is of length 0
            if(s.length() == 0){
                // if so reset the hint
                password.setHint("Password");
            }

            // check if the username and password both have text
            if(!(username.getText().toString().equals("") || password.getText().toString().equals(""))) {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                // buttonCurve.setColorFilter(new PorterDuffColorFilter(getResources().getColor(
                 //       R.color.lighterBlue), PorterDuff.Mode.SRC_IN));
            }
            else {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.lightestBlue));
                // buttonCurve.setColorFilter(new PorterDuffColorFilter(getResources().getColor(
                //        R.color.lightestBlue), PorterDuff.Mode.SRC_IN));
            }
        }
    };

    private final TextWatcher usernameWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                username.setHint("Username");
            }

            if(!(username.getText().toString().equals("") || password.getText().toString().equals(""))) {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                // buttonCurve.setColorFilter(new PorterDuffColorFilter(getResources().getColor(
                //        R.color.lighterBlue), PorterDuff.Mode.SRC_IN));
            }
            else {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.lightestBlue));
                // buttonCurve.setColorFilter(new PorterDuffColorFilter(getResources().getColor(
                //        R.color.lightestBlue), PorterDuff.Mode.SRC_IN));
            }
        }
    };
}
