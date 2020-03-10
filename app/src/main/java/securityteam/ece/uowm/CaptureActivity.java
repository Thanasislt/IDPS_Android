package securityteam.ece.uowm;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;


public class CaptureActivity extends AppCompatActivity  {
    WeakReference<Activity> activityreference;
    private final List<CheckableSpinnerAdapter.SpinnerItem<String>> spinner_items = new ArrayList<>();
    private final Set<String> selected_Interfaces = new HashSet<>();
    private final Set<String> all_Interfaces = new HashSet<>();
    private String command;
    NumberPickerView npvH,npvM,npvS;
    private String options;
    private String captureInterface = "any";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        String headerText = "Interfaces";
//        final Spinner spinner = findViewById(R.id.my_spinner);
        final int[] captureDuration = {0};
        activityreference = new WeakReference<>(this);
        String[] all_objects = null;
        String[] pickerValues = new String[60];
        new Capture_Root(activityreference);
        for (int i=0;i<60;i++){
            pickerValues[i] = ""+i;
        }

        command = "su -c "+ binaryHelper.getBinaryFile(activityreference)+" -Ul --immediate-mode "+" -w " +getExternalFilesDir(null).getAbsolutePath()+ "/capture.pcap";

        npvH = findViewById(R.id.pickerHour);
        npvM = findViewById(R.id.pickerMinute);
        npvS = findViewById(R.id.pickerSecond);

        npvH.setDisplayedValues(pickerValues);
        npvH.setMinValue(0);
        npvH.setMaxValue(pickerValues.length-1);
        npvH.setValue((int)npvH.getMaxValue()/2);

        npvM.setDisplayedValues(pickerValues);
        npvM.setMinValue(0);
        npvM.setMaxValue(pickerValues.length-1);
        npvM.setValue((int)npvM.getMaxValue()/2);

        npvS.setDisplayedValues(pickerValues);
        npvS.setMinValue(0);
        npvS.setMaxValue(pickerValues.length-1);
        npvS.setValue((int)npvS.getMaxValue()/2);

        CheckBox all = findViewById(R.id.checkBoxAll);

        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                   CheckBox tcp, udp, icmp, igmp;
                   tcp = findViewById((R.id.checkBoxTcp));
                   udp = findViewById((R.id.checkBoxUdp));
                   icmp = findViewById((R.id.checkBoxIcmp));
                   igmp = findViewById((R.id.checkBoxIgmp));

                   if (b == true) {
                       icmp.setChecked(false);
                       icmp.setEnabled(false);

                       igmp.setEnabled(false);
                       igmp.setChecked(false);

                       tcp.setChecked(false);
                       tcp.setEnabled(false);

                       udp.setChecked(false);
                       udp.setEnabled(false);
                   } else {
                       icmp.setEnabled(true);
                       igmp.setEnabled(true);
                       tcp.setEnabled(true);
                       udp.setEnabled(true);
                   }
               }
           }
            );



        try {
            String[] interfaces = getNetworkInterfaces();
            String interfaceName="";
            for (String interfaceLine : interfaces){
                String[] interfaceFields = interfaceLine.split(" ");
                if (!interfaceFields[0].equals("") && !interfaceFields[0].equals(" ")){
                    interfaceName = interfaceFields[0];
                    all_Interfaces.add(interfaceName);
                    Log.d("INTERFACES","Interfaces: " + interfaceName);
                }
//                else  interfaceName = "";


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        RadioGroup rg = findViewById(R.id.radioGroup_Interfaces);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                captureInterface = ((RadioButton)group.findViewById(group.getCheckedRadioButtonId())).getText().toString();
                UpdateSettings();
            }
        });
        RadioButton rb;
        for(String interface_Name : all_Interfaces) {
//            spinner_items.add(new CheckableSpinnerAdapter.SpinnerItem<>(interface_Name, interface_Name));
           rb = new RadioButton(this);
            rb.setText(interface_Name);
            rb.setId(View.generateViewId());
            rg.addView(rb);

        }
//        CheckBox checkBoxAll = findViewById(R.id.checkBoxInterfaceAny);
//        checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked){
//                selected_Interfaces.clear();
//            }
//        });



//        CheckableSpinnerAdapter.AdapterCallback callback = selected_items -> {
//            Log.d("CheckedInterfaces",""+selected_items.size());
//            if(selected_items.size()==0){
//                ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(true);
//            }
//            else if (selected_items.size() == all_Interfaces.size()){
//                ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(true);
//            }
//            else ((CheckBox)findViewById(R.id.checkBoxInterfaceAny)).setChecked(false);
//        };
//        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter<>(this, headerText, spinner_items, selected_Interfaces,callback);
//        spinner.setAdapter(adapter);


        findViewById(R.id.start_tcpdump).setOnClickListener(v -> {
            if(Capture_Root.captureIsActive) {
                Log.e("Start","Cannot Start: Already running.");
                return;
            }
            captureDuration[0] = npvH.getValue()*60*60 + npvM.getValue()*60 + npvS.getValue();
            if (captureDuration[0] == 0){
                Log.e("Start","Cannot Start: Capture duration is 0.");
                return;
            }
            UpdateSettings();
            Capture_Root.Capture(getApplicationContext(),command+options,captureDuration[0]);

        });
        findViewById(R.id.stop_tcpdump).setOnClickListener(v -> Capture_Root.StopCapture());


    }
    public static String[] getNetworkInterfaces() throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec("su -c ifconfig -S").getInputStream()).useDelimiter("\\A");
        String[] lines = (s.hasNext() ? s.next() : "").split("\n");


        return lines;
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(() -> {
            npvH.smoothScrollToValue(0);
            npvM.smoothScrollToValue(2);
            npvS.smoothScrollToValue(0);
        },500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Capture_Root.CleanUp();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Capture_Root.CleanUp();
    }

    void UpdateSettings() {

        CheckBox tcp, udp, icmp,igmp;
        tcp = findViewById((R.id.checkBoxTcp));
        udp = findViewById((R.id.checkBoxUdp));
        icmp = findViewById((R.id.checkBoxIcmp));
        igmp = findViewById((R.id.checkBoxIgmp));

        int countChecks = 0;

        options = "";
        if (tcp.isChecked()) {
            options += " tcp";
            countChecks++;
        }
        if (udp.isChecked()) {
            if (countChecks > 0)
                options += " or udp";
            else {
                options += " udp";
            }
            countChecks++;
        }
        if (icmp.isChecked()) {
            if (countChecks > 0)
                options += " or icmp";
            else {
                options += " icmp";
            }
            countChecks++;
        }
        if (igmp.isChecked()) {
            if (countChecks > 0) {
                options += " or igmp";
            } else
                options += " igmp";
            //countChecks++;
        }

        options+=" -i " + captureInterface;
//        if(selected_Interfaces.size()>0){
//            options+=" -i ";
//            for(int i =0 ;i<selected_Interfaces.size();i++){
//                //options+=
//            }
//        }
//        else {
//            options+=" -i any";
//        }


    }
}
