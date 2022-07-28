package be.heh.std.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import be.heh.std.R;
import be.heh.std.app.LoginActivity;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.Role;
import be.heh.std.database.User;
import be.heh.std.useful.PasswordHash;

public class RegisterActivity extends AppCompatActivity {
    private EditText mail;
    private EditText password;
    private EditText password2;
    private EditText firstname;
    private EditText lastname;
    private TextView error_msg;
    private Boolean firstTime;
    private ArrayList<View> emptyInput;
    private PasswordHash passwordHash;
    private Intent intent;
    private Button toLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emptyInput = new ArrayList<View>();
        error_msg = (TextView) findViewById(R.id.error_msg);
        firstTime = getIntent().getBooleanExtra("first_time", false);
        firstname = (EditText) findViewById(R.id.et_register_firstname);
        lastname = (EditText) findViewById(R.id.et_register_lastname);
        mail = (EditText) findViewById(R.id.et_register_mail);
        password = (EditText) findViewById(R.id.et_register_password);
        password2 = (EditText) findViewById(R.id.et_register_password2);
        emptyInput.addAll(Arrays.asList(firstname, lastname,mail,password,password2));
        toLogin = (Button) findViewById(R.id.bt_register_toLogin);

        if(firstTime){
            toLogin.setVisibility(View.GONE);
        }
    }

    public void onRegisterClickManager(View v){
        switch(v.getId()){
            case R.id.bt_register_toLogin:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_register_register:
                createUser(v);
                break;
        }
    }
    private void createUser(View v){
        try{
            verify(v);
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            User newUser = new User();
            newUser.firstname = firstname.getText().toString();
            newUser.lastname = lastname.getText().toString();
            newUser.mail = mail.getText().toString().toLowerCase();
            newUser.password = passwordHash.hashPassword(password.getText().toString());
            newUser.role = (firstTime)? Role.ADMIN : Role.BASIC;

            if(db.userDao().getUserByEmail(newUser.mail) != null)
                throw new Exception(getString(R.string.taken_email_err));

            db.userDao().addUser(newUser);

            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        }catch(Exception e){
            error_msg.setText(e.getMessage());
        }
    }
    private void verify(View v) throws Exception {
        error_msg.setText("");

        //Verification qu'un champ est vide ou non
        for (View e: emptyInput) {
            if((e instanceof EditText) && (((EditText) e).getText().toString().trim().length() == 0))
                throw new Exception(getString(R.string.empty_err, ((EditText)e).getHint()));
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(mail.getText()).matches()))
            throw new Exception(getString(R.string.bad_email_err, mail.getHint()));
        if(!Pattern.matches("^(?=.*[0-9])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$", password.getText()))
            throw new Exception(getString(R.string.bad_password_err, password.getHint(), 8, 1, 1));
        if(!password.getText().toString().equals(password2.getText().toString()))
            throw new Exception(getString(R.string.dont_match_err, password.getHint(), password2.getHint()));
    }
}