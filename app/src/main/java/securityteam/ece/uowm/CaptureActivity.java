package securityteam.ece.uowm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;


public class CaptureActivity extends AppCompatActivity  {
    WeakReference<Activity> activityreference;
    Button start_button;
    Button stop_button;
    TextView captureCount;
    Capture_Root captureroot;
    tcpdumpExecutor exec = new tcpdumpExecutor();
    Capture_Root capture_root;
    PieChart pieChart;
    final Object lock = new Object();
    volatile List<PieEntry> entries = new ArrayList<>();
    List<Integer> colors = new ArrayList<>();
    private final List<CheckableSpinnerAdapter.SpinnerItem<String>> spinner_items = new ArrayList<>();
    private final Set<String> selected_Interfaces = new HashSet<>();
    private final Set<String> all_Interfaces = new HashSet<>();
    NumberPickerView npvH,npvM,npvS;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        String headerText = "Interfaces";
        final Spinner spinner = findViewById(R.id.my_spinner);
        final int[] captureDuration = {0};
        activityreference = new WeakReference<Activity>(this);
        String[] all_objects = null;
        String[] pickerValues = new String[60];
        new Capture_Root(activityreference);
        for (int i=0;i<60;i++){
            pickerValues[i] = ""+i;
        }

        npvH = findViewById(R.id.pickerHour);
        npvM = findViewById(R.id.pickerMinute);
        npvS = findViewById(R.id.pickerSecond);

        npvH.setDisplayedValues(pickerValues);
        npvH.setMinValue(0);
        npvH.setMaxValue(pickerValues.length-1);


        npvM.setDisplayedValues(pickerValues);
        npvM.setMinValue(0);
        npvM.setMaxValue(pickerValues.length-1);

        npvS.setDisplayedValues(pickerValues);
        npvS.setMinValue(0);
        npvS.setMaxValue(pickerValues.length-1);



        try {
            String[] interfaces = getNetworkInterfaces();
            for (String interfaceLine : interfaces){
                String[] interfaceFields = interfaceLine.split(" ");
                String interfaceName = interfaceFields[0];
                all_Interfaces.add(interfaceName);
                Log.d("INTERFACES","Interfaces: " + interfaceName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String interface_Name : all_Interfaces) {
            spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<>(interface_Name, interface_Name));
        }
        CheckBox checkBoxAll = findViewById(R.id.checkBoxInterfaceAny);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selected_Interfaces.clear();
                }
            }
        });



        CheckableSpinnerAdapter.AdapterCallback callback = new CheckableSpinnerAdapter.AdapterCallback() {
            @Override
            public void getSelectedItems(Set selected_items) {
                Log.d("CheckedInterfaces",""+selected_items.size());
                if(selected_items.size()==0){
                    ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(true);
                }
                else if (selected_items.size() == all_Interfaces.size()){
                    ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(true);
                }
                else ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(false);
            }
        };
        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter<>(this, headerText, spinner_items, selected_Interfaces,callback);
        spinner.setAdapter(adapter);


        findViewById(R.id.start_tcpdump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Capture_Root.captureIsActive) {
                    Log.e("Start","Cannot Start: Already running.");
                    return;
                }
                captureDuration[0] = npvH.getValue()*60*60 + npvM.getValue()*60 + npvS.getValue();
                if (captureDuration[0] == 0){
                    Log.e("Start","Cannot Start: Capture duration is 0.");
                    return;
                }

                Capture_Root.Capture(getApplicationContext(),"su -c "+ binaryHelper.getBinaryFile(activityreference)+" -Ul --immediate-mode " +"-i any"+" -w " +getExternalFilesDir(null).getAbsolutePath()+ "/capture.pcap",captureDuration[0]);

            }
        });
        findViewById(R.id.stop_tcpdump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Capture_Root.StopCapture();
            }
        });


    }
    public static String[] getNetworkInterfaces() throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec("su -c ifconfig -S").getInputStream()).useDelimiter("\\A");
        String[] lines = (s.hasNext() ? s.next() : "").split("\n");


        return lines;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



}
