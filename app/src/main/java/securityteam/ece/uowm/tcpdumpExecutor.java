package securityteam.ece.uowm;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class tcpdumpExecutor {
    private Semaphore outputSem;
    private String output;
    private Semaphore errorSem;
    private String error;
    private Process tcpdumpProcess;
    Thread captureThread;
    private String command="";
    int captureCount =0;
    OutputReader outputReader = new OutputReader();
    ErrorReader errorReader = new ErrorReader();
    String[] protocolNames= {
            "HOPOPT","ICMP","IGMP","GGP","IPv4","ST","TCP","CBT","EGP","IGP","BBN-RCC-MON","NVP-II","PUP","ARGUS","EMCON","XNET",
            "CHAOS","UDP","MUX","DCN-MEAS","HMP","PRM","XNS-IDP","TRUNK-1","TRUNK-2","LEAF-1","LEAF-2","RDP","IRTP","ISO-TP4","NETBLT","MFE-NSP","MERIT-INP","DCCP","3PC","IDPR","XTP","DDP","IDPR-CMTP","TP++","IL","IPv6","SDRP","IPv6-Route","IPv6-Frag","IDRP","RSVP","GRE","DSR","BNA","ESP","AH"
            ,"I-NLSP","SWIPE","NARP","MOBILE","TLSP","SKIP","IPv6-ICMP","IPv6-NoNxt","IPv6-Opts","any host internal protocol","CFTP","any local network","SAT-EXPAK","KRYPTOLAN","RVD","IPPC","SAT-MON","VISA","IPCV","CPNX","CPHB","WSN","PVP","BR-SAT-MON","SUN-ND","WB-MON","WB-EXPAK","ISO-IP","VMTP","SECURE-VMTP","VINES","TTP","IPTM","NSFNET-IGP","DGP","TCF","EIGRP","OSPFIGP","Sprite-RPC"
            ,"LARP","MTP","AX.25","IPIP","MICP","SCC-SP","","","","","","","","","","","","","","","","","","","","","","","",""
            ,"","","","","","","","","","","","","","","","","","","","","","","","","","","",""
            ,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""



    };
    int[] protocolCount = new int[255];




    private class InputWriter extends Thread {
        private String input;

        public InputWriter(String input) {
            this.input = input;
        }

        public void run() {
            PrintWriter pw = new PrintWriter(tcpdumpProcess.getOutputStream());
            pw.println(input);
            pw.flush();
        }
    }


    public int getPacketCount(){
        return captureCount;
    }


    private class OutputReader extends Thread {
        public OutputReader() {
            try {
                outputSem = new Semaphore(1);
                outputSem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                StringBuilder readBuffer = new StringBuilder();
                BufferedReader isr = new BufferedReader(new InputStreamReader(tcpdumpProcess.getInputStream()));

                String buff;
                int index;
                while ((buff = isr.readLine()) != null) {
//                    readBuffer.append(buff);
//                    Log.d("Executor",buff);
//                    if (buff.contains("proto")){
                         index = buff.indexOf("proto");
                        String Protocol_dirty =  buff.substring(index+"proto".length()+1).split(" ")[1];
                        String Protocol = Protocol_dirty.substring(1,Protocol_dirty.indexOf(")"));
                        protocolCount[Integer.valueOf(Protocol)]++;
//                    }
//                    Log.d("TCPDUMP",buff);
                    captureCount++;

                }
                output = readBuffer.toString();
                outputSem.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                outputSem.release();
            }
        }
    }





    private class ErrorReader extends Thread {
        public ErrorReader() {
            try {
                errorSem = new Semaphore(1);
                errorSem.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                StringBuffer readBuffer = new StringBuffer();
                BufferedReader isr = new BufferedReader(new InputStreamReader(tcpdumpProcess
                        .getErrorStream()));
                String buff;
                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                    Log.d("TCPDUMP_ERROR",buff);



                }
                error = readBuffer.toString();
                errorSem.release();

            if (error.length() > 0)
                Log.d("TCPDUMP_ERROR",error);

            } catch (Exception e) {
                e.printStackTrace();
                errorSem.release();
            }
        }
    }

    public tcpdumpExecutor(String command, String input) {
        try {
            tcpdumpProcess = Runtime.getRuntime().exec(makeArray(command));
            new InputWriter(input).start();
            new OutputReader().start();
            new ErrorReader().start();
            tcpdumpProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public tcpdumpExecutor() {
        outputReader = new OutputReader();
        errorReader = new ErrorReader();
        captureCount =0;


    }
    public void executeCommand(String cmd){
        this.command = cmd;
        outputReader = new OutputReader();
        errorReader = new ErrorReader();
        captureCount =0;
        protocolCount = new int[255];
         captureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpdumpProcess = Runtime.getRuntime().exec(makeArray(command));
                    outputReader.start();
                    errorReader.start();

                    tcpdumpProcess.waitFor();
                    tcpdumpProcess.destroy();
                }
                catch(IOException io){
                    Log.e("Starting","IOEXCEPTION -> "+io.getMessage());
                    tcpdumpProcess.destroy();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    tcpdumpProcess.destroy();
                }
            }
        });
         captureThread.run();
    }

    public void stopRunningExecution(){

        captureThread.interrupt();

        try {
            int pid=-1;
            Field f = tcpdumpProcess.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getInt(tcpdumpProcess);
            f.setAccessible(false);
            Log.d("STOP","Stopping tcpdump: "+ "su -c killall -q  -2 tcpdump ");
            Runtime.getRuntime().exec("su -c killall -q  -2 tcpdump ");
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            Log.e("STOP","FAILED TO STOP TCPDUMP");
        }
    }

    public String getOutput() {
        try {
            outputSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String value = output;
        outputSem.release();
        return value;
    }

    public String getError() {
        try {
            errorSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String value = error;
        errorSem.release();
        return value;
    }

    private String[] makeArray(String command) {
        ArrayList<String> commandArray = new ArrayList<String>();
        String buff = "";
        boolean lookForEnd = false;
        for (int i = 0; i < command.length(); i++) {
            if (lookForEnd) {
                if (command.charAt(i) == '\"') {
                    if (buff.length() > 0)
                        commandArray.add(buff);
                    buff = "";
                    lookForEnd = false;
                } else {
                    buff += command.charAt(i);
                }
            } else {
                if (command.charAt(i) == '\"') {
                    lookForEnd = true;
                } else if (command.charAt(i) == ' ') {
                    if (buff.length() > 0)
                        commandArray.add(buff);
                    buff = "";
                } else {
                    buff += command.charAt(i);
                }
            }
        }
        if (buff.length() > 0)
            commandArray.add(buff);

        String[] array = new String[commandArray.size()];
        for (int i = 0; i < commandArray.size(); i++) {
            array[i] = commandArray.get(i);
        }

        return array;
    }
}