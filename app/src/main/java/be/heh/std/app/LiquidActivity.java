package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import be.heh.std.GlobaleVariable;
import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.Role;
import be.heh.std.plc.ReadLiquidS7;
import be.heh.std.plc.ReadPillsS7;
import be.heh.std.plc.WriteLiquidS7;
import be.heh.std.plc.WritePillsS7;

public class LiquidActivity extends AppCompatActivity {

    private int id_plc;
    private PlcConf plcConf;
    private NetworkInfo network;
    private ReadLiquidS7 readS7;
    private WriteLiquidS7 writeS7;
    private ConnectivityManager connexStatus;
    private Intent intent;
    private Role role;

    private TextView ip;
    private TextView rack;
    private TextView slot;
    private TextView datablock;
    private TextView liquidLevel;
    private TextView pilotageVanne;
    private TextView consigneManuelle;
    private TextView consigneAu;
    private EditText setManual;
    private TextView error;
    private TextView cpu;
    private TextView networkStatus;
    private Switch vanne1;
    private Switch vanne2;
    private Switch vanne3;
    private Switch vanne4;
    private Switch manuelAuto;
    private Switch local;
    private EditText consigneAuto;
    private TextView msgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquid);

        intent = getIntent();
        id_plc = intent.getIntExtra("id",-1);

        role = ((GlobaleVariable) this.getApplication()).getRole_user();
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        plcConf = db.plcConfDao().getConfById(id_plc);

        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();

        if(network == null ) {
            Toast.makeText(getApplicationContext(),"Pas de connexion Internet",Toast.LENGTH_LONG).show();
            this.onBackPressed();
        }
        setInfo();
            try{
                readS7 = new ReadLiquidS7(Integer.valueOf(plcConf.data_block), liquidLevel, pilotageVanne,consigneManuelle,consigneAu,cpu,networkStatus);
                readS7.Start(plcConf.ip, plcConf.rack, plcConf.slot);
                if(role != Role.BASIC){
                    writeS7 = new WriteLiquidS7(Integer.valueOf(plcConf.data_block), msgUser);
                    writeS7.Start(plcConf.ip, plcConf.rack, plcConf.slot);
                }
            }catch(Exception e) {
                Log.e("LiquidActivity", e.toString());
            }
    }

    @Override
    public void onBackPressed() {
        readS7.Stop();
        if(role != Role.BASIC) writeS7.Stop();
        finish();
    }

    public void onLiquidClick(View v){
        switch (v.getId()){
            case R.id.sw_liquid_vanne1:
                writeS7.setWriteBool(1, 2,vanne1.isChecked()?1:0);
                break;
            case R.id.sw_liquid_vanne2:
                writeS7.setWriteBool(2, 2,vanne2.isChecked()?1:0);
                break;
            case R.id.sw_liquid_vanne3:
                writeS7.setWriteBool(3, 2,vanne3.isChecked()?1:0);
                break;
            case R.id.sw_liquid_vanne4:
                writeS7.setWriteBool(4, 2,vanne4.isChecked()?1:0);
                break;
            case R.id.sw_liquid_manualAuto:
                writeS7.setWriteBool(5, 2,manuelAuto.isChecked()?1:0);
                break;
            case R.id.sw_liquid_local:
                writeS7.setWriteBool(6, 2,local.isChecked()?1:0);
                break;
            case R.id.bt_liquid_setPoint:
                if(Integer.parseInt(consigneAuto.getText().toString()) >1000 || Integer.parseInt(consigneAuto.getText().toString())<0) {
                    error.setText("SetPoint Entre 0 et 1000");
                    break;
                }
                writeS7.setWriteBool(0,26,Integer.parseInt(consigneAuto.getText().toString()));
                break;
            case R.id.bt_liquid_manual:
                if(Integer.parseInt(setManual.getText().toString())>100 || Integer.parseInt(setManual.getText().toString())<0){
                    error.setText("SetManual Entre 0 et 100");
                    break;
                }
                writeS7.setWriteBool(0,28,Integer.parseInt(setManual.getText().toString()));
        }
    }

    private void setInfo(){
        ip = (TextView) findViewById(R.id.tv_liquid_ip);
        rack = (TextView) findViewById(R.id.tv_liquid_rack);
        slot = (TextView) findViewById(R.id.tv_liquid_slot);
        datablock = (TextView) findViewById(R.id.tv_liquid_datablock);
        cpu = (TextView) findViewById(R.id.tv_liquid_cpu);
        networkStatus = (TextView) findViewById(R.id.tv_liquid_network);
        consigneAuto = (EditText) findViewById(R.id.et_liquid_setPoint);
        error = (TextView) findViewById(R.id.error_msg);
        setManual = (EditText) findViewById(R.id.et_liquid_manual);
        liquidLevel = (TextView) findViewById(R.id.tv_liquid_nvLiquide);
        pilotageVanne =(TextView) findViewById(R.id.tv_liquid_pilotageVanne);
        consigneManuelle = (TextView) findViewById(R.id.tv_liquid_consigeMan);
        consigneAu=(TextView) findViewById(R.id.tv_liquid_setPoint);
        vanne1 = (Switch) findViewById(R.id.sw_liquid_vanne1);
        vanne2 = (Switch) findViewById(R.id.sw_liquid_vanne2);
        vanne3 = (Switch) findViewById(R.id.sw_liquid_vanne3);
        vanne4 = (Switch) findViewById(R.id.sw_liquid_vanne4);
        manuelAuto = (Switch) findViewById(R.id.sw_liquid_manualAuto);
        local =(Switch) findViewById(R.id.sw_liquid_local);
        msgUser = (TextView) findViewById(R.id.tv_liquid_user);

        ip.setText("IP :"+plcConf.ip);
        rack.setText("Rack :"+plcConf.rack);
        slot.setText("Slot :"+plcConf.slot);
        datablock.setText("Datablock :"+plcConf.data_block);
        if(role == Role.BASIC) this.setBasicUser();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    while(!readS7.getNetworkStatus() && readS7.getNetworkStatus()!= null) Thread.sleep(100);
                    if(readS7.getNetworkStatus()){
                        vanne1.setChecked(readS7.getVanne1());
                        vanne2.setChecked(readS7.getVanne2());
                        vanne3.setChecked(readS7.getVanne3());
                        vanne4.setChecked(readS7.getVanne4());
                        manuelAuto.setChecked(readS7.getManuelAuto());
                        local.setChecked(readS7.getLocal());
                    }
                }catch (Exception e) {
                    Log.e("LiquidActivity setInfo",e.toString());
                }
            }
        }).start();
    }

    private void setBasicUser(){
        msgUser.setText("Vous n'avez pas les droits pour interargir");
        Button bt1 = (Button) findViewById(R.id.bt_liquid_manual);
        Button bt2 = (Button) findViewById(R.id.bt_liquid_setPoint);
        vanne1.setClickable(false);
        vanne2.setClickable(false);
        vanne3.setClickable(false);
        vanne4.setClickable(false);
        manuelAuto.setClickable(false);
        local.setClickable(false);
        bt1.setClickable(false);
        bt2.setClickable(false);
    }
}