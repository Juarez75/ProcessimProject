package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import be.heh.std.GlobaleVariable;
import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.Role;
import be.heh.std.database.User;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.plc.ReadPillsS7;
import be.heh.std.plc.WritePillsS7;

public class PillsActivity extends AppCompatActivity {

    private int id_plc;
    private PlcConf plcConf;
    private NetworkInfo network;
    private ReadPillsS7 readS7;
    private WritePillsS7 writeS7;
    private ConnectivityManager connexStatus;
    private Intent intent;
    private Role role;

    private TextView ip;
    private TextView rack;
    private TextView slot;
    private TextView datablock;
    private TextView cpu;
    private TextView networkStatus;
    private TextView nmbreBottle;
    private TextView nmbreComprime;
    private TextView msgUser;
    private Switch genBottle;
    private Switch onService;
    private Switch resetBottle;
    private Switch local;
    private Button pills5;
    private Button pills10;
    private Button pills15;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        intent = getIntent();
        id_plc = intent.getIntExtra("id",-1);
        role = ((GlobaleVariable) this.getApplication()).getRole_user();
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        plcConf = db.plcConfDao().getConfById(id_plc);
        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();


        setInfo();
        if(network == null ){
            Toast.makeText(getApplicationContext(),"Pas de connexion Internet",Toast.LENGTH_LONG).show();
            this.onBackPressed();
        }
            try{

            Toast.makeText(this,network.getTypeName(),Toast.LENGTH_LONG).show();
            readS7 = new ReadPillsS7(Integer.valueOf(plcConf.data_block),nmbreBottle,nmbreComprime,pills5,pills10,pills15,genBottle,onService,resetBottle,local,cpu,networkStatus);
            readS7.Start(plcConf.ip, plcConf.rack, plcConf.slot);
            if(role != Role.BASIC){
                writeS7 = new WritePillsS7(Integer.valueOf(plcConf.data_block), msgUser);
                writeS7.Start(plcConf.ip,plcConf.rack, plcConf.slot);
            }
            }catch(Exception e){
                Log.e("Error", e.getMessage());
            }
    }
    @Override
    public void onBackPressed() {
        readS7.Stop();
        if(role != Role.BASIC) writeS7.Stop();
        finish();
    }
    public void onPillsClick(View v){
        switch (v.getId()){
            case R.id.bt_pills_5:
                writeS7.setWriteBool(2,true,true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(5000);
                            writeS7.setWriteBool(2,true,false);
                        }catch(Exception e){
                            Log.e("Error",e.getMessage());
                        }
                    }
                }).start();
                break;
            case R.id.bt_pills_10:
                writeS7.setWriteBool(3,true,true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(5000);
                            writeS7.setWriteBool(3,true,false);
                        }catch(Exception e){
                            Log.e("Error",e.getMessage());
                        }
                    }
                }).start();
                break;
            case R.id.bt_pills_15:
                writeS7.setWriteBool(4,true,true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(5000);
                            writeS7.setWriteBool(4,true,false);
                        }catch(Exception e){
                            Log.e("Error",e.getMessage());
                        }
                    }
                }).start();
                break;
            case R.id.sw_pills_local:
                writeS7.setWriteBool(7, false,local.isChecked());
                break;
            case R.id.sw_pills_reset:
                writeS7.setWriteBool(3, false,resetBottle.isChecked());
                break;
            case R.id.sw_pills_genBottle:
                writeS7.setWriteBool(4, false,genBottle.isChecked());
                break;
            case R.id.sw_pills_selec:
                writeS7.setWriteBool(1, true,onService.isChecked());
        }
    }

    private void setInfo(){
        ip = (TextView) findViewById(R.id.tv_pills_ip);
        rack = (TextView) findViewById(R.id.tv_pills_rack);
        slot = (TextView) findViewById(R.id.tv_pills_slot);
        datablock = (TextView) findViewById(R.id.tv_pills_datablock);
        cpu = (TextView) findViewById(R.id.tv_pills_cpu);
        networkStatus = (TextView) findViewById(R.id.tv_pills_network);
        local = (Switch) findViewById(R.id.sw_pills_local);
        resetBottle = (Switch) findViewById(R.id.sw_pills_reset);
        nmbreBottle = (TextView) findViewById(R.id.tv_pills_nmbreBouteille);
        nmbreComprime=(TextView) findViewById(R.id.tv_pills_nmbreComprime);
        genBottle =(Switch) findViewById(R.id.sw_pills_genBottle);
        onService=(Switch) findViewById(R.id.sw_pills_selec);
        pills5=(Button) findViewById(R.id.bt_pills_5);
        pills10=(Button) findViewById(R.id.bt_pills_10);
        pills15=(Button) findViewById(R.id.bt_pills_15);
        networkStatus = (TextView) findViewById(R.id.tv_pills_network);
        local = (Switch) findViewById(R.id.sw_pills_local);
        resetBottle = (Switch) findViewById(R.id.sw_pills_reset);
        cpu = (TextView) findViewById(R.id.tv_pills_cpu);
        msgUser = (TextView) findViewById(R.id.tv_pills_user);

        if(role == Role.BASIC) setBasicUser();
        ip.setText("IP :"+plcConf.ip);
        rack.setText("Rack :"+plcConf.rack);
        slot.setText("Slot :"+plcConf.slot);
        datablock.setText("Datablock :"+plcConf.data_block);
        networkStatus.setText("Network Status : Loading");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    while(!readS7.getNetworkStatus()) Thread.sleep(100);
                    if(readS7.getNetworkStatus()){
                        genBottle.setChecked(readS7.getGenBottle());
                        onService.setChecked(readS7.getOnService());
                        local.setChecked(readS7.getLocal());
                        resetBottle.setChecked(readS7.getResetBottle());
                    }
                }catch (Exception e) {
                    Log.e("error",e.getMessage());
                }
            }
        }).start();

    }

    private void setBasicUser(){
        msgUser.setText("Vous n'avez pas les droits pour interagir");
        genBottle.setClickable(false);
        onService.setClickable(false);
        local.setClickable(false);
        resetBottle.setClickable(false);
        pills5.setClickable(false);
        pills10.setClickable(false);
        pills15.setClickable(false);
    }

}