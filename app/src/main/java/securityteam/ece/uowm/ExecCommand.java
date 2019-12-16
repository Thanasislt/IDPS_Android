package securityteam.ece.uowm;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ExecCommand {
    private Semaphore outputSem;
    private String output;
    private Semaphore errorSem;
    private String error;
    private Process p;
    Thread t;
    private String command="";
    int lineCount=0;
    OutputReader outputReader = new OutputReader();
    ErrorReader errorReader = new ErrorReader();
    private class InputWriter extends Thread {
        private String input;

        public InputWriter(String input) {
            this.input = input;
        }

        public void run() {
            PrintWriter pw = new PrintWriter(p.getOutputStream());
            pw.println(input);
            pw.flush();
        }
    }


    public int getPacketCount(){
        return lineCount;
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
                BufferedReader isr = new BufferedReader(new InputStreamReader(p .getInputStream()));
                String buff;
                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                    Log.d("TCPDUMP",buff);
                    lineCount++;
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
                BufferedReader isr = new BufferedReader(new InputStreamReader(p
                        .getErrorStream()));
                String buff = new String();
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

    public ExecCommand(String command, String input) {
        try {
            p = Runtime.getRuntime().exec(makeArray(command));
            new InputWriter(input).start();
            new OutputReader().start();
            new ErrorReader().start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ExecCommand() {
        outputReader = new OutputReader();
        errorReader = new ErrorReader();
        lineCount=0;


    }
    public void startNow(String command2){
        this.command = command2;
        outputReader = new OutputReader();
        errorReader = new ErrorReader();
        lineCount=0;
         t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p = Runtime.getRuntime().exec(makeArray(command));
                    outputReader.start();
                    errorReader.start();

                    p.waitFor();
                    p.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                    p.destroy();
                }
            }
        });
         t.run();
    }

    public void StopExecution(){

        t.interrupt();

        try {
            int pid=-1;
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getInt(p);
            f.setAccessible(false);
            Runtime.getRuntime().exec("su -c kill -2 " + pid);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
//        if (p!=null){
//           p.destroy();
//
//        }
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