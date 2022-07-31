package be.heh.std.plc;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;
import be.heh.std.imported.simaticS7.S7OrderCode;

public class ReadTaskS7 {
    private static final int MESSAGE_PRE_EXECUTE = 1;
    private static final int MESSAGE_PROGRESS_UPDATE = 2;
    private static final int MESSAGE_POST_EXECUTE = 3;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private AutomateS7 plcS7;
    private Thread readThread;

    private S7Client comS7 ;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];

    public ReadTaskS7() {
        comS7 = new S7Client();
        plcS7 = new AutomateS7();

        readThread = new Thread(plcS7);
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
                int numCPU=-1;
                if(res.equals(0) && result.equals(0)){
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5,8));
                }
                else numCPU = 0;
                Log.i("NumCPU :", numCPU+"");
                Log.i("Connecté", res +"");
            }catch (Exception e){

            }
        }
    }
}




