package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static java.lang.Thread.sleep;

public class SelectMethod extends AppCompatActivity {
    private Button button_root;
    private Button button_non_root;
    private ViewGroup rootView;
    private Fade mFade = new Fade(Fade.IN);;
    LinearLayout linearLayout;
    CardView cview;
    WeakReference<Activity> activityWeakReference;

    protected void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.select_method);
        activityWeakReference = new WeakReference<Activity>(this);
        linearLayout = (LinearLayout) findViewById(R.id.LandingScrollLinear);
//        mFade =


        findViewById(R.id.button_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentRootCapture(activityWeakReference).execute();



            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        int childViewCount = linearLayout.getChildCount();
        if (childViewCount > 0) {
            int postDelay = 250;
            for (int i = 0; i < childViewCount; i++) {
                Handler handler = new Handler();
                final int finalI = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        linearLayout.getChildAt(finalI).setVisibility(View.VISIBLE);
                        TransitionManager.beginDelayedTransition(linearLayout, mFade);
                    }
                }, postDelay * (i));
            }
        }
    }

    static class IntentRootCapture extends AsyncTask<Void,Void,Boolean>{
        WeakReference<Activity> activityWeakReference = null;
        private Transition mFade;

        public IntentRootCapture(WeakReference<Activity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
            mFade = new Fade(Fade.IN);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Process p = Runtime.getRuntime().exec("su -c exit");
                p.waitFor();
                if(p.exitValue() != 0){
                    Log.e("ROOTCHECK","Root not granted or missing");
                    return false;
                }
               return true;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ROOTCHECK","Root not granted or missing");
                return false;

            }

        }

        @Override
        protected void onPostExecute(Boolean rootAccessGranted) {
            super.onPostExecute(rootAccessGranted);

            Log.e("OnPostExecute"," "+ rootAccessGranted);
            if(rootAccessGranted) {
                Intent intent = new Intent(this.activityWeakReference.get(), MainActivity.class);
                activityWeakReference.get().startActivity(intent);
            }
            else {
                Toast.makeText(activityWeakReference.get(),"Root access not granted or root not available.",Toast.LENGTH_LONG).show();
            }


        }


    }

}
