package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.User;
import be.heh.std.imported.security.PasswordHash;

public class ProfileActivity extends AppCompatActivity {

    private EditText mail;
    private EditText firstname;
    private EditText lastname;
    private EditText oldPassword;
    private EditText newPassword;
    private TextView error_msg;
    private ArrayList<View> emptyInput;
    private ArrayList<View> emptyInputPwd;
    private User user;
    private int id;
    private Intent intent;
    private AppDatabase db;
    private PasswordHash passwordHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        intent = getIntent();
        emptyInput = new ArrayList<>();
        emptyInputPwd = new ArrayList<View>();
        error_msg = (TextView) findViewById(R.id.error_msg);
        mail = (EditText) findViewById(R.id.et_profile_mail);
        firstname = (EditText) findViewById(R.id.et_profile_firstname);
        lastname = (EditText) findViewById(R.id.et_profile_lastname);
        oldPassword =(EditText) findViewById(R.id.et_profile_password);
        newPassword =(EditText) findViewById(R.id.et_profile_password2);
        id = intent.getIntExtra("id", -1);

        db = AppDatabase.getInstance(getApplicationContext());
        user = db.userDao().getUserById(id);

        emptyInput.addAll(Arrays.asList(mail,firstname,lastname));
        emptyInputPwd.addAll(Arrays.asList(oldPassword,newPassword));
        updateView();
    }

    public void onProfileClickManager(View v){
        switch(v.getId()){
            case R.id.bt_profile_update:
                update(v);
                break;
            case R.id.bt_profile_updatePwd:
                updatePwd(v);
                break;
        }
    }

    private void update(View v){
        try{
            verify(v);
            db.userDao().updateUser(id,firstname.getText().toString(),lastname.getText().toString(),mail.getText().toString().toLowerCase());
            Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();
            return;
        }catch(Exception e){
            error_msg.setText(e.getMessage());
        }
    }
    private void updatePwd(View v){
        try{
            verifyPwd(v);
            db.userDao().updatePassword(id, passwordHash.hashPassword(newPassword.getText().toString()));
            Toast.makeText(getApplicationContext(),"New password saved",Toast.LENGTH_LONG).show();
            return;
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

        return;
    }

    private void verifyPwd(View v) throws Exception{
        error_msg.setText("");

        for (View e: emptyInputPwd) {
            if((e instanceof EditText) && (((EditText) e).getText().toString().trim().length() == 0))
                throw new Exception(getString(R.string.empty_err, ((EditText)e).getHint()));
        }
        if(!Pattern.matches("^(?=.*[0-9])(?=.*[@#$%^&+=*])(?=\\S+$).{8,}$", newPassword.getText()))
            throw new Exception(getString(R.string.bad_password_err, newPassword.getHint(), 8, 1, 1));
        if(!passwordHash.validatePassword(oldPassword.getText().toString(),user.password)){
            throw new Exception(getString(R.string.wrong_pwd));
        }
        return;
    }

    private void updateView(){
    mail.setText(user.mail);
    firstname.setText(user.firstname);
    lastname.setText(user.lastname);
    }
}