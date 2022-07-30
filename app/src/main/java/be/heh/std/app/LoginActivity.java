package be.heh.std.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.User;
import be.heh.std.imported.security.PasswordHash;

public class LoginActivity extends AppCompatActivity {

    private EditText mail;
    private EditText password;
    private TextView error_msg;
    private ArrayList<View> emptyInput;
    private PasswordHash passwordHash;
    private Intent intent;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emptyInput = new ArrayList<View>();
        mail= (EditText) findViewById(R.id.et_login_mail);
        password=(EditText) findViewById(R.id.et_login_password);
        error_msg = (TextView) findViewById(R.id.error_msg);
        emptyInput.addAll(Arrays.asList(mail,password));

    }

    public void onLoginClickManager(View v){
        switch(v.getId()){
            case R.id.bt_login_toRegister:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_login_login:
                Login(v);
                break;
        }
    }

    private void Login(View v){
        error_msg.setText("");
        try{
            for (View e: emptyInput) {
                if((e instanceof EditText) && (((EditText) e).getText().toString().trim().length() == 0))
                    throw new Exception(getString(R.string.empty_err, ((EditText)e).getHint()));
            }
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            user = db.userDao().getUserByEmail(mail.getText().toString().toLowerCase());
            if( user == null)
                throw new Exception(getString(R.string.wrong_login));
            if(passwordHash.validatePassword(password.getText().toString(),user.password)){
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("id", user.id);
                startActivity(intent);
                finish();
            }
            else{
                throw new Exception(getString(R.string.wrong_login));
            }

        }catch(Exception e){
            error_msg.setText(e.getMessage());
        }
    }
}