package com.mobile.dev.quantity;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Luis.Cariño on 20/01/2015.
 */
public class ParseHelper extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        parseInit();
    }

    public void parseInit(){
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "5hcj5JrkKa3bbwJZOZwiFO5f8k2a022qmaEYzr4S", "qYH1Z3YaCzq1vjlEOvYxGEWNtIS7PIDfVy3l9aKp");
    }
}
