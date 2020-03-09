package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class binaryHelper {

    static File  getBinaryFile(WeakReference<Activity> activityReference){
        if (installBinary(activityReference) != -1){
            return activityReference.get().getFileStreamPath("tcpdump");
        }
        else
            return null;

    }
    /**
     *
     * @param activityReference A WeakReference to the calling activity.
     * @return Returns true if  the tcpdump binary is found in the App's folder.
     */
    static boolean isBinaryInstalled(WeakReference<Activity> activityReference){
        return activityReference.get().getFileStreamPath ("tcpdump").exists();
    }

    /**
     *
     * @param activityReference A WeakReference to the calling activity.
     * @return Returns:
     * -1) If an exception occurrs TODO add more detailed exception handle
     * 1) On successful binary installation
     * 2) If binary already exists so  installation is unnecessary
     *
     */
    static int installBinary(WeakReference<Activity> activityReference){
        File file = activityReference.get().getFileStreamPath("tcpdump");
        try {
            if (!isBinaryInstalled(activityReference)) {
                Log.d("TCPDUMP_LOCATION", "TCPDUMP binary not found, installing to " + file.getAbsolutePath());
                InputStream ins = activityReference.get().getAssets().open("tcpdump");
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = activityReference.get().openFileOutput("tcpdump", Context.MODE_PRIVATE);
                fos.write(buffer);
                fos.close();
                file = activityReference.get().getFileStreamPath("tcpdump");
                file.setExecutable(true);
                return 1;
            } else {
                Log.d("TCPDUMP_LOCATION", "TCPDUMP found " + activityReference.get().getFileStreamPath("tcpdump").getAbsolutePath());
                return 2;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

}
