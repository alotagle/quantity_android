package com.mobile.dev.quantity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by Luis.Cariño on 19/01/2015.
 */
public class SplashActivity extends Activity {


    /**Duration of wait**/
    private final int SPLASH_DELAY_LENGTH = 2000;
    private ImageView mLogo;
    private TextView mTextView;



    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        setContentView(R.layout.splash);

        //get reference to UI elements
        mTextView = (TextView) findViewById(R.id.textViewName);
        mLogo = (ImageView) findViewById(R.id.splashscreen);


        //Create typeface for font using crateFromAsset
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        //set font to text view
        mTextView.setTypeface(typeface);

        /**
         * New handler to start delayed Main-Activity and finish this Activity
         *
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //create object animators for logo elements
                ObjectAnimator animator = ObjectAnimator.ofFloat(mLogo,"alpha",0f,1f);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(mTextView,"alpha",0f,1f);

                //create animator set in order to run both animators
                AnimatorSet animatorSet = new AnimatorSet();
                //set properties of animation
                animatorSet.playTogether(animator,animator2);
                animatorSet.setDuration(1500);
                animatorSet.start();

                //create intent for next Activity
                final Intent i = new Intent(getApplicationContext(),LoginActivity.class);

                //Check for current version of Android
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //start activity with transitions
                            Pair pair = new Pair((View)mLogo,"imageViewLogo");
                            Pair pair1 = new Pair((View)mTextView,"textViewName");
                            Bundle option = ActivityOptions.makeSceneTransitionAnimation(mActivity,pair,pair1).toBundle();
                            startActivity(i,option);
                            finish();
                        }
                    },3000);
                }else{
                        //start activity without transitions
                        startActivity(i);
                        finish();
                }




            }
        },SPLASH_DELAY_LENGTH);


    }




 }
