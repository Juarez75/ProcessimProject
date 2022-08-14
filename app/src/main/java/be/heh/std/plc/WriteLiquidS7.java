package be.heh.std.plc;

import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;

public class WriteLiquidS7 {
    private int databloc;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread writeThread;
    private AutomateS7 plcS7;
    private S7Client comS7;
    private String[] parConnexion = new String[10];
    private byte[] byte2 = new byte[16];
    private byte[] byte26 = new byte[16];
    private byte[] byte28 = new byte[16];

    private TextView msgUser;

    public WriteLiquidS7(int databloc, TextView msgUser){
        this.databloc = databloc;
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
        this.msgUser = msgUser;
    }
    public void Stop(){
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }
    public void Start(String ip, String rack, String slot){
        if (!writeThread.isAlive()) {
            parConnexion[0] = ip;
            parConnexion[1] = rack;
            parConnexion[2] = slot;
            writeThread.start();
            isRunning.set(true);
        }
    }
    public void setWriteBool(int bit, int whichByte, int value){
        switch (whichByte){
            case 2:
                S7.SetBitAt(byte2, 0, bit, (value == 1));
            break;
            case 26:
                S7.SetWordAt(byte26, 0, value);
            break;
            case 28:
                S7.SetWordAt(byte28, 0, value);
                break;
        }
    }
    private class AutomateS7 implements Runnable{
        @Override
        public void run() {
            try {
                msgUser.setText("L'écriture n'est pas encore disponible");
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0], Integer.valueOf(parConnexion[1]),Integer.valueOf(parConnexion[2]));
                while(!res.equals(0))
                {
                    Thread.sleep(100);
                    comS7.SetConnectionType(S7.S7_BASIC);
                    res = comS7.ConnectTo(parConnexion[0], Integer.valueOf(parConnexion[1]),Integer.valueOf(parConnexion[2]));
                }
                msgUser.setText("L'écriture est disponible");
                while(isRunning.get() && (res.equals(0))){
                    Log.i("WriteLiquid","Running");
                    comS7.WriteArea(S7.S7AreaDB,databloc,2,8,byte2);
                    comS7.WriteArea(S7.S7AreaDB,databloc,26,2,byte26);
                    comS7.WriteArea(S7.S7AreaDB,databloc,28,2,byte28);
                }
            }
            catch (Exception e){
                Log.e("WriteLiquid",e.toString());
            }
        }
    }
}
