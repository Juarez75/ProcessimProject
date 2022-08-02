package be.heh.std.plc;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import be.heh.std.imported.simaticS7.S7;
import be.heh.std.imported.simaticS7.S7Client;

public class WritePillsS7 {

        private int databloc;
        private AtomicBoolean isRunning = new AtomicBoolean(false);
        private Thread writeThread;
        private AutomateS7 plcS7;
        private S7Client comS7;
        private String[] parConnexion = new String[10];
        private byte[] byte5 = new byte[16];
        private byte[] byte6 = new byte[16];
        public WritePillsS7(int databloc){
//monAPI = new AutomateS7();
            this.databloc = databloc;
            comS7 = new S7Client();
            plcS7 = new AutomateS7();
            writeThread = new Thread(plcS7);
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
    public void setWriteBool(int bit, boolean whichByte, boolean value){
        if(whichByte){
            S7.SetBitAt(byte5, 0, bit, value);
        }else{
            S7.SetBitAt(byte6,0,bit,value);
        }



    }


    private class AutomateS7 implements Runnable{
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0],
                        Integer.valueOf(parConnexion[1]),Integer.valueOf(parConnexion[2]));
                while(isRunning.get() && (res.equals(0))){
                    comS7.WriteArea(S7.S7AreaDB,databloc,5,8,byte5);
                    comS7.WriteArea(S7.S7AreaDB,databloc,6,8,byte6);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
