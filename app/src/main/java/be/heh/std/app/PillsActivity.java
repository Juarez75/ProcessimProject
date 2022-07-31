package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.plc.ReadTaskS7;

public class PillsActivity extends AppCompatActivity {

    private int id_plc;
    private PlcConf plcConf;
    private NetworkInfo network;
    private ReadTaskS7 readS7;
    private ConnectivityManager connexStatus;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        intent = getIntent();
        id_plc = intent.getIntExtra("id",-1);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        plcConf = db.plcConfDao().getConfById(id_plc);
        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();

        if(network != null && network.isConnectedOrConnecting())
        {
            Toast.makeText(this,network.getTypeName(),Toast.LENGTH_LONG).show();
            readS7 = new ReadTaskS7();
            readS7.Start(plcConf.ip, plcConf.rack, plcConf.slot);
            Log.i("slot", plcConf.slot);
        }
    }
}