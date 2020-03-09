package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;

public class SelectMethod extends AppCompatActivity {
    private Button button_root;
    private Button button_non_root;
    private ViewGroup rootView;
    private Fade mFade = new Fade(Fade.IN);

    LinearLayout linearLayout;
    CardView cview;
    WeakReference<Activity> activityWeakReference;

    protected void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.select_method);
        activityWeakReference = new WeakReference<Activity>(this);
        linearLayout = (LinearLayout) findViewById(R.id.LandingScrollLinear);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                final OvershootInterpolator interpolator = new OvershootInterpolator();
                v.animate().rotationYBy(360).setDuration(300).setInterpolator(new LinearInterpolator()).start();

                return false;
            }
        });
        findViewById(R.id.button_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.performLongClick();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new IntentRootCapture(activityWeakReference).execute();
                    }
                },300);

            }
        });
        findViewById(R.id.button_vpn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // fab.performLongClick();
                //view.postDelayed(new Runnable() {
                 //   @Override
                  //  public void run() {
                  //      new IntentRootCapture(activityWeakReference).execute();
                 //   }
               // },300);
                openActivity();
            }
        });

//        fab.setAnimation(AnimationUtils.loadAnimation(activityWeakReference.get(), R.anim.rotate360));

    }

    private void openActivity(){
        Intent intent = new Intent(this,PacketDisplayActivity.class);
        startActivity(intent);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        int childViewCount = linearLayout.getChildCount();
//        if (childViewCount > 0) {
//            int postDelay = 250;
//            for (int i = 0; i < childViewCount; i++) {
//                Handler handler = new Handler();
//                final int finalI = i;
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        linearLayout.getChildAt(finalI).setVisibility(View.VISIBLE);
//                        TransitionManager.beginDelayedTransition(linearLayout, mFade);
//                    }
//                }, postDelay * (i));
//            }
//        }
//    }

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
                Intent intent = new Intent(this.activityWeakReference.get(), CaptureActivity.class);
                activityWeakReference.get().startActivity(intent);
            }
            else {
                Toast.makeText(activityWeakReference.get(),"Root access not granted or root not available.",Toast.LENGTH_LONG).show();
            }


        }


    }

}
