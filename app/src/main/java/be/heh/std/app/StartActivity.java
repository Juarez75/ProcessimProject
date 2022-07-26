package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import be.heh.std.R;
import be.heh.std.database.AppDatabase;

public class StartActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        intent = new Intent(this, LoginActivity.class);
        try{
            AppDatabase db =AppDatabase.getInstance(getApplicationContext());
            Boolean isDatabaseEmpty = (db.userDao().getCountOfUsers() == 0);

            if(isDatabaseEmpty){
                intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("first_time", true);
            }
            startActivity(intent);
            finish();
        }catch(Exception e){
            finish();
        }
    }
}