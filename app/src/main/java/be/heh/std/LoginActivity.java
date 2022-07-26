package be.heh.std;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

    EditText et_login_mail;
    EditText et_login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_login_mail= (EditText) findViewById(R.id.et_login_mail);
        et_login_password=(EditText) findViewById(R.id.et_login_password);
    }

    public void onLoginClickManager(View v){
        switch(v.getId()){
            case R.id.bt_login_toRegister:
                Intent intentRegister = new Intent(this,RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }
}