package be.heh.std.plc;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

import be.heh.std.R;
import be.heh.std.imported.simaticS7.IntByRef;
import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.imported.simaticS7.S7CpuInfo;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadPillsS7 {
    private static final int MESSAGE_PRE_EXECUTE = 1;
    private static final int MESSAGE_PROGRESS_UPDATE = 2;
    private static final int MESSAGE_POST_EXECUTE = 3;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7 ;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];
    private int databloc;
    private Boolean networkStatus = false;
    private int numCPU = 0;
    private Boolean onService = false;
    private Boolean genBottle = false;

    private TextView nmbreBottle;
    private TextView nmbreComprime;
    private Button pills5;
    private Button pills10;
    private Button pills15;


    public ReadPillsS7(int databloc, TextView nmbreBottle, TextView nmbreComprime, Button pills5, Button pills10, Button pills15) {
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        this.databloc = databloc;
        readThread = new Thread(plcS7);
        this.nmbreBottle = nmbreBottle;
        this.nmbreComprime = nmbreComprime;
        this.pills5 = pills5;
        this.pills10 = pills10;
        this.pills15 = pills15;
    }
    public void Stop(){
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String ip, String rack, String slot){

        if(!readThread.isAlive()){
            Log.i("slot", slot);
            param[0]= ip;
            param[1] = rack;
            param[2] = slot;
            readThread.start();
            isRunning.set(true);
        }
    }
    public int getnumCPU(){
        return numCPU;
    }
    public Boolean getNetworkStatus(){
        return networkStatus;
    }
    public Boolean getGenBottle(){return genBottle;}
    public Boolean getOnService(){return onService;}

    private class AutomateS7 implements Runnable{
        @Override
        public void run(){
            try{
                comS7.SetConnectionType(S7.S7_BASIC);
                Log.i("ip", param[0]);
                Log.i("rack", param[1] +"");
                Log.i("slot", param[2]+"");
                Integer res = comS7.ConnectTo(param[0],Integer.valueOf(param[1]),Integer.valueOf(param[2]));
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                if(res.equals(0) && result.equals(0)){
                    networkStatus = true;
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5,8));
                }

                while(isRunning.get() && res.equals(0)){
                        // 1 Sélecteur
                        int retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,0,8,datasPLC);
                        int data = 0;
                        if(retInfo == 0){
                            data = S7.GetBitAt(datasPLC,0,0)?0 : 1;

                        }

                        // 2 Led 5 comprimés
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,4,8,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            if(S7.GetBitAt(datasPLC,0,3))pills5.setBackgroundColor(0xFF537A38);
                            else pills5.setBackgroundColor(0xFF6200EE);
                        }
                        // 3 Led 10 comprimés
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,4,8,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            if(S7.GetBitAt(datasPLC,0,4))pills5.setBackgroundColor(0xFF537A38);
                            else pills5.setBackgroundColor(0xFF6200EE);
                        }
                        // 4 LED 15 comprims
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,4,8,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            if(S7.GetBitAt(datasPLC,0,5))pills5.setBackgroundColor(0xFF537A38);
                            else pills5.setBackgroundColor(0xFF6200EE);
                        }
                        // 5 Générateur d'objet
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,1,8,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            data = S7.GetBitAt(datasPLC,0,3)?0 : 1;

                        }
                        // 6 Nmbre de comprimé
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,15,2,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            data = S7.GetWordAt(datasPLC,0);
                            nmbreComprime.setText("Nmbre de comprimés :" + data);

                        }
                        // 7 Nmbre de bouteille
                        retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,16,2,datasPLC);
                        data = 0;
                        if(retInfo == 0){
                            data = S7.GetWordAt(datasPLC,0);
                            nmbreBottle.setText("Nmbre de bouteille :" + data);

                        }
                }
            }catch (Exception e){

            }
        }
    }
}




