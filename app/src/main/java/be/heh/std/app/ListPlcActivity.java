package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import be.heh.std.R;
import be.heh.std.customView.PlcView;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.Role;
import be.heh.std.database.User;

public class ListPlcActivity extends AppCompatActivity {

    private ScrollView scv;
    private LinearLayout layoutPlc;
    private LinearLayout layoutMessage;
    private ArrayList<PlcConf> plc;
    private Intent intent;
    private int id;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_plc);
        intent = getIntent();
        id = intent.getIntExtra("id", -1);
        scv = findViewById(R.id.scv_main);
        layoutPlc = findViewById(R.id.lnl_main);
        layoutMessage = findViewById(R.id.lnl_main_message);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        user = db.userDao().getUserById(id);
        plc = new ArrayList<PlcConf>(db.plcConfDao().getAllConfs());

        if(plc.isEmpty()){
            scv.setVisibility(View.GONE);
            layoutMessage.setVisibility(View.VISIBLE);
        }else{
            scv.setVisibility(View.VISIBLE);
            layoutPlc.setVisibility(View.VISIBLE);
            layoutMessage.setVisibility(View.GONE);


            for(PlcConf r: plc){

                layoutPlc.addView(new PlcView(this, r,id,user.role));
            }
        }
    }

    public void onListPlcManager (View v) {
        switch (v.getId()) {
            case R.id.bt_list_newPlc:
                intent = new Intent(this, NewPlcActivity.class);
                startActivity(intent);
                break;
        }

    }
}