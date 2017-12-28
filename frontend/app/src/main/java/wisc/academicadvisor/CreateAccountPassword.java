package wisc.academicadvisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountPassword extends AppCompatActivity {
    EditText editPass;
    EditText editPassReenter;
    String passwordToStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_password);

        Button passwordBtn = (Button)findViewById(R.id.password_enter_btn);
        editPass = (EditText)findViewById(R.id.password_input);
        editPassReenter = (EditText)findViewById(R.id.password_reenter_input);

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int passLength = editPass.getText().toString().length();
                String password = editPass.getText().toString();
                int upCount = 0;
                int loCount = 0;
                int digit = 0;
                int special = 0;

                // compare the password and the passwore reentered to see if they match
                if(!editPass.getText().toString().equals(editPassReenter.getText().toString())){
                    Toast.makeText(CreateAccountPassword.this, "The passwords do not match", Toast.LENGTH_SHORT).show();
                    editPass.setText("");
                    editPassReenter.setText("");
                }
                else if(passLength < 8 || passLength > 16){
                    Toast.makeText(CreateAccountPassword.this, "Your password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    editPass.setText("");
                    editPassReenter.setText("");
                }
                else {
                    // iterate through password checking for number of upper case, digits, and special characters
                    for (int i = 0; i < passLength; i++) {
                        char c = password.charAt(i);
                        if (Character.isUpperCase(c)) {
                            upCount++;
                        }
                        if (Character.isLowerCase(c)) {
                            loCount++;
                        }
                        if (Character.isDigit(c)) {
                            digit++;
                        }
                        if (c >= 33 && c <= 46 || c == 64) {
                            special++;
                        }
                    }
                    // if(special>=1&&loCount>=1&&upCount>=1&&digit>=1){
                    if(loCount>=1&&upCount>=1&&digit>=1){
                        passwordToStore = password;
                        startActivity(new Intent(CreateAccountPassword.this, AccountPage.class));
                    }
                    else {
                        if(loCount < 1){
                            Toast.makeText(CreateAccountPassword.this, "Your password should have at least one lowercase letter", Toast.LENGTH_SHORT).show();
                        }
                        else if (upCount < 1){
                            Toast.makeText(CreateAccountPassword.this, "Your password should have at least one capital letter", Toast.LENGTH_SHORT).show();
                        }
                        else if (digit < 1){
                            Toast.makeText(CreateAccountPassword.this, "Your password should have at least one digit", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // put this button just to get to the next activity quickly for testing
        // Button testBtn = (Button)findViewById(R.id.testBtn);
        // testBtn.setOnClickListener(new View.OnClickListener() {
         //   public void onClick(View v) {
         //       startActivity(new Intent(CreateAccountPassword.this, AccountPage.class));
         //   }
        //});


    }
}
