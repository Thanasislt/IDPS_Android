package securityteam.ece.uowm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    WeakReference<Activity> activityreference ;
    Button start_button;
    Button stop_button;
    TextView captureCount;
    private Capture_Root capture_root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityreference = new WeakReference<Activity>(this);

        final CommandExecutor exec = new CommandExecutor();
        capture_root = new Capture_Root(activityreference);

        start_button = findViewById(R.id.start_tcpdump);
        stop_button = findViewById(R.id.stop_tcpdump);
        captureCount = ((TextView)findViewById(R.id.textView));
        @SuppressLint("StaticFieldLeak") final AsyncTask a = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (true){
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
                captureCount.setText(String.valueOf(values[0]));
            }
        };
        a.execute();


        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Runtime.getRuntime().exec("su");
                            activityreference.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    start_button.setEnabled(false);
                                    stop_button.setEnabled(true);
                                }
                            });
                            UpdateSettings();
                            Log.e("SUCHECK","SUCCESS. Executing: " + capture_root.getCaptureCommand());
                            exec.executeCommand(capture_root.getCaptureCommand());

                        } catch (IOException e) {
                            Log.e("SUCHECK","FAIL");
                            e.printStackTrace();
                            activityreference.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activityreference.get(),"Root access not detected or not allowed.",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                t.start();

            }
        });
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                stop_button.setEnabled(false);
                start_button.setEnabled(true);
                exec.stopRunningExecution();

            }
        });
    }

    void UpdateSettings(){
        RadioGroup rg =  findViewById(R.id.radioGroup);
        RadioButton rb = findViewById(rg.getCheckedRadioButtonId());
        String a = (String) rb.getText();


        String options = "";

        if(a.equals("ALL")){
            options = options + "";
        }
        else if(a.equals("TCP")){
            options+="tcp";
        }
        else if(a.equals("UDP")){
            options+="udp";
        }

        EditText ed =  findViewById(R.id.host_text);

        if(ed.getText().toString().matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")){
            options+=" host " +ed.getText().toString();
        }


        capture_root.updateCaptureCommand(options);

    }









}
