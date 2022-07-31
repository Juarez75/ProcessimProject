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
import be.heh.std.customView.RobotView;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.Role;

public class ListPlcActivity extends AppCompatActivity {

    private ScrollView scv;
    private LinearLayout lnlRobots;
    private LinearLayout lnlMessage;
    private ArrayList<PlcConf> robots;
    private Intent intent;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_plc);
        intent = getIntent();
        id = intent.getIntExtra("id", -1);
        scv = findViewById(R.id.scv_main);
        lnlRobots = findViewById(R.id.lnl_main);
        lnlMessage = findViewById(R.id.lnl_main_message);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        robots = new ArrayList<PlcConf>(db.plcConfDao().getAllConfs());

        if(robots.isEmpty()){
            scv.setVisibility(View.GONE);
            lnlMessage.setVisibility(View.VISIBLE);
        }else{
            scv.setVisibility(View.VISIBLE);
            lnlRobots.setVisibility(View.VISIBLE);
            lnlMessage.setVisibility(View.GONE);


            for(PlcConf r: robots){

                lnlRobots.addView(new RobotView(this, r));
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