package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public  class Capture_Root {
    static String captureLocation;
    static String captureCommand;
    static File tcpdump;
    static boolean captureIsActive = false;
    static Process captureProcess = null;
    static CountDownTimer cdt;
    static AsyncTask captureTask = null;
    private static WeakReference<Activity> weakReference;

    public Capture_Root(WeakReference<Activity> activityReference){
        weakReference = activityReference;
        if (tcpdump == null ) tcpdump = new binaryHelper().getBinaryFile(activityReference);
        try{
            if (tcpdump!=null) {
                captureLocation = weakReference.get().getExternalFilesDir(null).getAbsolutePath();
            }
            else{
                Toast.makeText(weakReference.get(),"Capture_Root: Binary file is null! ",Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){

        }

    }
    public static void Capture(final Context context,final String command,final int captureDurationSeconds)  {
        NumberPickerView npvH,npvM,npvS;
        npvH = weakReference.get().findViewById(R.id.pickerHour);
        npvM = weakReference.get().findViewById(R.id.pickerMinute);
        npvS = weakReference.get().findViewById(R.id.pickerSecond);
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
                    Log.d("Capture","IOExpection");
                }
                catch (IllegalThreadStateException e) {
                    Log.d("Capture", "Still alive");
                } catch (InterruptedException e) {
                    Log.d("Capture", "Interrupted");
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

         cdt = new CountDownTimer(captureDurationSeconds * 1000L, 1000) {


            public void onTick(long millisUntilFinished) {
                if (weakReference != null) {
                    long millis = millisUntilFinished % 1000;
                    long second = (millisUntilFinished / 1000) % 60;
                    long minute = (millisUntilFinished / (1000 * 60)) % 60;
                    long hour = (millisUntilFinished / (1000 * 60 * 60));

                    npvH.smoothScrollToValue((int)hour);
                    npvM.smoothScrollToValue((int)minute);
                    npvS.smoothScrollToValue((int) second);
                }
            }

            public void onFinish() {
                StopCapture();
                if (weakReference != null) {
                    npvH.smoothScrollToValue(0);
                    npvM.smoothScrollToValue(0);
                    npvS.smoothScrollToValue(0);
                }
            }

        };
        cdt.start();




    }
    public static void StopCapture()  {
        if (weakReference==null) return;
        NumberPickerView npvH,npvM,npvS;
        npvH = weakReference.get().findViewById(R.id.pickerHour);
        npvM = weakReference.get().findViewById(R.id.pickerMinute);
        npvS = weakReference.get().findViewById(R.id.pickerSecond);
        if(captureProcess!=null && captureIsActive ){
            try{
                Log.d("Capture","Trying to kill...");
                Process p =Runtime.getRuntime().exec("su -c killall tcpdump");
                captureIsActive= false;
                Log.d("Capture","Killed");
                captureProcess = null;
                cdt.cancel();
                npvH.smoothScrollToValue(0);
                npvM.smoothScrollToValue(0);
                npvS.smoothScrollToValue(0);
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

    public static void CleanUp(){
        StopCapture();
        if (captureTask!=null)captureTask.cancel(true);
        captureLocation=null;
        captureCommand=null;

        captureTask = null;


    }

}
