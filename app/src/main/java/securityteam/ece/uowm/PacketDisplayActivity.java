package securityteam.ece.uowm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.checkerframework.checker.units.UnitsTools.s;

public class PacketDisplayActivity extends AppCompatActivity {

    List<Packet> packetList = new ArrayList<Packet>();

    String command = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_display3);
        command += "su -c " + Capture_Root.tcpdump.getAbsolutePath() + " -qns 0 -r " + Capture_Root.captureLocation + "/capture.pcap";

        new reader().execute();


    }

    private class reader extends AsyncTask<Object, Object, Object> {
        reader() {
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String[] lines = null;
            try {
                lines = ReadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (lines != null) {
                for (int i = 1; i < lines.length; i++) {
                    String[] data = lines[i].split(" ");
                    packetList.add(new Packet(data[5], data[2], data[4]));
                    publishProgress((int)(i/(float)lines.length)*100);

                }

            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("Downloaded " + result + " bytes");
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
}


