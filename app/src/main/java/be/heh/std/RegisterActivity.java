package be.heh.std;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;

public class RegisterActivity extends Activity {
    EditText et_register_mail;
    EditText et_register_password;
    EditText et_register_mail2;
    EditText et_register_password2;
    EditText et_register_firstname;
    EditText et_register_lastname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_register_firstname = (EditText) findViewById(R.id.et_register_firstname);
        et_register_lastname = (EditText) findViewById(R.id.et_register_lastname);
        et_register_mail = (EditText) findViewById(R.id.et_register_mail);
        et_register_mail2 = (EditText) findViewById(R.id.et_register_mail2);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        et_register_password2 = (EditText) findViewById(R.id.et_register_password2);
    }

    public void onRegisterClickManager(View v){
        switch(v.getId()){
            case R.id.bt_register_toLogin:
                Intent intentLogin = new Intent(this,LoginActivity.class);
                startActivity(intentLogin);
                break;
        }
    }
}