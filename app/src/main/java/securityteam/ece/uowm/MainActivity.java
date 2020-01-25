package securityteam.ece.uowm;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    WeakReference<Activity> activityreference ;
    Button start_button;
    Button stop_button;
    TextView captureCount;
    Capture_Root captureroot;
    tcpdumpExecutor exec = new tcpdumpExecutor();
    Capture_Root capture_root;
    PieChart pieChart ;
    final Object lock = new Object();
    volatile  List<PieEntry> entries = new ArrayList<>();
    AsyncTask a;
//    ReadWriteLock lock = new ReadWriteLock();



    private class test extends AsyncTask<Object,Object,Object>{
        float calculatePercentage ( int count, int total){
            if (total == 0) return 0.0f;
            return (((float) count / total) * 100.0f);
        }

        @Override
        protected Object doInBackground (Object[]objects){
            boolean f;
            while (true) {
                if (Thread.currentThread().isInterrupted()) break;
                if (this.isCancelled()) break;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < exec.protocolCount.length; i++) {
                    if (exec.protocolCount[i] > 0 && exec.getPacketCount() > 0) {
                        PieEntry tmp = new PieEntry(calculatePercentage(exec.protocolCount[i], exec.getPacketCount()), exec.protocolNames[i]);
//                        Log.d("NEWENTRY", "CREATED ENTRY " + tmp.getLabel() + "PERCENTAGE " + tmp.getValue() + " TOTAL PACKETS " + exec.getPacketCount());
                        f = false;

                        synchronized (lock) {
                            for (PieEntry entry : entries) {
                                if (entry.getLabel().equals(tmp.getLabel())) {
                                    entries.set(entries.indexOf(entry), tmp);
                                    f = true;
                                }
                            }
                            if (!f) entries.add(tmp);
                        }
                    }

                }


                publishProgress(exec.getPacketCount());

            }
            return null;
        }

        @Override
        protected void onProgressUpdate (Object[]values){
            super.onProgressUpdate(values);
            captureCount.setText(String.valueOf((int) values[0]));

            PieChart pieChart = findViewById(R.id.pieChart);
            PieDataSet pieDataSet;
//                lock.readLock().lock();

            synchronized (lock) {
                pieDataSet = new PieDataSet(entries, "");
                PieData data = new PieData(pieDataSet);
                pieChart.setData(data);


                pieDataSet.setSliceSpace(0);
                pieDataSet.setColors(ContextCompat.getColor(activityreference.get().getApplicationContext(), R.color.dark_blue),
                        ContextCompat.getColor(activityreference.get().getApplicationContext(), R.color.dark_green),
                        ContextCompat.getColor(activityreference.get().getApplicationContext(), R.color.dark_orange),
                        ContextCompat.getColor(activityreference.get().getApplicationContext(), R.color.dark_red));
                pieChart.setCenterText("Captured Packets");
                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(50);
                pieChart.setUsePercentValues(true);
                Legend legend = pieChart.getLegend();
                legend.setEnabled(false);
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                pieChart.getDescription().setEnabled(false);

                pieChart.setTransparentCircleRadius(0);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate(); // refresh
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityreference = new WeakReference<Activity>(this);
        capture_root = new Capture_Root(activityreference);
        captureroot = capture_root;
        start_button = findViewById(R.id.start_tcpdump);
        stop_button = findViewById(R.id.stop_tcpdump);
        captureCount = ((TextView) findViewById(R.id.textView));
        pieChart = findViewById(R.id.pieChart);


        final PieChart pieChart = findViewById(R.id.pieChart);
        PieDataSet pieDataSet;
        synchronized (lock) {
            pieDataSet = new PieDataSet(entries, "");
            PieData data = new PieData(pieDataSet);
            pieChart.setData(data);
        }

        pieDataSet.setSliceSpace(5);
        pieDataSet.setColors(new int[]{R.color.green, R.color.orange, R.color.red, R.color.blue}, this);
        pieChart.setCenterText("Captured Packets");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        pieChart.getDescription().setEnabled(false);

        pieChart.setTransparentCircleRadius(0);

        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
        pieChart.invalidate(); // refresh




        CheckBox all=findViewById(R.id.checkBoxAll);

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CheckBox all,tcp,udp,icmp;
                tcp=findViewById((R.id.checkBoxTcp));
                udp=findViewById((R.id.checkBoxUdp));
                icmp=findViewById((R.id.checkBoxIcmp));

                if(b==true){
                    tcp.setChecked(false);
                    tcp.setEnabled(false);

                    udp.setChecked(false);
                    udp.setEnabled(false);

                    icmp.setChecked(false);
                    icmp.setEnabled(false);
                }
                else{

                    tcp.setEnabled(true);


                    udp.setEnabled(true);


                    icmp.setEnabled(true);
                }


            }
        });






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

                a = new test().execute();
            }
        });
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                stop_button.setEnabled(false);
                start_button.setEnabled(true);
                exec.stopRunningExecution();
                a.cancel(true);


            }
        });
    }

    void UpdateSettings(){

        CheckBox all,tcp,udp,icmp;
        all=findViewById(R.id.checkBoxAll);
        tcp=findViewById((R.id.checkBoxTcp));
        udp=findViewById((R.id.checkBoxUdp));
        icmp=findViewById((R.id.checkBoxIcmp));

        int countChecks=0;

        String options = "";


        if(tcp.isChecked()){
            options+=" tcp";
            countChecks++;
        }
        if(udp.isChecked()){
            if(countChecks>0)
                options+=" or udp";
            else {
                options += " udp";
            }
            countChecks++;
        }
        if(icmp.isChecked()){

            if(countChecks>0)
                options+=" or icmp";
            else {
                options+=" icmp";
            }
            countChecks++;
        }

        EditText ed =  findViewById(R.id.host_text);

        if(ed.getText().toString().matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")){
            options+=" host " +ed.getText().toString();
        }

        if(captureroot!=null)
            captureroot.updateCaptureCommand(options);

    }









}
