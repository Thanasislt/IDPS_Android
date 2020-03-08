package securityteam.ece.uowm;

import android.app.Activity;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;

public class Capture_Root {
    static String captureLocation;
    String captureCommand;
    static File tcpdump;



    public Capture_Root(WeakReference<Activity> activityReference){
        tcpdump = new binaryHelper().getBinaryFile(activityReference);
        try{
            if (tcpdump!=null) {
//                captureLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
                captureLocation = activityReference.get().getExternalFilesDir(null).getAbsolutePath();
                this.captureCommand =
                        "su -c " + tcpdump.getAbsolutePath() + "  -i any -Ul --immediate-mode -w - | tee " + captureLocation + "/capture.pcap |" + tcpdump.getAbsolutePath() + " -Utvvvnnl --immediate-mode -r - | grep 'proto'";
            }
            else{
                Toast.makeText(activityReference.get(),"Capture_Root: Binary file is null! ",Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){

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
