package securityteam.ece.uowm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.lang.ref.WeakReference;

import static java.lang.Thread.sleep;

public class SelectMethod extends AppCompatActivity {
    private Button button_root;
    private Button button_non_root;
    private ViewGroup rootView;
    private Fade mFade;
    LinearLayout linearLayout;
    CardView cview;
    WeakReference<Activity> activityWeakReference;
    protected void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.select_method);
        activityWeakReference = new WeakReference<Activity>(this);
        linearLayout = (LinearLayout) findViewById(R.id.LandingScrollLinear);
//        linearLayout = (LinearLayout) findViewById(R.id.LandingScrollLinear);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        cview = (CardView) inflater.inflate(R.layout.custom_card_view, linearLayout, false);
//        // set item content in view
//        ((TextView)cview.findViewById(R.id.TextView)).setText(R.string.landing_welcome );
////        linearLayout.addView(cview);

        mFade = new Fade(Fade.IN);



    }

    @Override
    protected void onStart() {
        super.onStart();



        int childViewCount = linearLayout.getChildCount();
        if (childViewCount>0){
            int postDelay = 250;
            for (int i=0;i<childViewCount;i++){
                Handler handler = new Handler();
                final int finalI = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        linearLayout.getChildAt(finalI).setVisibility(View.VISIBLE);
                        TransitionManager.beginDelayedTransition(linearLayout,mFade);
                    }
                },postDelay*(i));
            }

        }

    }


        public void openActiviteMain () {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        public void openActiviteMethod2 () {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
