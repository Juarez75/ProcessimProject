package be.heh.std.plc;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadLiquidS7 {
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7 ;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];
    private int databloc;

    private Boolean networkStatus = false;
    private Boolean vanne1;
    private Boolean vanne2;
    private Boolean vanne3;
    private Boolean vanne4;
    private Boolean manuelAuto;
    private Boolean local;
    private TextView liquidLevel;
    private TextView pilotageVanne;
    private TextView consigneManuelle;
    private TextView consigneAuto;
    private TextView cpu;
    private TextView networkStatusTv;


    public ReadLiquidS7(int databloc, TextView liquidLevel, TextView pilotageVanne, TextView consigneManuelle, TextView consigneAuto,TextView cpu, TextView networkStatusTv){
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        this.databloc = databloc;
        readThread = new Thread(plcS7);

        this.liquidLevel = liquidLevel;
        this.pilotageVanne = pilotageVanne;
        this.consigneManuelle = consigneManuelle;
        this.consigneAuto = consigneAuto;
        this.cpu = cpu;
        this.networkStatusTv = networkStatusTv;
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
    public Boolean getNetworkStatus(){return networkStatus;}
    public Boolean getVanne1(){return vanne1;}
    public Boolean getVanne2(){return vanne2;}
    public Boolean getVanne3(){return vanne3;}
    public Boolean getVanne4(){return vanne4;}
    public Boolean getManuelAuto(){return manuelAuto;}
    public Boolean getLocal(){return local;}

    private class AutomateS7 implements Runnable{
        @Override
        public void run(){
            try{
                networkStatusTv.setText("Network Status : LOADING");
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(param[0],Integer.valueOf(param[1]),Integer.valueOf(param[2]));
                while(!res.equals(0))
                {
                    Thread.sleep(100);
                    comS7.SetConnectionType(S7.S7_BASIC);
                    res = comS7.ConnectTo(param[0],Integer.valueOf(param[1]),Integer.valueOf(param[2]));
                }

                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                int data;
                int retInfo;
                if(res.equals(0) && result.equals(0)){

                    networkStatus = true;
                    networkStatusTv.setText("Network Status : ON");
                    cpu.setText("CPU :"+ Integer.valueOf(orderCode.Code().toString().substring(5,8)));
                    retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,0,1,datasPLC);
                    if(retInfo == 0){
                        //Vanne 1
                        vanne1 = S7.GetBitAt(datasPLC,0,1);
                        //Vanne 2
                        vanne2 = S7.GetBitAt(datasPLC,0,2);
                        //Vanne 3
                        vanne3 = S7.GetBitAt(datasPLC,0,3);
                        //Vanne 4
                        vanne4 = S7.GetBitAt(datasPLC,0,4);
                        //Vanne Manut/Auto
                        manuelAuto = S7.GetBitAt(datasPLC,0,5);
                        //Switch local/distant
                        local=S7.GetBitAt(datasPLC,0,6);
                    }
                }

                while(isRunning.get() && res.equals(0)){
                    Log.i("ReadLiquid","Running");
                    //Niveau de liquide
                    retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,16,2,datasPLC);
                    if(retInfo == 0){
                        data = S7.GetWordAt(datasPLC,0);
                        liquidLevel.setText("Liquid level :"+data);
                    }
                    //Set point
                    retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,18,2,datasPLC);
                    if(retInfo == 0){
                        data = S7.GetWordAt(datasPLC,0);
                        consigneAuto.setText("Conigne auto :"+data);
                    }
                    //Consigne manuelle
                    retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,20,2,datasPLC);
                    if(retInfo == 0){
                        data = S7.GetWordAt(datasPLC,0);
                        consigneManuelle.setText("Consigne manuelle :" +data);
                    }
                    //Pilotage vanne
                    retInfo = comS7.ReadArea(S7.S7AreaDB,databloc,22,2,datasPLC);
                    if(retInfo == 0){
                        data = S7.GetWordAt(datasPLC,0);
                        pilotageVanne.setText("Pilotage vanne :"+data);
                    }
                }
            }catch (Exception e){
                Log.e("ReadLiquid",e.toString());
            }

        }
    }
}
