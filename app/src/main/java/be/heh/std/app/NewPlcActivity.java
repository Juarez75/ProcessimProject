package be.heh.std.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import be.heh.std.R;
import be.heh.std.database.AppDatabase;
import be.heh.std.database.PlcConf;
import be.heh.std.database.PlcType;
import be.heh.std.database.User;

public class NewPlcActivity extends AppCompatActivity {

   private EditText ip;
    private EditText rack;
    private EditText slot;
    private EditText datablock;
    private Spinner type;
    private Intent intent;
    private ArrayList<View> emptyInput;
    private TextView error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plc);

        ip = (EditText) findViewById(R.id.et_newlpc_ip);
        rack = (EditText) findViewById(R.id.et_newlpc_rack);
        slot = (EditText) findViewById(R.id.et_newlpc_slot);
        datablock = (EditText) findViewById(R.id.et_newplc_datablock);
        error_msg = (TextView) findViewById(R.id.error_msg);
        type=(Spinner) findViewById(R.id.addPlc_type);


        emptyInput = new ArrayList<View>();

        emptyInput.addAll(Arrays.asList(ip,rack,slot,datablock));
        type.setAdapter(new ArrayAdapter<PlcType>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, PlcType.values()));

    }

   public void onNewPlcClickManager(View v){
        switch (v.getId()){
            case R.id.bt_newPlc_cancel:
                intent = new Intent(this,ListPlcActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.bt_newPlc_add:
                addPlc(v);
                break;
        }
    }

    private void addPlc(View v){
    try {
        if (!Patterns.IP_ADDRESS.matcher(ip.getText()).matches())
            throw new Exception(getString(R.string.bad_ip_err, ip.getText()));
        for (View e: emptyInput) {
            if((e instanceof EditText) && (((EditText) e).getText().toString().trim().length() == 0))
                throw new Exception(getString(R.string.empty_err, ((EditText)e).getHint()));
        }
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        PlcConf plcConf = new PlcConf();
        plcConf.ip = ip.getText().toString();
        plcConf.rack = rack.getText().toString();
        plcConf.slot = rack.getText().toString();
        plcConf.data_block = datablock.getText().toString();
        plcConf.type = PlcType.valueOf(type.getSelectedItem().toString());
        db.plcConfDao().addConf(plcConf);
        Toast.makeText(getApplicationContext(),"Plc ajouté !", Toast.LENGTH_LONG).show();

    }catch(Exception e){
        error_msg.setText(e.getMessage());
    }
    }
}