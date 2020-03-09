package securityteam.ece.uowm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PacketDisplayActivity extends AppCompatActivity {

    List<Packet> packetList = new ArrayList<Packet>();

    String command = "";
    private Capture_Root captureroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_display3);
        WeakReference<Activity> activityreference = new WeakReference<Activity>(this);
        Capture_Root capture_root = new Capture_Root(activityreference);
        captureroot = capture_root;



        command += "su -c " + Capture_Root.tcpdump.getAbsolutePath() + " -qns 0 -r " + Capture_Root.captureLocation + "/capture.pcap";


        new reader().execute();


    }

    private class reader extends AsyncTask<Integer, Integer, String> {
        reader() {
        }

        @Override
        protected String doInBackground(Integer[] objects) {
            String[] lines = null;
            try {
                lines = ReadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("msg","Do back ground started");
            if (lines != null) {
                for (int i = 1; i < lines.length; i++) {
                    //Log.d("Line",lines[i]);
                    String[] data = lines[i].split(" ");
                    if(data.length>=6)
                        packetList.add(new Packet(data[5], data[2], data[4]));

                    //publishProgress((int)(i/(float)lines.length)*100);
                    float prog = (i/(float)(lines.length-1))*100;
                    publishProgress((int)prog);

                }

            }
            return "Progress finished successful";
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            setFinish(result);
        }

    }


    private String[] ReadFile() throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(command).getInputStream()).useDelimiter("\\A");
        String[] lines = (s.hasNext() ? s.next() : "").split("\n");
        return lines;
    }

    void setProgressPercent(int prog){
        TextView t = (TextView) findViewById(R.id.textView6);
        t.setText(prog+"%");
    }

    void setFinish(String result){
        TextView t = findViewById(R.id.textView7);
        t.setText(result);
    }
}


