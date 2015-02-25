package com.mobile.dev.quantity.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mobile.dev.quantity.BuildConfig;

/**
 * Created by Luis.Cariño on 11/02/2015.
 * This class contains commons tools used within application
 */
public class QuantityDictionay {


    private static final String TAG = "QUANTITIY DEBUG LOG";


    public QuantityDictionay(){}


    public static  void debugLog(String message){
        if(BuildConfig.APP_DEBUG){ //checks for the value of BuildConfigField
            Log.d(TAG, message);
        }
    }


    /**
     * This method checks for active network connection
     * @param appContext : application context
     * @return true if valid connection is active, false otherwise
     */

    public static boolean checkNetworkConnection(Context appContext){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                appContext.getSystemService(appContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
