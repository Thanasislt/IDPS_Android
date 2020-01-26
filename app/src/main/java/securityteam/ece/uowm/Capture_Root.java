package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class Capture_Root {
    String captureLocation;
    String captureCommand;
    File tcpdump;


    public Capture_Root(WeakReference<Activity> activityReference){
        try {
            captureLocation = Environment.getExternalStorageDirectory().getAbsolutePath();

            File file = activityReference.get().getFileStreamPath ("tcpdump");
            if (!file.exists()){
                Log.d("TCPDUMP_LOCATION","TCPDUMP binary not found, installing to " + file.getAbsolutePath());
                InputStream ins = activityReference.get().getAssets().open("tcpdump");
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = activityReference.get().openFileOutput("tcpdump", Context.MODE_PRIVATE);
                fos.write(buffer);
                fos.close();
                file = activityReference.get().getFileStreamPath ("tcpdump");
                file.setExecutable(true);

            }
            else{
                Log.d("TCPDUMP_LOCATION","TCPDUMP found " + file.getAbsolutePath());

            }
            tcpdump = file;

            this.captureCommand = "su -c " + file.getAbsolutePath() + "  -i any -Ul --immediate-mode -w - | tee "+ captureLocation+ "/capture.pcap |" + file.getAbsolutePath()+" -Utvvvnnl --immediate-mode -r - | grep 'proto'";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCaptureCommand(String a){


        this.captureCommand = "su -c " + tcpdump.getAbsolutePath() + "  -i any " + a + " -Ul --immediate-mode -w - | tee "+ captureLocation+ "/capture.pcap |" + tcpdump.getAbsolutePath()+" -Utvvvnnl --immediate-mode -r - | grep 'proto'";
    }

    public String getCaptureCommand() {
        return captureCommand;
    }

    public String getCaptureLocation() {
        return captureLocation;
    }

    public void setCaptureCommand(String captureCommand) {
        this.captureCommand = captureCommand;
    }

    public void setBinaryFile(File binaryLocation) {
        this.tcpdump = binaryLocation;
    }

    public void setCaptureLocation(String captureLocation) {
        this.captureLocation = captureLocation;
    }

    public File getBinaryFile() {
        return tcpdump;
    }
}
