package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import be.heh.std.GlobaleVariable;
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
    private Role role;
    private Button addPlc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_plc);
        intent = getIntent();
        role = ((GlobaleVariable) this.getApplication()).getRole_user();
        scv = findViewById(R.id.scv_main);
        layoutPlc = findViewById(R.id.lnl_main);
        layoutMessage = findViewById(R.id.lnl_main_message);
        addPlc = findViewById(R.id.bt_list_newPlc);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        plc = new ArrayList<PlcConf>(db.plcConfDao().getAllConfs());

        if(role == Role.BASIC) addPlc.setVisibility(View.GONE);
        if(plc.isEmpty()){
            scv.setVisibility(View.GONE);
            layoutMessage.setVisibility(View.VISIBLE);
        }else{
            scv.setVisibility(View.VISIBLE);
            layoutPlc.setVisibility(View.VISIBLE);
            layoutMessage.setVisibility(View.GONE);

            for(PlcConf r: plc){
                layoutPlc.addView(new PlcView(this, r,role));
            }
        }
    }

    public void onListPlcManager (View v) {
        switch (v.getId()) {
            case R.id.bt_list_newPlc:
                intent = new Intent(this, NewPlcActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_listPlc_profile:
                this.onBackPressed();
        }
    }
}