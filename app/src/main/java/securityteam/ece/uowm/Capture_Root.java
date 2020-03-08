package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public  class Capture_Root {
    static String captureLocation;
    static String captureCommand;
    static File tcpdump;
    static boolean captureIsActive = false;
    static Process captureProcess = null;
    static int ProccessID = -1;
    static AsyncTask captureTask = null;


    public Capture_Root(WeakReference<Activity> activityReference){
        if (tcpdump == null ) tcpdump = new binaryHelper().getBinaryFile(activityReference);
        try{
            if (tcpdump!=null) {
                captureLocation = activityReference.get().getExternalFilesDir(null).getAbsolutePath();
            }
            else{
                Toast.makeText(activityReference.get(),"Capture_Root: Binary file is null! ",Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){

        }

    }
    public static void Capture(final Context context,final String command,final int captureDurationSeconds)  {
        captureTask= new AsyncTask<Void, Void, Void>()  {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    captureLocation = context.getExternalFilesDir(null).getAbsolutePath();
                    if(!captureIsActive && captureProcess ==null){
                        Log.d("Capture","Starting");
                        captureIsActive = true;

                        System.out.println(command);
                        captureProcess= Runtime.getRuntime().exec(command);
                        captureProcess.waitFor();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Capture","IOExpection");
                }
                catch (IllegalThreadStateException e) {
//                    e.printStackTrace();
                    Log.d("Capture", "Still alive");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                Log.d("Capture","Ended");
                Log.d("Capture","Finished");
                captureIsActive = false;
                captureProcess = null;
            }
        }.execute();

        TimerTask timeout = new TimerTask() {
            @Override
            public void run() {
                    StopCapture();
            }
        };
        Timer t = new Timer();
        t.schedule(timeout,captureDurationSeconds*1000L);




    }
    public static void StopCapture()  {
        if(captureProcess!=null && captureIsActive ){
            try{
                Log.d("Capture","Trying to kill...");
                Process p =Runtime.getRuntime().exec("su -c killall tcpdump");
                captureIsActive= false;
                Log.d("Capture","Killed");
                captureProcess = null;
            } catch (IllegalThreadStateException e){
                Log.d("Capture","Still alive");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            Log.d("Capture","Can't Stop: Not Running.");
        }
    }

//    public void updateCaptureCommand(String a){
//
//
//        this.captureCommand = "su -c " + tcpdump.getAbsolutePath() + "  -i any " + a + " -Ul --immediate-mode -w - | tee "+ captureLocation+ "/capture.pcap |" + tcpdump.getAbsolutePath()+" -Utvvvnnl --immediate-mode -r - | grep 'proto'";
//    }
//
//    public String getCaptureCommand() {
//        return captureCommand;
//    }
//
//    public String getCaptureLocation() {
//        return captureLocation;
//    }
//
//    public void setCaptureCommand(String captureCommand) {
//        this.captureCommand = captureCommand;
//    }
//
//    public void setBinaryFile(File binaryLocation) {
//        this.tcpdump = binaryLocation;
//    }
//
//    public void setCaptureLocation(String captureLocation) {
//        this.captureLocation = captureLocation;
//    }
//
//    public File getBinaryFile() {
//        return tcpdump;
//    }
}
