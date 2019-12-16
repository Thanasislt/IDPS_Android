package securityteam.ece.uowm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String COMMAND = "su -c " + getFileStreamPath ("tcpdump") + " -i any -U -w - | tee "+ Environment.getExternalStorageDirectory().getAbsolutePath()+ "/mypcap.pcap |" + getFileStreamPath ("tcpdump")+" -r -";


        final ExecCommand exec = new ExecCommand();
        try {

            File file = getFileStreamPath ("tcpdump");
            if (!file.exists()){
                Log.d("TCPDUMP_LOCATION","TCPDUMP binary not found, installing to " + file.getAbsolutePath());
                InputStream ins = getAssets().open("tcpdump");
                byte[] buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = openFileOutput("tcpdump", Context.MODE_PRIVATE);
                fos.write(buffer);
                fos.close();
                file = getFileStreamPath ("tcpdump");
                file.setExecutable(true);
            }
            else{
                Log.d("TCPDUMP_LOCATION","TCPDUMP found " + file.getAbsolutePath());


            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        @SuppressLint("StaticFieldLeak") final AsyncTask a = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (true){

//                    onProgressUpdate(exec.getPacketCount());
                    publishProgress(exec.getPacketCount());
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                ((TextView)findViewById(R.id.textView)).setText(String.valueOf(values[0]));
            }
        };
        a.execute();


        findViewById(R.id.start_tcpdump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                findViewById(R.id.stop_tcpdump).setEnabled(true);

//                a.execute();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        exec.startNow(COMMAND);


                    }
                });
                t.start();

            }
        });
        findViewById(R.id.stop_tcpdump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                findViewById(R.id.start_tcpdump).setEnabled(true);
//                a.cancel(true);

                exec.StopExecution();

            }
        });
    }









}
